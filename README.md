[![Build Status](https://travis-ci.com/KalievskiyYan/ContactFilter.svg?branch=master)](https://travis-ci.com/KalievskiyYan/ContactFilter)
# Contact Filter Application


### Required infrastructure:
- Docker
- JDK 11

### Local development:
- Run PostgresDB, could be done within docker-compose

  ```shell script
  docker-compose up db
  ```
  
- Run Contact Filter API application, by default http://localhost:8080

  ```shell script
  mvn spring-boot:run
  ```

### Project build:

Compile and build project: `mvn clean install`
