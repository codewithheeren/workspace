## Micrsoservices Implemetations Details
@author Heeren

 **Topics Covered**
--------------
1. [Java Features and Compilation, Execution Architecture of Java Program](#1-java-features-and-compilation-execution-architecture-of-java-program)       
2. [Types of Class Loaders](#2-types-of-class-loaders)       
3. [Variables and Data Types](#3-variables-and-data-types)
4. [IDE and JDK Installation](#4-ide-and-jdk-installation)
5. [First Java Program](#5-first-java-program)      
6. [OOPS Fundamentals](#6-oops-fundamentals)
7. [Data Shadowing and Data Hiding](#7-data-shadowing-and-data-hiding)
8. [Assignment](#8-assignment)
--------------

**1. Rest Template Implementation (Microservice-01,Microservice-02)**
```java
+----------------------------+                           +-----------------------------+
|   Microservice-01 (8282)  |                            |   Microservice-02 (8080)    |
|   [EmployeeService.java]  |                            |   [EmployeeController.java] |
+----------------------------+                            +-----------------------------+
|  - createEmployee()        |                            |                             |
|   ===> POST /api/v1/employees    --------------->        createEmployee(@RequestBody) |
|                           |                             |                             |
| - getEmployeeById(Long id)|                             |                             |
|   ===> GET /api/v1/employees/{id} -------------->         getEmployeeById(@PathVar)   |
|                           |                             |                             |
| - getAllEmployees()       |                             |                             |
|   ===> GET /api/v1/employees       --------------->       getAllEmployees()           |
|                           |                             |                             |
| - updateEmployee(Long id) |                             |                             |
|   ===> PUT /api/v1/employees/{id}  ----------------->     updateEmployee(@PathVar)    |
|                            |                            |                             |
| - deleteEmployee(Long id)  |                            |                             |
|   ===> DELETE /api/v1/employees/{id} ---------------->     deleteEmployee(@PathVar)   |
+----------------------------+                            +-----------------------------+
```

---
**2. Implementing h2-console database and exposing rest endpoint for currency exchange (3.currency-exchange-service)**    
```java
+-----------------------------------------------------------+
|               Currency Exchange Microservice              |
|                  (Port: 8000, Spring Boot)                |
+-----------------------------------------------------------+
| @RestController                                           |
| CurrencyExchangeController                                |
|                                                           |
|  ===> GET /currency-exchange/from/{from}/to/{to}          |
|       -- called from REST client (browser/Postman)        |
|                                                           |
|        ---> retrieveExchangeValue(String from, String to) |
|             |                                             |
|             |                                             |
|             ---> repository.findByFromAndTo(from, to)     |
|                          |                                |
|                          |                                |
|                          ---> [H2 DATABASE]               |
|                                ExchangeValue Table        |
|                                                           |
+-----------------------------------------------------------+
| @Component                                                |
| ExchangeValueDataLoader laoding sample data in db         |
|                                                           |
|  ===> On app startup:                                     |
|       ---> Inserts sample data to H2 DB:                  |
|           - USD -> INR : 65                               |
|           - EUR -> INR : 75                               |
|           - AUD -> INR : 25                               |
|                                                           |
|  ---> System.out.println("Exchange values inserted!")     |
+-----------------------------------------------------------+
```
---
**3. Using Rest Template 4.currency-conversion-service (Running on port 8100) Calling 3.currency-exchange-service (Running on port 8000)**
```java
+-------------------------------------------------------------+
|          Currency Conversion Microservice (Port 8100)       |
|         spring.application.name=currency-conversion-service |
+-------------------------------------------------------------+
| @RestController                                             |
| CurrencyConversionController                                |
|                                                             |
| ===> GET /currency-converter/from/{from}/to/{to}/quantity/{q} 
|      -- called by REST Client (Postman/Browser)             |
|                                                             |
|    ---> convertCurrency(from, to, quantity)                 |
|           |                                                 |
|           |                                                 |
|           ---> RestTemplate.getForEntity(                   |
|                   "http://localhost:8000/currency-exchange/|
|                        from/{from}/to/{to}",                |
|                        CurrencyConversionBean.class)        |
|                          |                                  |
|                          |                                  |
|                          ===> HTTP CALL to PORT 8000        |
|                                                             |
+-------------------------------------------------------------+

                            |
                            |  (REST Call)
                            V

+-------------------------------------------------------------+
|          Currency Exchange Microservice (Port 8000)         |
|         spring.application.name=currency-exchange-service   |
+-------------------------------------------------------------+
| @RestController                                             |
| CurrencyExchangeController                                  |
|                                                             |
| ===> GET /currency-exchange/from/{from}/to/{to}             |
|                                                             |
|    ---> repository.findByFromAndTo(from, to)                |
|           |                                                 |
|           ---> [H2 Database: ExchangeValue Table]           |
|                                                             |
|    ---> Response (id, from, to, conversionMultiple, port)   |
+-------------------------------------------------------------+

                            |
                            |  (Response returned)
                            V

+-------------------------------------------------------------+
|          Currency Conversion Microservice (Continued)       |
+-------------------------------------------------------------+
| <--- receives CurrencyConversionBean                        |
|                                                             |
| ---> Calculates totalAmount = quantity * conversionMultiple |
|                                                             |
| ===> Final Response to Client                               |
|      (id, from, to, quantity, conversionMultiple,           |
|       totalAmount, port from exchange-service)              |
+-------------------------------------------------------------+
```
---

## 8. Assignment   

**Task 1:** Implement a class with a static variable to count the number of instances and then Create multiple instances of this class and print the count.

**Task 2:** Develop a Java class to represent a library, with a static data member to store the total number of books in the library and an instance data member to store the book's title. Implement a static method to update the total count when a new book is added.

**Task 3:** Implement a Java class to manage a shopping cart, with an instance data member to store the items in the cart and instance methods to add, remove, and calculate the total price of the items.

**Task 4:** Develop a Java program to process a list of employees. Use instance methods to calculate their salary based on after 10% of tax deduction.

---
