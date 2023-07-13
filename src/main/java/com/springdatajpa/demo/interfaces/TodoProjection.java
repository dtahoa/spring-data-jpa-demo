package com.springdatajpa.demo.interfaces;

import java.time.LocalDateTime;

public interface TodoProjection {
    String getTodoItem();
    LocalDateTime getCreatedAt();
}
