package com.springdatajpa.demo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.List;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    // Các thuộc tính và getters, setters khác

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonManagedReference
//    private List<Todo> todos;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<Todo> todos;

    public User() {
        // Empty constructor
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters và setters cho các thuộc tính name, email và todos

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Todo> getTodos() {
        return todos;
    }

    public void setTodos(List<Todo> todos) {
        this.todos = todos;
    }

    // Nếu bạn muốn thêm các phương thức hỗ trợ quản lý mối quan hệ @OneToMany,
    // bạn có thể thêm các phương thức dưới đây.

    public void addTodo(Todo todo) {
        todos.add(todo);
        todo.setUser(this);
    }

    public void removeTodo(Todo todo) {
        todos.remove(todo);
        todo.setUser(null);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
