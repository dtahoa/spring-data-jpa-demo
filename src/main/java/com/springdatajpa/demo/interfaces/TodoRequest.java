package com.springdatajpa.demo.interfaces;

import java.time.LocalDateTime;

public class TodoRequest {
    private String completed;
    private String todoItem;
    private LocalDateTime createdAt;

    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }

    public String getTodoItem() {
        return todoItem;
    }

    public void setTodoItem(String todoItem) {
        this.todoItem = todoItem;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
// Getters and setters
}
