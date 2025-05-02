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

### 1. Rest Template Implementation
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
## 7. Data Shadowing and Data Hiding
<table>
    <tr>
        <td><a href="https://youtu.be/1UXBX1xcOmI">
            <img src="https://github.com/user-attachments/assets/393a6073-ba6a-48dd-972b-9e9b8d908e45" alt="yt" width="20" height="20">
        </a></td>
        <th align="left">7. Data Shadowing and Data Hiding</th>
    </tr>
 </table>
 
🔵 **Data Shadowing**      
If method's local variable and class level variable having same name then inside the method local priority will goes to method's local variable. That is known as data shadowing.In this case 'this' Keyword is use to access class level variable. 

🔵 **Data Hiding**     
In case of Inheritance if parent class data member and child class data memeber having same name then from child class priority will goes to child class data member, that is known as data hiding.  In this case 'super' keyword is use to access parent class variable.

---
## 8. Assignment   

**Task 1:** Implement a class with a static variable to count the number of instances and then Create multiple instances of this class and print the count.

**Task 2:** Develop a Java class to represent a library, with a static data member to store the total number of books in the library and an instance data member to store the book's title. Implement a static method to update the total count when a new book is added.

**Task 3:** Implement a Java class to manage a shopping cart, with an instance data member to store the items in the cart and instance methods to add, remove, and calculate the total price of the items.

**Task 4:** Develop a Java program to process a list of employees. Use instance methods to calculate their salary based on after 10% of tax deduction.

---
