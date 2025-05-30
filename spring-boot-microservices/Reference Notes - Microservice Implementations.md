## Micrsoservices Implemetations Details
@author Heeren

 **Topics Covered**
--------------
## 📘 Index

1. [**Task-1.** Rest Template Implementation (Microservice-01, Microservice-02)](#task-1-rest-template-implementation-microservice-01microservice-02)  
2. [**Task-2.** Implementing H2-Console Database and Exposing REST Endpoint for Currency Exchange (currency-exchange-service)](#task-2-implementing-h2-console-database-and-exposing-rest-endpoint-for-currency-exchange-3currency-exchange-service)  
3. [**Task-3.** Using Rest Template: currency-conversion-service (Port 8100) Calling currency-exchange-service (Port 8000)](#task-3-using-rest-template-4currency-conversion-service-running-on-port-8100-calling-3currency-exchange-service-running-on-port-8000)  
4. [**Task-4.** Implementing Feign Client: currency-conversion-service (Port 8100) Calling currency-exchange-service (Port 8000)](#task-4-implementing-feign-client-5currency-conversion-service-running-on-port-8100-calling-3currency-exchange-service-running-on-port-8000)  
5. [**Task-5.** Implementing Spring Cloud Load Balancer: 6.currency-conversion-service calling 3.currency-exchange-service with 2 instances](#task-5-implementing-spring-cloud-load-balancer-6currency-conversion-service-running-on-port-8100-calling-3currency-exchange-service-running-2-instances---intance-1---running-on-port-8085-----intance-2---running-on-port-8085)  
6. [**Task-6.** Implementing Eureka Naming Server](#task-6-implementing-eureka-naming-server)  
7. [**Task-7.** Spring Cloud Config Server Local Git Setup](#task-7-spring-cloud-config-server-local-git-setup--)  
8. [**Task-8.** Spring Cloud Config Server Remote Git Setup](#task-8-spring-cloud-config-server-remote-git-setup--)  
9. [**Assignment**](#assignment)
 
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
- 9.1.spring-cloud-config-server-localGit     

🔁 **Implementation Steps** -          
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


🔁 **Spring cloud config server flow diagram for Local git Repository**     
```java
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
**Task-8. Spring Cloud Config Server Remote Git Setup -**    
📌 Following service used -    
- 9.2.spring-cloud-config-server-remoteGit     

🔁 **Implementation Steps** -          
**Step 1** - Config server project setup (9.2.spring-cloud-config-server-remoteGit).        
**Step 2** - Create global repository with configuration files of different enviornments. or else push push configuration files from local repository to global.   
EG. -    
![Capture](https://github.com/user-attachments/assets/ff1258cf-be94-4546-9a0e-bba6122b6e15)   

**Step 3** - Connect spring cloud config server with remote git repo directory -    
![Capture](https://github.com/user-attachments/assets/07555f17-4ce5-4c13-a1ab-c50deda17407)
     
**Step 4** - Run spring cloud config server and test for different environment 
```java
http://localhost:8888/application/default
http://localhost:8888/application/dev
http://localhost:8888/application/qa
```
![Capture2](https://github.com/user-attachments/assets/6f25916b-fd07-4c31-9367-304aa3415b56)
![Capture](https://github.com/user-attachments/assets/58187859-c0d1-46cb-b323-2f966a7847ee)

**Expected response -**     
For Dev profile , default and dev profile properties are getting as response.
![Capture](https://github.com/user-attachments/assets/28520742-94e4-46f4-8196-5bdc117a99e5)

🔁 **Spring cloud config server flow diagram for Remote git Repository**     
```java
                           +---------------------------------------------------+
                           |             GitHub Remote Repository              |
                           | https://github.com/codewithheeren/workspace.git   |
                           | ➤ Folder: spring-boot-microservices               |
                           |     - application.properties                      |
                           |     - application-dev.properties                  |
                           |     - application-qa.properties                   |
                           | ➤ Branch: main                                    |
                           +---------------------------------------------------+
                                            ▲
                                            |
                           Loads config from branch: main, path: spring-boot-microservices
                                            |
+-------------------------------------------|-------------------------------------------+
|                               Spring Cloud Config Server                              |
|                       (http://localhost:8888, Port: 8888)                             |
|                                                                                       |
| spring.application.name = spring-cloud-config-server                                  |
| spring.cloud.config.server.git.uri = https://...workspace.git                         |
| spring.cloud.config.server.git.searchPaths = spring-boot-microservices                |
| spring.cloud.config.server.git.default-label = main                                   |
|                                                                                       |
|  ➤ Serves configuration via REST:                                                     |
|    - http://localhost:8888/application/default                                        |
|    - http://localhost:8888/application/dev                                            |
|    - http://localhost:8888/application/qa                                             |
+-------------------------------------------|-------------------------------------------+
                                            |
                         (Clients fetch configs on startup or refresh)
                                            |
          ------------------------------------------------------------------
          |                |                       |                       |
+----------------+  +--------------------+  +--------------------+  +----------------------+
| Dev Environment|  | QA Environment     |  | Default Environment|  | Other Microservices |
| (Port 2000)    |  | (Port 3000)        |  | (Any fallback)     |  | as needed           |
+----------------+  +--------------------+  +--------------------+  +----------------------+
| spring.application.name=application                            |
| spring.profiles.active=dev / qa / default                      |
| spring.config.import=optional:configserver:http://localhost:8888 |
|                                                                 |
| ➤ Fetches from:                                                 |
|   - /application-dev.properties (for dev)                       |
|   - /application-qa.properties (for qa)                         |
|   - /application.properties (for default)                       |
+-----------------------------------------------------------------+

```
---
**Task-9. Integrate microservices with spring cloud config server -**     
**Assignment**
**This Assignmet need to be done with following steps-**
- Create a Spring Cloud Config Server that reads .properties files (for default, dev, and qa) from a Git repository or local folder.    
- Build a Spring Boot microservice (client) that connects to the config server and reads these properties dynamically basis on activated profile.     
- Create a simple REST endpoint in client microservice to return the loaded configuration values.         
- Test the application by running it with different profiles (dev, qa, or default) to confirm the correct config values are loaded.
---

**Task-10. Spring cloud api gateway -**      

📌 Following service used -    
- 7.eureka-naming-server
- 10.1api-gateway-server
- 10.2currency-exchange-service
- 10.3currency-conversion-service_dynamicloadbalancing
  
🔁 **Api gateway server flow diagrams**  
![image](https://github.com/user-attachments/assets/010351ea-7474-4b95-ac0c-6a795c21129e)    

---
