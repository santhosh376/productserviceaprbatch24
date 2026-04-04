## Product Service

A Spring Boot 3 RESTful API for managing products and categories, backed by MySQL and Flyway migrations. It demonstrates layered architecture with controllers, services, repositories, DTOs, and global exception handling.

### Tech stack

- **Language**: Java 21
- **Framework**: Spring Boot 3.3.2
- **Build tool**: Maven
- **Persistence**: Spring Data JPA / Hibernate
- **Database**: MySQL
- **Migrations**: Flyway (`src/main/resources/db/migration`)
- **Service discovery**: Spring Cloud Netflix Eureka Client (`spring-cloud-starter-netflix-eureka-client`)
- **HTTP client**: `@LoadBalanced` `RestTemplate` (FakeStore integration and Eureka-resolved calls to other services, such as the User Service)
- **Caching**: Redis (Spring Data Redis with `RedisTemplate`)
- **Testing**: JUnit 5, Spring Boot Test, MockMvc

### Project structure (high level)

- **`src/main/java/com/example/productservice`**
  - **`Productservice`**: Spring Boot entry point (`@SpringBootApplication`).
  - **`controllers`**: REST controllers — `ProductController` (`/products`), `SearchController` (`/search`), `HealthController` (`/health`).
  - **`services`**:
    - `ProductService`: service interface.
    - `ProductServiceDBImpl`: database-backed implementation used by `ProductController` via `@Qualifier("dbProductService")`.
    - `ProductServiceFakestoreImpl`: alternative implementation that integrates with `https://fakestoreapi.com` using `RestTemplate`.
    - `SearchService`: search with filters and sorting (`filteringService`, `sortingService` packages).
  - **`models`**: JPA entities (`BaseModel`, `Product`, `Category`, `Subcategory`).
  - **`repositories`**: Spring Data JPA repositories (`ProductRepository`, `CategoryRepository`) with derived queries and custom JPQL/native queries.
  - **`dtos`**: Request/response DTOs for products, FakeStore integration, and error responses.
  - **`configs`**: Application configuration (`ApplicationConfiguration` defines a `@LoadBalanced` `RestTemplate` bean for client-side load balancing against Eureka-registered services).
  - **`advices`**: Global exception handling (`ExceptionAdvices`).
- **`src/main/resources`**
  - **`application.properties`**: application name, datasource, JPA, Flyway, Redis, Eureka client, and server port (several values are supplied via environment variables).
  - **`db/migration`**: Flyway SQL migrations creating and evolving the schema.
- **`src/test/java/com/example/productservice/controllers`**
  - `ProductControllerTest`: controller unit test with mocked `ProductService`.
  - `ProductControllerMvcTest`: MockMvc-based tests for the `/products` endpoints.

### Database & migrations

The service uses MySQL as the primary data store and Flyway for schema management.

- **Migrations**: Located under `src/main/resources/db/migration`, for example:
  - `V1__.sql`: creates `product`, `category`, `subcategory`, and junction tables with relationships.
  - `V2__add_countofproducts.sql`: adds a `count_of_products` column to the `category` table.
- On application startup, Flyway runs pending migrations against the configured database.

#### Required database setup

1. **Create a MySQL database** (name must match the one in `application.properties` or your override):
  - Example from the current config: `productservice27july`.
2. **Create a database user** with permissions on that database:
  - Username is configured in `application.properties` (e.g. `dbuserproductservice24july`).
3. **Configure credentials**:
  - Either update `spring.datasource.username` and `spring.datasource.password` in `src/main/resources/application.properties`,
  - Or set the corresponding environment variables (e.g. `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`).

Once the datasource is configured, Flyway will create/update the schema the first time the app starts.

### Running the application locally

#### Prerequisites

- Java 21 installed.
- MySQL running and reachable with the configured URL and credentials.
- **Eureka server** running and reachable at the URL you set in `SERVICE_DISCOVERY_URL` (this service registers with Eureka and uses the registry to resolve other microservices by logical name).
- Maven wrapper (`mvnw`) is included in the project (or a system Maven installation).

#### Environment variables

The app reads several values from the environment (see `src/main/resources/application.properties`):

| Variable | Purpose |
|----------|---------|
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | MySQL JDBC URL and credentials |
| `SERVICE_DISCOVERY_URL` | Eureka server base URL (for example `http://localhost:8761/eureka/`) |
| `SERVER_PORT` | Port this service listens on |

Note: `application.properties` includes `productServiceType` for reference, but **`ProductController` selects the implementation with a fixed `@Qualifier`** (`dbProductService`). To use FakeStore instead, change the qualifier to `fakeStoreProductService` or introduce conditional wiring.

#### Steps

1. **Clone the repository**:

   ```bash
   git clone <this-repo-url>
   cd productservice
   ```

2. **Verify / adjust configuration**: set database and Eureka-related environment variables (see table above). You can still override individual Spring properties in `application.properties` if you prefer.

3. **Build and run** using the Maven wrapper:

   ```bash
   ./mvnw clean package
   ./mvnw spring-boot:run
   ```

   The application listens on the port given by `SERVER_PORT` (not the Spring Boot default unless you set it that way).

### Eureka service discovery
servicediscovery microservice repo link:
[Service Discovery (Eureka)](https://github.com/santhosh376/servicediscovery)

This service is a **Eureka client**: it registers itself under the name `productservice` (`spring.application.name`) and **fetches the registry** from Eureka (`eureka.client.register-with-eureka=true`, `eureka.client.fetch-registry=true`). The Eureka server URL is configured with `eureka.client.service-url.defaultZone=${SERVICE_DISCOVERY_URL}`.

That setup allows:

- Other components (API Gateway, other microservices) to discover **this** service by name.
- This service to call **other** Eureka-registered applications using their logical host names in URLs (see below).

### Inter-service communication: User Service
userService microservice repo link:
[User Service Authorization](https://github.com/santhosh376/userService)

The User Service is expected to register with the **same** Eureka server under the logical name **`userService`**. Product Service uses the shared, **`@LoadBalanced`** `RestTemplate` bean so URLs like `http://userService/...` are resolved through Eureka (client-side load balancing when multiple instances exist).

**Where it is used in this codebase**

- **`POST /products`** sends the `Authorization` request header to **User Service** for validation: it calls `http://userService/auth/validate?token=<token>` and expects a `Boolean`. If the result is not authenticated, the controller throws a `RuntimeException` (`"STOP HERE"`).

Ensure User Service exposes that validation endpoint and is registered in Eureka as `userService` (or align the URL with your actual Eureka application name).

### API Gateway integration
API Gateway microservice repo link:
[API Gateway](https://github.com/santhosh376/APIGateway)

The **API Gateway** is typically a separate Spring Cloud Gateway (or similar) application that **also registers as a Eureka client**. Gateway routes are often defined with targets such as `lb://productservice`, so traffic enters through the gateway, which looks up **productservice** in Eureka and forwards to a healthy instance.

You can **verify end-to-end gateway behavior** by invoking Product Service routes **through the gateway** (for example `/products`, `/products/{id}`, or other exposed paths), confirming that discovery, routing, and load balancing work together. The gateway project is not part of this repository; this service only needs to register with Eureka and expose its REST API as documented below.

### API overview

#### Product API (`ProductController`)

Endpoints below are rooted at **`/products`**.

- **GET `/products`**
  - **Description**: Returns all products from the database via `dbProductService`.
  - **Response**: A DTO wrapping a list of product representations (see `GetAllProductsResponseDto` / `GetProductDto`).

- **GET `/products/{id}`**
  - **Description**: Fetch a single product by ID using the `dbProductService` implementation.
  - **Success**: Returns a `GetProductResponseDTO` containing the product.
  - **Error handling**:
    - `id < 0` results in a `ProductNotFoundException` wrapped in a runtime exception.
    - `id == 0` throws `RuntimeException("Something went wrong")`.
    - `ExceptionAdvices` converts `RuntimeException` into a JSON `ErrorResponseDto` with `status` and `message` fields.

- **POST `/products`**
  - **Description**: Creates a new product.
  - **Headers**: **`Authorization`** — forwarded to User Service for validation (see *Inter-service communication: User Service*).
  - **Request body**: `CreateProductRequestDto` (includes product fields such as `title`, `description`, `price`, `imageUrl`, and category info).
  - **Behavior**:
    - Validates the token via Eureka-resolved **`userService`** (`/auth/validate`).
    - `ProductServiceDBImpl` ensures the category exists (creating it if necessary) and saves the product.
  - **Response**: `CreateProductResponseDto` with the persisted product data.

- **PATCH `/products/{id}`**
  - **Description**: Partially updates an existing product (title, description, price, category).
  - **Request body**: `CreateProductDto` (fields are optional; only non-null fields are applied).
  - **Behavior**:
    - `ProductServiceDBImpl.partialUpdateProduct` looks up the product, updates provided fields, and saves it.
  - **Response**: `PatchProductResponseDto` with the updated product.

- **PUT `/products/{id}`** and **DELETE `/products/{id}`**
  - Currently stubbed out in `ProductController` and return an empty `Product` instance.
  - Intended for full replace and delete operations; their implementations can be added following the patterns already used in the service layer.

#### Search API (`SearchController`)

- **GET `/search/`**
  - **Description**: Text search on product titles with optional filters, sorting, and pagination.
  - **Query parameters**: `query`, `filters` (list of `FilterDto`), `sortBy` (`SortingCriteria`), `pageNumber`, `pageSize`.
  - **Behavior**: Uses `SearchService` with `FilterFactory` / `SorterFactory` over repository results.
  - **Response**: `SearchResponseDto` containing a page of `GetProductDto` items.

- **GET `/search/byCategory`**
  - Declared in `SearchController`; implementation currently returns `null` (placeholder).

#### Health check (`HealthController`)

- **GET `/health`**
  - Returns the plain string **`OK`**. Used for load balancer / Elastic Beanstalk health checks (see *Deployment*).

### Service implementations

- **`ProductServiceDBImpl` (`@Service("dbProductService")`)**
  - Uses `ProductRepository` and `CategoryRepository` to interact with the MySQL database.
  - Handles:
    - Creating products and categories (with cascade and relationship management).
    - Fetching all products.
    - Partial updates of product fields.
    - Fetching a product by ID with proper `ProductNotFoundException` handling.

- **`ProductServiceFakestoreImpl` (`@Service("fakeStoreProductService")`)**
  - Integrates with `https://fakestoreapi.com/products` via `RestTemplate`.
  - Demonstrates:
    - Mapping between internal `Product` model and FakeStore request/response DTOs.
    - Creating and fetching products from an external API.
    - Redis-backed read-through caching for `getProductById`.
  - Not wired into `ProductController` by default; inject via `@Qualifier("fakeStoreProductService")` if you want the FakeStore-backed implementation.

### Redis caching usage

Redis is used to cache product responses when the FakeStore-backed service is enabled.

- **Connection configuration**
  - Redis host and port are configured in `src/main/resources/application.properties`:
    - `spring.data.redis.host=localhost`
    - `spring.data.redis.port=6379`
- **Redis template setup**
  - `ApplicationConfiguration` defines a `RedisTemplate<String, Object>` bean.
  - String serializers are used for keys/hash keys.
  - `GenericJackson2JsonRedisSerializer` is used for values/hash values so `Product` objects are stored as JSON and can be safely deserialized.
- **Cache strategy in service**
  - In `ProductServiceFakestoreImpl#getProductById`, the app first checks Redis hash key `PRODUCTS` with field `PRODUCT_<id>`.
  - On a **cache hit**, the product is returned directly from Redis.
  - On a **cache miss**, the app fetches product data from FakeStore API and stores the mapped `Product` in Redis before returning it.
  - This is a manual read-through caching approach that reduces repeated external API calls for the same product ID.
- **How to run locally**
  - Start Redis on your machine (default port `6379`) before running the Spring Boot app.
  - Keep `spring.data.redis.*` values aligned with your local/container Redis instance.

### Global error handling

- `ExceptionAdvices` uses `@ControllerAdvice` to handle exceptions across controllers:
  - **`RuntimeException`**: mapped to an `ErrorResponseDto` containing `status` and `message`.
  - **`Exception`** (fallback): returns a simple `"something went wrong"` message.

This centralizes error responses and keeps controller methods focused on happy-path logic.

### Testing

You can run the tests with:

```bash
./mvnw test
```

Key tests:

- **`ProductControllerTest`**
  - Uses `@SpringBootTest` with a mocked `dbProductService`.
  - Verifies that `getProductById`:
    - Returns the expected DTO when a valid ID is passed.
    - Throws a `RuntimeException` with message `"Something went wrong"` when called with `id = 0L`.

- **`ProductControllerMvcTest`**
  - Uses `@WebMvcTest(ProductController.class)` and `MockMvc` to test:
    - `GET /products` returns the expected DTO structure.
    - `POST /products` creates a product via the mocked `ProductService` and returns the expected JSON.


## Deployment

The Product Service application was deployed on AWS using Elastic Beanstalk and connected to a MySQL database hosted on AWS RDS. This deployment demonstrates how a Spring Boot application can be packaged, deployed, and made accessible through a cloud environment.

---

### Infrastructure Used

The following AWS services were used during deployment:

- **AWS Elastic Beanstalk** – Application deployment and environment management
- **AWS EC2** – Compute instance running the application
- **AWS RDS (MySQL)** – Managed relational database service
- **Elastic Load Balancer** – Routes incoming traffic to the application instance
- **Nginx Reverse Proxy** – Handles HTTP requests and forwards them to the Spring Boot application

---

### Deployment Architecture

The deployed architecture follows a standard backend cloud setup.
Internet   
↓  
Elastic Load Balancer    
↓  
EC2 Instance (Elastic Beanstalk Environment)    
↓  
Spring Boot Application (Port 5000)  
↓  
AWS RDS MySQL Database (Port 3306)


---

### Build Process

The Spring Boot application was packaged into an executable JAR using Maven.
mvn clean package -DskipTests


This command generated the deployable artifact located at:
target/productservice-0.0.1-SNAPSHOT.jar


This JAR file was uploaded to the Elastic Beanstalk environment for deployment.

---

### Deployment Steps

1. Created an **Elastic Beanstalk Application** in AWS.
2. Selected **Java Corretto platform** for running the Spring Boot application.
3. Uploaded the generated **JAR file** to Elastic Beanstalk.
4. Elastic Beanstalk automatically provisioned the required infrastructure including:
- EC2 instance
- Load balancer
- Nginx configuration
5. Configured environment variables and database connection properties.
6. Connected the application to **AWS RDS MySQL** database.
7. Configured a health check endpoint to monitor application status.

---

### Health Check Configuration

A health check endpoint was created in the application:
/health

Elastic Beanstalk periodically checks this endpoint to verify that the application is running correctly.  
If the endpoint returns **HTTP 200**, the environment status becomes **Healthy (GREEN)**.

---

### Security Configuration

Security groups were configured to control network access between services.

- The **EC2 instance** accepts HTTP requests from the internet.
- The **RDS database** allows MySQL access only from the EC2 instance security group.
- Direct public access to the database is disabled.

This ensures that the database remains secure and accessible only through the application server.

---

## AWS Security Groups Used in Deployment

Security groups were configured to control network access between the application server and the database.

The following security groups were used:

- **Elastic Beanstalk EC2 Security Group** – Allows HTTP traffic to the application server.
- **Elastic Load Balancer Security Group** – Handles incoming internet requests.
- **RDS Security Group** – Allows MySQL connections from the EC2 instance only.

This configuration ensures that the database is not publicly accessible and can only be accessed by the application server.

### Deployment Proof

The following screenshots demonstrate successful deployment.

**1. Elastic Beanstalk Environment Status**

![Elastic Beanstalk Environment](images/elasticbeanstalk-environment.png)

---

**2. AWS RDS Database Instance**

![RDS Database](images/rds-productservice-db1.png)
![RDS Endpoints](images/rds-productservice-db1-Endpoints.png)

---
**3. EC2 Instance Running**

![EC2 Instance](images/ec2-instance-productservicedb1.png)

---

**4. Security Groups Overview**

![Security Groups](images/security-groups.png)

---

**5 Custom Security Group for RDS (Port 3306)**

![Custom Security Group](images/custom-security-group-for-RDS.png)

---


### Notes

To avoid unnecessary AWS charges, the Elastic Beanstalk environment and related resources were terminated after testing the deployment. Screenshots included above serve as proof of successful deployment.



### Notes & next steps

- **PUT/DELETE implementations**: The `replaceProduct` and `deleteProductById` methods in `ProductController` are currently placeholders and can be implemented using `ProductRepository` operations.
- **Configurable service backend**: `ProductController` injects `dbProductService`. Switch the `@Qualifier` to `fakeStoreProductService` (or use profiles) to use the FakeStore-backed implementation for product CRUD.
- **Validation & security**: `POST /products` delegates token checks to User Service; broader validation, authentication schemes, and authorization rules can be extended as needed.
- **`/search/byCategory`**: Endpoint stub — implement or remove when finalizing the search API.

