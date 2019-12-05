# quarkus-todo
Quarkus Todo App Demo

This demo is based on Burr Sutter's presentation From Devoxx Ukraine: https://www.youtube.com/watch?v=iJBh2NoSCKM&t=2630s

This repository includes the HTML for the application, in the "html-stuff" folder and a completed app in the "todoapp" folder

## Environment setup
* a JDK
* Maven
* Postman or another way to visually test REST API's
* PostgreSQL
* pgAdmin

You an swap out PostgreSQL for another database.  I like PostgreSQL and pgAdmin because PGAdmin's interface makes it is easy to show the changes to the database.  Whatever database you choose you have to set up a user account with the appropriate permissions and a schema for the application

I run both PostgreSQL and PGAdmin from Docker:

```shell
docker run docker run -d -p 5432:5432 --name pgdemodb -e POSTGRES_PASSWORD=redhat-19 postgres
docker run --name pgadmin -p 80:80 -e 'PGADMIN_DEFAULT_EMAIL=<<YOUR_EMAIL_ADDRESS>>' -e 'PGADMIN_DEFAULT_PASSWORD=redhat-19' -d dpage/pgadmin4
```

_NOTE:_ You need to use your laptop's ip when setting up a new Server in pgAdmin.  You can get this with

```shell

ifconfig | grep inet

```

## Step 1 : Generate a basic app

```shell
mvn io.quarkus:quarkus-maven-plugin:1.0.1.Final:create

Set the project groupId [org.acme.quarkus.sample]: com.redhat.demos.quarkus.todo
Set the project artifactId [my-quarkus-project]: quarkus-todo
Set the project version [1.0-SNAPSHOT]:
Do you want to create a REST resource? (y/n) [no]: y
Set the resource classname [com.redhat.demos.quarkus.todo.HelloResource]: com.redhat.demos.quarkus.todo.ApiResource
Set the resource path  [/api]:
```

Show application strucuture

## Step 2 : Dev mode

This next step assumes that you have Visual Studio Code and the VSCode shell command, https://code.visualstudio.com/docs/setup/mac.  You can of course start VSCode however you like and open the directory.

```shell
cd quarkus-todo
code .

./mvnw clean test
./mvnw clean compile quarkus:dev

```

Open localhost:8080 and show the default page
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

Optionally adjust the test case so that it passes:

```java
package com.redhat.demos.quarkus.todo;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class ApiResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/api")
          .then()
             .statusCode(200)
             .body(is("Hello!"));
    }

}
```

## Step 3: Hibernate Panache

ctl-c to stop Quarkus

### Add New Dependencies

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

### Update the application.properties

Add the database properties to our src/main/resources/application.properties file:

```properties

quarkus.datasource.url=jdbc:postgresql://localhost:5432/tododb?currentSchema=todo
quarkus.datasource.driver=org.postgresql.Driver
quarkus.datasource.username=todouser
quarkus.datasource.password=redhat-19
quarkus.hibernate-orm.log.sql=true

quarkus.hibernate-orm.database.generation=drop-and-create

```

### Create an Entity

Create the Todo Entity, src/main/java/com/redhat/demos/quarkus/todo/Todo.java

```java

package com.redhat.demos.quarkus.todo;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.Entity;

@Entity
public class Todo extends PanacheEntity {

    public String title;

}

```

Fire up Quarkus in dev mode and show the create-drop functionality.

```shell

./mvnw clean compile quarkus:dev

```

Show the table in pgAdmin.

### Create some REST endpoints

#### Get all Todo entities

Open src/main/java/com/redhat/demos/quarkus/todo/ApiResource.java

Add a method to return all Todo entities.  Be sure to add the class level annotations for Producing and Consuming JSON:

```java

package com.redhat.demos.quarkus.todo;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ApiResource {

    @GET
    public List<Todo> getAllTodos() {

        return Todo.listAll();
    }

```

Refresh the browser at http://localhost:8080/api

#### Create a Todo entity

Add a method to create Todo entities from a POST:

```java

package com.redhat.demos.quarkus.todo;

import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ApiResource {

    @GET
    public List<Todo> getAllTodos() {
        return Todo.listAll();
    }

    @POST
    @Transactional
    public Response addTodo(final Todo todo) {

        todo.persist();
        return Response.created(URI.create("/" + todo.id)).entity(todo).build();
    }

}

```

_DEBUGGING_: It is easy to forget the @Transactional annotation (at least for me)

Open Postman (or other REST tool) and send a post with the following JSON:

```javascript

{
  "title":"something"
}

```
_NOTE_: Be sure to set the "Accept" and "Content-Type" headers
