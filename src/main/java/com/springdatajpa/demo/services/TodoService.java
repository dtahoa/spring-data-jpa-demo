package com.springdatajpa.demo.services;

import com.springdatajpa.demo.interfaces.TodoProjection;
import com.springdatajpa.demo.interfaces.TodoRequest;
import com.springdatajpa.demo.entity.Todo;
import com.springdatajpa.demo.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
//@Transactional // Apply transactional behavior at the class level
public class TodoService {
    @Autowired
    private TodoRepository todoRepository;

    /**
     * Example query method
     */
    public Todo createNewTask(Todo task) {
        return todoRepository.save(task);
    }

    public List<Todo> getAllTask() {
        return (List<Todo>) todoRepository.findAll();
    }

    public List<Todo> findTaskById(Long id) {
        return todoRepository.findTodoById(id);
    }

    public List<Todo> findAllCompletedTask() {
        return todoRepository.findByCompletedTrue();
    }

    public List<Todo> findAllInCompleteTask() {
        return todoRepository.findByCompletedFalse();
    }

    public void deleteTask(Long id) {
        Todo task = todoRepository.getById(id);
        todoRepository.delete(task);
    }

    public Todo updateTask(Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Todo not found with id: " + id));

        if ("Yes".equals(todo.getCompleted())) {
            todo.setCompleted("No");
        } else {
            todo.setCompleted("Yes");
        }

        return todoRepository.save(todo);
    }

    /**
     * Example transaction
     */
    @Transactional
    public void performMultipleOperations() {
        // Perform multiple database operations within a transaction
        Todo todo1 = new Todo("Task 1", "No");
        Todo todo2 = new Todo("Task 2", "Yes");

        // Save todos
        todoRepository.save(todo1);
        todoRepository.save(todo2);

        // Update todo status
        todo1.setCompleted("Yes");
        todoRepository.save(todo1);

        // Delete todo
        todoRepository.delete(todo2);
    }

    @Transactional
    public void createTaskRollback(Todo task) {
        try {
            // Thực hiện thao tác lưu người dùng vào cơ sở dữ liệu
            todoRepository.save(task);

            // Ném một ngoại lệ để tạo ra một tình huống lỗi
            throw new RuntimeException("Error occurred");

        } catch (Exception e) {
            // Xảy ra lỗi, giao dịch sẽ được rollback
            throw new RuntimeException("Failed to create task: " + e.getMessage());
        }
    }

    public Page<Todo> findAllWithPagingAndSorting(Pageable pageable) {
        return todoRepository.findAllWithPagingAndSorting(pageable);
    }

    /**
     * Example paging
     */
    public Page<Todo> getAllTodos(Pageable pageable) {
        return todoRepository.findAll(pageable);
    }


    /**
     * Example native query & projection
     */
    public List<Todo> getAllTodosNativeQuery() {
        return todoRepository.findAllTodos();
    }

    public List<Todo> findTodosByCompleted(String status) {
        return todoRepository.findTodosByCompleted(status);
    }

    public void insertTodo(TodoRequest todoRequest) {
        todoRepository.insertTodo(todoRequest.getCompleted(),
                todoRequest.getTodoItem(),
                todoRequest.getCreatedAt());
    }

    public List<TodoProjection> getAllTodoItems() {
        return todoRepository.findAllBy();
    }

    /**
     * Bulk create
     */
    public void bulkCreateTodoItems(List<TodoRequest> todoRequests) {
        List<Todo> todoItems = todoRequests.stream()
                .map(this::mapToTodo)
                .collect(Collectors.toList());
        todoRepository.saveAll(todoItems);
    }

    private Todo mapToTodo(TodoRequest todoRequest) {
        Todo todo = new Todo();
        todo.setCompleted(todoRequest.getCompleted());
        todo.setTodoItem(todoRequest.getTodoItem());
        return todo;
    }

    // CACHING
    @Cacheable(value = "todos")
    public List<Todo> getAllTodos() {
        return fetchAllTodosFromRepository();
    }

    @CachePut(value = "todos", key = "#result.id")
    public Todo saveTodo(Todo todo) {
        return todoRepository.save(todo);
    }

    @CacheEvict(value = "todos", allEntries = true)
    public void deleteById(Long id) {
        todoRepository.deleteById(id);
    }

    private List<Todo> fetchAllTodosFromRepository() {
        return (List<Todo>) todoRepository.findAll();
    }
}
