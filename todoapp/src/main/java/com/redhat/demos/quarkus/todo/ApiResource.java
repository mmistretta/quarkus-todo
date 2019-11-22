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

    @POST
    @Transactional
    public Response addTodo(Todo todo) {

        todo.persist();
        return Response.created(URI.create("/" + todo.id)).entity(todo).build();
    }

    @PATCH
    @Path("/{id}")
    @Transactional
    public Response updateTodo(@PathParam("id") Long id, Todo updatedTodo) {

        Todo todo = Todo.findById(id);
        todo.title = updatedTodo.title;
        todo.persist();
        return Response.ok(todo).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteTodo(@PathParam("id") Long id) {

        Todo todo = Todo.findById(id);
        todo.delete();
        return Response.ok().build();
    }
}