package com.redhat.demos.quarkus.todo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/springapi")
public class SpringApi {


    @GetMapping
    public List<Todo> getAllTodos() {

        return Todo.listAll();
    }
}
