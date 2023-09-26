# cloud-ready-stock
A stock control application using latest SpringCloud features


## Uses
* Spring Boot 3.1.4
* Spring Cloud 2022.0.0
    - Netflix Eureka
    - OpenFeign
    - Api Gateway
* Java 17
* Swagger OpenAPI 3.0


## Running Instructions

* 1. Enter the stock-eureka-server project folder and run the application with the command: `mvn clean spring-boot:run`
* 2. Enter the stock-api-gateway project folder and run the application with the command: `mvn clean spring-boot:run`
* 3. Enter on each of the root-<app>-service and run the command: `mvn clean spring-boot:run`
* wait for about 30s for the applications to start and register themselves to the discovery server (Eureka) the you can test the APIs


## Documentation

* Category Service: http://localhost:8080/category-service/swagger-ui/index.html
* Product Service: http://localhost:8080/product-service/swagger-ui/index.html
* Stock & Transactions Service: http://localhost:8080/stock-service/swagger-ui/index.html




##### copyright(C) Romeu Franzoia - 2023