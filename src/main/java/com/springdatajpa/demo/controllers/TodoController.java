package com.springdatajpa.demo.controllers;

import com.springdatajpa.demo.entity.Todo;
import com.springdatajpa.demo.entity.TodoDTO;
import com.springdatajpa.demo.interfaces.TodoProjection;
import com.springdatajpa.demo.interfaces.TodoRequest;
import com.springdatajpa.demo.interfaces.TodoUser;
import com.springdatajpa.demo.repository.TodoRepository;
import com.springdatajpa.demo.services.CachingService;
import com.springdatajpa.demo.services.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/todos")
public class TodoController {
	@Autowired
	private TodoService todoService;

	@Autowired
	private CachingService cachingService;

	@Autowired
	private TodoRepository todoRepository;

	// QUERY CREATION
	@GetMapping
	public ResponseEntity<List<Todo>> getAllTodos() {
		return ResponseEntity.ok(todoService.getAllTask());
	}

	// QUERY CREATION: CREATE NEW TODO
	@PostMapping
	public ResponseEntity<Todo> addTodo(@RequestParam String todoItem, @RequestParam String status) {
		Todo todo = new Todo(todoItem, status);
		todo.setTodoItem(todoItem);
		todo.setCompleted(status);
		return ResponseEntity.ok(todoService.createNewTask(todo));
	}

	// QUERY CREATION: FIND BY ID
	@GetMapping("/{id}")
	public ResponseEntity<List<Todo>> findTodoById(@PathVariable long id) {
		return ResponseEntity.ok(todoService.findTaskById(id));
	}

	@GetMapping("/detail/{id}")
	public ResponseEntity<TodoDTO> getTodoById(@PathVariable long id) {
//		Todo todo = todoRepository.findById(id)
//				.orElseThrow(() -> new Error("todoId " + id + "not found"));
//		TodoDTO todoDto = this.convertToDTO(todo);

		Optional<Todo> optionalTodo = todoRepository.findById(id);
		TodoDTO res = optionalTodo.map(this::convertToDTO).orElse(null);
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	private TodoDTO convertToDTO(Todo todo) {
		TodoDTO todoDTO = new TodoDTO();
		todoDTO.setId(todo.getId());
		todoDTO.setTodoItem(todo.getTodoItem());
		todoDTO.setCompleted(todo.getCompleted());
		todoDTO.setUser(todo.getUser());
		// Set other fields if needed
		return todoDTO;
	}

	// QUERY CREATION: FIND BY DESC
	@GetMapping("/desc/{desc}")
	public ResponseEntity<List<Todo>> findTaskByDescription(@PathVariable String desc) {
		return ResponseEntity.ok(todoService.findTaskByDescription(desc));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Boolean> deleteTodoById(@PathVariable long id) {
		todoService.deleteTask(id);
		return ResponseEntity.ok(true);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Todo> updateTodoStatus(@PathVariable long id) {
		return ResponseEntity.ok(todoService.updateTask(id));
	}

	// TRANSACTIONS EX 1
	@GetMapping("/demo/transaction")
	public ResponseEntity<Boolean> performMultipleOperations() {
		todoService.performMultipleOperations();
		return ResponseEntity.ok(true);
	}

	// TRANSACTIONS EX 2
	@PostMapping("/demo/transaction-rollback")
	public ResponseEntity<Boolean> performOperationsRollback(@RequestParam String todoItem, @RequestParam String status) {
		Todo todo = new Todo(todoItem, status);
		todo.setTodoItem(todoItem);
		todo.setCompleted(status);
		todoService.createTaskRollback(todo);
		return ResponseEntity.ok(true);
	}

	// PAGING & SORTING EX 1
	@GetMapping("/all")
	public Page<Todo> getAllTodos(Pageable pageable) {
		return todoService.getAllTodos(pageable);
	}

	@GetMapping("/all-page-one")
	public Page<Todo> getAllTodosOne(@RequestParam Integer page, @RequestParam Integer size) {
		// Create a Pageable object with page number, page size, and sorting
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "todoItem"));
		return todoService.getAllTodos(pageable);
	}

	@GetMapping("/all-page-two")
	public List<Todo> getAllTodosTwo(@RequestParam Integer page, @RequestParam Integer size, @RequestParam String sortBy, @RequestParam Sort.Direction direction) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
		Page<Todo> todoPage = todoService.findAllWithPagingAndSorting(pageable);
		return todoPage.getContent();
	}

	// NATIVE QUERY & PROJECTION
	// 1. NATIVE QUERY & PROJECTION
	@GetMapping("/native-query-one")
	public ResponseEntity<List<Todo>> getAllTodoNativeOne() {
		List<Todo> todos = todoService.getAllTodosNativeQuery();
		return ResponseEntity.ok(todos);
	}

	@GetMapping("/native-query-two")
	public ResponseEntity<List<Todo>> getAllTodoNativeTwo(@RequestParam String status) {
		List<Todo> todos = todoService.findTodosByCompleted(status);
		return ResponseEntity.ok(todos);
	}

	@PostMapping("/native-query-three")
	public ResponseEntity<String> insertTodo(@RequestBody TodoRequest todoRequest) {
		try {
			todoService.insertTodo(todoRequest);
			return ResponseEntity.ok("Todo item created successfully.");
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + ex.getMessage());
		}
	}

	// 2. PROJECTION
	@GetMapping("/projection")
	public ResponseEntity<List<TodoProjection>> getAllTodoItemsProjection() {
		List<TodoProjection> todos = todoService.getAllTodoItems();
		return ResponseEntity.ok(todos);
	}

	// BULK CREATE
//	@PostMapping("/bulk-create")
//	public ResponseEntity<String> bulkCreateTodoItems(@RequestBody List<TodoRequest> todoRequests) {
//		todoService.bulkCreateTodoItems(todoRequests);
//		return ResponseEntity.ok("Todo items created successfully.");
//	}

	// CACHING
	@GetMapping("/caching")
	public ResponseEntity<List<Todo>> getAllTodoCaching() {
		List<Todo> todos = todoService.getAllTodos();
		return ResponseEntity.ok(todos);
	}

	@DeleteMapping("/caching/{id}")
	public ResponseEntity<String> deleteTodoCaching(@PathVariable long id) {
		todoService.deleteById(id);
		return ResponseEntity.ok("Delete task successfully.");
	}

	@PostMapping("/caching")
	public ResponseEntity<Todo> deleteTodoCaching(@RequestBody Todo todo) {
		Todo result = todoService.saveTodo(todo);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/clearAllCaches")
	public void clearAllCaches() {
		cachingService.evictAllCaches();
	}

	// LAZY LOADING
	@GetMapping("/lazy-loading/{userId}")
	public ResponseEntity<List<Todo>> lazyLoading(@PathVariable Long userId) {
		return ResponseEntity.ok(todoService.fetchAllTodosByUser(userId));
	}

	@GetMapping("/lazy-loading/todo/{id}")
	public TodoUser lazyLoadingWithUser(@PathVariable Long id) {
		return todoService.findTodoWithUser(id);
	}
}
