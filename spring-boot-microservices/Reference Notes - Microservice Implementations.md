## Micrsoservices Implemetations Details
@author Heeren

 **Topics Covered**
--------------
1. [**Task-1.** Rest Template Implementation (Microservice-01, Microservice-02)](#1-task-1-rest-template-implementation-microservice-01-microservice-02)  
2. [**Task-2.** Implementing H2-Console Database and Exposing REST Endpoint for Currency Exchange (currency-exchange-service)](#2-task-2-implementing-h2-console-database-and-exposing-rest-endpoint-for-currency-exchange-currency-exchange-service)  
3. [**Task-3.** Using Rest Template: currency-conversion-service (Port 8100) Calling currency-exchange-service (Port 8000)](#3-task-3-using-rest-template-currency-conversion-service-port-8100-calling-currency-exchange-service-port-8000)  
4. [**Task-4.** Implementing Feign Client: currency-conversion-service (Port 8100) Calling currency-exchange-service (Port 8000)](#4-task-4-implementing-feign-client-currency-conversion-service-port-8100-calling-currency-exchange-service-port-8000)
 
--------------

**Task-1. Rest Template Implementation (Microservice-01,Microservice-02)**
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
**Task-2. Implementing h2-console database and exposing rest endpoint for currency exchange (3.currency-exchange-service)**  
Disable eureka server for this implementation - 
![image](https://github.com/user-attachments/assets/bc8714bd-2be5-4ff7-bf93-3509895bf7f3)
![image](https://github.com/user-attachments/assets/b8e8eae3-c731-4b8d-b735-82b2b11b6bc6)

**Flow Diagram -**
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
**Task-3. Using Rest Template 4.currency-conversion-service (Running on port 8100) Calling 3.currency-exchange-service (Running on port 8000)**
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
**Task-4. Implementing Feign Client, 5.currency-conversion-service (Running on port 8100) Calling 3.currency-exchange-service (Running on port 8000)**

- 5.currency-conversion-service (Microservice1) is client class which is consuming 3.currency-exchange-service (Microservice 2).
 - 5.currency-conversion-service (Microservice1) having a proxy service implemented feign client to simplifies REST APIs call of existing 3.currency-exchange-service (Microservice 2).
 - 5.currency-conversion-service (Microservice1) getting rates from 3.currency-exchange-service (Microservice 2) and then calculating total conversion amount basis on quantity.

```java
+-------------------------------------------------------------+
|     Currency Conversion Feign Microservice (Port 8100)      |
|  spring.application.name=currency-conversion-service        |
+-------------------------------------------------------------+
| @SpringBootApplication + @EnableFeignClients                |
|                                                             |
| @RestController                                             |
| CurrencyConversionController                                |
|                                                             |
| ===> GET /currency-converter-feign/from/{from}/to/{to}/     |
|               quantity/{quantity}                           |
|                                                             |
| ---> convertCurrencyFeign(from, to, quantity)               |
|       ---> proxy.retrieveExchangeValue(from, to)           |
|              |                                              |
|              ===> FeignClient auto-generated HTTP call      |
|                   to currency-exchange-service              |
|                                                             |
+-------------------------------------------------------------+

                            |
                            |  (Feign REST Call)
                            V

+-------------------------------------------------------------+
|           Currency Exchange Microservice (Port 8000)        |
|         spring.application.name=currency-exchange-service   |
+-------------------------------------------------------------+
| @RestController                                             |
| CurrencyExchangeController                                  |
|                                                             |
| ===> GET /currency-exchange/from/{from}/to/{to}             |
|                                                             |
| ---> repository.findByFromAndTo(from, to)                   |
|       ---> [H2 Database: ExchangeValue Table]               |
|                                                             |
| ---> Returns ExchangeValue (id, from, to, conversionRate,   |
|                              port)                          |
+-------------------------------------------------------------+

                            |
                            |  (Feign Response)
                            V

+-------------------------------------------------------------+
|   Currency Conversion Feign Microservice (Continued)        |
+-------------------------------------------------------------+
| <--- receives CurrencyConversionBean from proxy             |
|                                                             |
| ---> Calculates totalAmount = quantity * conversionMultiple |
|                                                             |
| ===> Final Response to Client                               |
|      (id, from, to, quantity, conversionMultiple,           |
|       totalAmount, port from exchange-service)              |
+-------------------------------------------------------------+
```
---
**Task-5. Implementing Spring clound load balancer, 6.currency-conversion-service (Running on port 8100) Calling 3.currency-exchange-service Running 2 instances       intance 1 - (Running on port 8085)     
intance 2 - (Running on port 8085)**    

 📌 This project implements and Setting up **client side load balancing** with Spring cloud load balancer.          
- Prerequisite - execute multiple instances of currency exchange service.     
- Use following running configurations in Ecilipse/STS for currency exchange service -     
![image](https://github.com/user-attachments/assets/32d4466f-b8fb-4739-becd-73f264d54d48)   

![image](https://github.com/user-attachments/assets/b9af196c-58c5-4e94-8ad2-00c99c8aa2f6)   

```java
+-------------------------------------------------------------+
|     Currency Conversion Service (Port 8100)                 |
|     spring.application.name=currency-conversion-service     |
+-------------------------------------------------------------+
| @SpringBootApplication + @EnableFeignClients                |
|                                                             |
| @RestController                                             |
| CurrencyConversionController                                |
|                                                             |
| ===> GET /currency-converter/from/{from}/to/{to}/           |
|                quantity/{quantity}                          |
|                                                             |
| ---> convertCurrencyFeign(from, to, quantity)               |
|       ---> proxy.retrieveExchangeValue(from, to)           |
|              |                                              |
|              ===> Feign Client                              |
|              |                                              |
|              ===> Spring Cloud LoadBalancer                 |
|                      |                                      |
|                      V                                      |
|       +--------------------Load Balancer------------------+ |
|       | Resolves "currency-exchange-service" to 2 instances| |
|       |                                                    | |
|       | - localhost:8085 (currency-exchange-1)             | |
|       | - localhost:8086 (currency-exchange-2)             | |
|       +----------------------------------------------------+ |
|                                                             |
+-------------------------------------------------------------+
                            |
                            |  (Load-balanced Feign REST call)
                            V

+-------------------------------------------------------------+
|           Currency Exchange Microservice (One of):          |
|             [Port 8085] OR [Port 8086]                      |
|         spring.application.name=currency-exchange-service   |
+-------------------------------------------------------------+
| @RestController                                             |
| CurrencyExchangeController                                  |
|                                                             |
| ===> GET /currency-exchange/from/{from}/to/{to}             |
|                                                             |
| ---> Returns ExchangeValue (id, from, to, conversionRate,   |
|                              port)                          |
+-------------------------------------------------------------+

                            |
                            |  (Feign Response)
                            V

+-------------------------------------------------------------+
|   Currency Conversion Service (Continued - Port 8100)       |
+-------------------------------------------------------------+
| <--- receives CurrencyConversionBean from proxy             |
|                                                             |
| ---> Calculates totalAmount = quantity * conversionMultiple |
|                                                             |
| ===> Final Response to Client                               |
|      (id, from, to, quantity, conversionMultiple,           |
|       totalAmount, port of instance used)                   |
+-------------------------------------------------------------+
```
---
**Task-6. Implementing Eureka Naming Server**    
📌 Following services used -    
**Service 1** - 7.eureka-naming-server    
**Service 2** - 8.currency-conversion-service_dynamicloadbalancing    
**Service 3** - 3.currency-exchange-service    

🔁 **Execution Flow Summary**
- eureka-naming-server starts first.
- Both currency-exchange-service instances (8085 and 8086) register with Eureka.
- currency-conversion-service also registers itself with Eureka.
- Spring Cloud LoadBalancer + Eureka is enabled in currency-conversion-service

🔁 **Eureka server flow diagrams**        

![test](https://github.com/user-attachments/assets/c8db0e66-3c21-4142-89a9-b0c063bf0c04)
![eureka](https://github.com/user-attachments/assets/5162fc9c-4ed1-469d-904d-0c4a71b72d8c)

```java
                         
                      +-----------------------------+
                      |     Eureka Naming Server    |
                      |        (Port 8761)          |
                      +--------------+--------------+
                                     ^
                                     |
            Service Registry         | Registers & fetches instances
                                     |
          +--------------------------+--------------------------+
          |                                                     |
+----------------------------+                     +----------------------------+
|  Currency Exchange Service |                     |  Currency Exchange Service |
|        (Port 8085)         |                     |        (Port 8086)         |
+----------------------------+                     +----------------------------+
|  Registered with Eureka    |                     |  Registered with Eureka    |
+----------------------------+                     +----------------------------+

                                     ▲
                                     |
                Load Balanced        | Uses Eureka + Spring Cloud LoadBalancer
                                     |
                      +--------------+--------------+
                      | Currency Conversion Service |
                      |        (Port 8100)          |
                      +-----------------------------+
                      | Feign Client + LoadBalancer |
                      |                             |
                      | --> Ask Eureka for "currency-exchange-service"
                      | --> Eureka returns list: [8085, 8086]
                      | --> LoadBalancer chooses one instance
                      | --> Sends REST call to selected instance
                      +-----------------------------+
```
![test](https://github.com/user-attachments/assets/dae1df48-1bbf-4793-944c-cf7a87db7909)

---

**Task-7. Spring Cloud Config Server Local Git Setup -**    
📌 Following service used -    
**Service** - 9.1.spring-cloud-config-server-localGit     

🔁 **Implementation Steps**     
**Step 1** - Config server project setup (9.1.spring-cloud-config-server-localGit).    
**Step 2** - Create local drive folder and place there - configuration files of different enviornments.  
EG. -    
![Capture](https://github.com/user-attachments/assets/7c9813b2-46fc-4cfe-8ce8-f3ea9e9a5427)

**Step 3** - Checkin local drive folder's files into local git -    
![Capture](https://github.com/user-attachments/assets/3a1f1411-ca23-4a95-9d85-23ab3d97e93a)      

**Step 4** - Connect spring cloud config server with local git repo directory -    
![Capture](https://github.com/user-attachments/assets/117e9280-04a4-4ad1-b97f-34aabedffadc)
     
**Step 5** - Run spring cloud config server and test for different environment 
```java
http://localhost:8888/propertyfilename/default  
http://localhost:8888/propertyfilename/dev
http://localhost:8888/propertyfilename/qa
```
**Expected response -**     
![Capture](https://github.com/user-attachments/assets/22c6d044-61d4-4062-bd59-4b401042e57c)


🔁 **Spring cloud config server flow diagram** 
                                +-----------------------------------+
                                |      Local Git Repository         |
                                |  file:///E:/.../local-drive-folder|
                                +-----------------------------------+
                                             ▲
                                             |
                            (Loads property files at startup)
                                             |
+--------------------------------------------|--------------------------------------------+
|                                  Spring Cloud Config Server                             |
|                          (http://localhost:8888, Port: 8888)                            |
|  spring.application.name=spring-cloud-config-server                                     |
|  spring.cloud.config.server.git.uri=file:///...                                         |
|                                                                                        |
|  @EnableConfigServer                                                                   |
|                                                                                        |
|  ➤ Serves configuration properties to client services                                  |
|    - http://localhost:8888/{application}/{profile}                                     |
|    - Example: http://localhost:8888/currency-conversion-service/default                |
|                                                                                        |
+--------------------------------------------|--------------------------------------------+
                                             |
                                (REST API: GET config by app/profile)
                                             |
                  ----------------------------------------------------------------
                  |                           |                                 |
+------------------------+     +-------------------------------+     +-------------------------------+
|  currency-exchange-    |     |  currency-conversion-service  |     |  any other Spring Boot client |
|  service (Port 8000/82)|     |  (Port 8100)                   |     | app (e.g., limits-service)   |
+------------------------+     +-------------------------------+     +-------------------------------+
```
---

