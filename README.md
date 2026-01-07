# Project Setup Guide

Hi, I'm Duong, the leader of this project. We completed this project as a team of 5 people through many challenges. Today, I will show you what you need to run this project on your local machine.

## 1. Requirements

You need to install:
- JDK 17.3
- IntelliJ IDEA
- Visual Studio Code
- SQL Server
- Spring Boot 3
- Vue.js
- Java environment

Most of these can be found with installation tutorials on YouTube or the Internet.

## 2. Setup

- Open the FE project in Visual Studio Code  
- Open the BE project in IntelliJ  
- Open the database file in SQL Server

After that:
- Reload the `pom.xml` file in the BE project
- Run `npm install` in the FE project to install dependencies
- Run the database script to create the database
- Optionally change  
  `spring.jpa.hibernate.ddl-auto=none`  
  to  
  `spring.jpa.hibernate.ddl-auto=update`  
  in `application.properties`

Update configuration values such as:
- Database username
- Database password
- Cloudinary credentials  
…so they match your environment.

## 3. Run the Project

After completing the setup, run both BE and FE.

I hope everything works immediately. If any issues occur during the process, please feel free to reach out.
