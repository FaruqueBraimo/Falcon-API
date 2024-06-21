# Falcon API

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)

A Java application project for travel assistance.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Used Technologies](#used-technologies)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Authentication](#authentication)
- [Database](#database)
- [Swagger Endpoint](#swagger)
- [How it works?](#how-it-works)
- [How it works?](#how-it-works)
## Prerequisites

1. JDK 17 or Higher
2. Postgres Database

## Installation

1. Clone the repository: https://github.com/FaruqueBraimo/Falcon-API.git
2. Install the maven dependencies
3. Run the database [See section](#database)


## Usage

1. Start the application with Maven
2. The API will be accessible at http://localhost:8083

## API Endpoints

The API provides the following endpoints:

```markdown
GET falcon/insight - Should return population, gdp exchange rates and weather forecast if a city is provided, if it's
country only population and gdp. Full data only if the user is authenticated.

GET falcon/insights/historical - Should return population and gdp from 2012 to 2022 for given country. The endpoint is
cached, the subsequent requests should be faster (Only for authenticated users)

POST falcon/auth/singIn - User Login

POST falcon/auth/sinnUp - New User registration
```

## Authentication
The API uses Spring Security with jwt for authentication control. To get access use falcon/auth/signUp, if the registration is done, please use the credentials to login.
It should give you a token. Use the token to use other endpoints. 


## Database
The project uses [PostgresSQL](https://www.postgresql.org/) as the database. You must create a database named falcon manually.
It should have admin and 123 as username and password. Alternatively you can run the docker compose file under /docker folder.


## Used Technologies
- **Java 17**
    - Website: [Java SE 17 Documentation](https://docs.oracle.com/en/java/javase/17/)

- **Spring Boot**
    - Website: [Spring Boot Documentation](https://spring.io/projects/spring-boot)

- **PostgreSQL**
    - Website: [PostgreSQL Documentation](https://www.postgresql.org/docs/)

- **JWT (JSON Web Token)**
    - Website: [JWT.io](https://jwt.io/)

- **Failsafe**
    - Website: [Failsafe Documentation](https://failsafe.dev/)

- **Bucket4j**
    - Website: [Bucket4j GitHub Repository](https://github.com/vladimir-bukhtoyarov/bucket4j)

## Swagger
The project has a Swagger documentation, can be found at:
http://localhost:8083/swagger-ui/index.html#/


## How it works
The main endpoint is /falcon/insight, it can retrieve a population, gdp, exchange rates and weather information.
  - If a city is provided it returns a full data a weather forecast for the city, population and exchange rates for it's country
  - If a country is provided it should return only population and gdp. 
  - All the data is cached, so subsequents request should be faster, except for weather forecast.
  - It implements, rate limit per minute to prevent abuse of services using Bucket4j.
  - It handles API failure by retrying few times, before giving up using Failsafe. 
  - /falcon/insights/historical is just for gdp and population. If a country is given.
 
## Performance
- It makes 4/5 API Calls for data achievement, those endpoints take seconds to respond. asynchronous tasks are executed on task that don't depend on each other, this improved the performance, from 7s to 2.5.
- The average response time is 2.5 seconds for the main endpoint /insigh/city. When running the same query again it shoud around 1332 milliseconds.
- It could take longer 5s on the first atempt when the application is starting. The subsequent requests should be faster, without caching.

