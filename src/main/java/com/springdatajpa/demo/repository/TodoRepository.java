package com.springdatajpa.demo.repository;

import com.springdatajpa.demo.entity.Todo;
import com.springdatajpa.demo.interfaces.TodoProjection;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TodoRepository extends PagingAndSortingRepository<Todo, Long> {
    public Todo findByTodoItem(String task);

    public List<Todo> findByCompletedTrue();

    public List<Todo> findByCompletedFalse();

    public Todo getById(Long id);

    public Todo getTodoById(Long id);

    public List<Todo> findTodoById(Long id);

    public List<Todo> findTodoByTodoItem(String desc);

    @Query(value = "SELECT * FROM todo ORDER BY created_at DESC", countQuery = "SELECT COUNT(*) FROM todo", nativeQuery = true)
    Page<Todo> findAllWithPagingAndSorting(Pageable pageable);

    @Query(value = "SELECT * FROM todo", nativeQuery = true)
    List<Todo> findAllTodos();
    @Query(value = "SELECT * FROM todo WHERE completed = :completed", nativeQuery = true)
    List<Todo> findTodosByCompleted(@Param("completed") String completed);

    @Modifying
    @Query(value = "INSERT INTO todo (completed, todo_item, created_at) VALUES (:completed, :todoItem, :createdAt)", nativeQuery = true)
    void insertTodo(@Param("completed") String completed, @Param("todoItem") String todoItem, @Param("createdAt") LocalDateTime createdAt);

    List<TodoProjection> findAllBy();
}