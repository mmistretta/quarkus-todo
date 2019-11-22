# quarkus-todo
Quarkus Todo App Demo

This demo is based on Burr Sutter's presentation From Devoxx Ukraine: https://www.youtube.com/watch?v=iJBh2NoSCKM&t=2630s

This repository includes the HTML for the application and a completed app

```shell
mvn io.quarkus:quarkus-maven-plugin:1.0.0.CR2:create

Set the project groupId [org.acme.quarkus.sample]: com.redhat.demos.quarkus.todo
Set the project artifactId [my-quarkus-project]: todoapp
Set the project version [1.0-SNAPSHOT]:
Do you want to create a REST resource? (y/n) [no]: y
Set the resource classname [com.redhat.demos.quarkus.todo.HelloResource]: com.redhat.demos.quarkus.todo.ApiResource
Set the resource path  [/api]:
...

cd todoapp
code .

./mvnw clean test
./mvnw clean compile quarkus:dev

```
Open localhost:8080
Open localhost:8080/api
Open src/main/java/com/redhat/demos/quarkus/todo/ApiResource
Change "hello" to "Hello"

```java
package com.redhat.demos.quarkus.todo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api")
public class ApiResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {

        return "Hello";
    }
}
```

Refresh the page
Add an exclaimation point, "Hello!"

```java
package com.redhat.demos.quarkus.todo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api")
public class ApiResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {

        return "Hello!";
    }
}
```

## Hibernate Panache
ctl-c to stop Quarkus

```shell
./mvnw quarkus:list-extensions
...

./mvnw quarkus:add-extensions -Dextensions=quarkus-jdbc-postgresql,quarkus-hibernate-orm-panache,quarkus-resteasy-jsonb
```

Show pom.xml

```xml
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-resteasy-jsonb</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-jdbc-postgresql</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-hibernate-orm-panache</artifactId>
    </dependency>

```

Fire up dev mode again

```shell

./mvnw clean compile quarkus:dev


