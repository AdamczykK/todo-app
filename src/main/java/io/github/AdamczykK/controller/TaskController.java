package io.github.AdamczykK.controller;


import io.github.AdamczykK.logic.TaskService;
import io.github.AdamczykK.model.Task;
import io.github.AdamczykK.model.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final ApplicationEventPublisher eventPublisher;
    private final TaskRepository repository;
    private final TaskService service;

    public TaskController(ApplicationEventPublisher eventPublisher, final TaskRepository repository, TaskService service) {
        this.eventPublisher = eventPublisher;
        this.repository = repository;
        this.service = service;
    }

    @GetMapping
    String showTasks(Model model) {
        model.addAttribute("tasks", getAllTasks());
        return "tasks";
    }

    @GetMapping(params = {"!sort", "!page", "!size"})
    CompletableFuture<ResponseEntity<List<Task>>> readAllTasks() {
        logger.warn("Exposing all tasks!");
        return service.findAllAsync().thenApply(ResponseEntity::ok);
    }

    @GetMapping("/deadline")
    ResponseEntity<List<Task>> readAllDeadlineTasks() {
        return ResponseEntity.ok(service.findAllTasksBeforeDeadline());
    }

    /*@GetMapping
    ResponseEntity<List<Task>> readAllTasks(Pageable page) {
        logger.info("Custom pageable!");
        return ResponseEntity.ok(repository.findAll(page).getContent());
    }*/

    @GetMapping("/{id}")
    ResponseEntity<?> readTask(@PathVariable int id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    ResponseEntity<?> postTask(@RequestBody @Valid Task toPost) {
        repository.save(toPost);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/search/done")
    ResponseEntity<List<Task>> readDoneTasks(@RequestParam(defaultValue = "true") boolean state) {
        return ResponseEntity.ok(repository.findByDone(state));
    }


    @PutMapping("/{id}")
    ResponseEntity<?> updateTask(@PathVariable int id, @RequestBody @Valid Task toUpdate) {
        if(!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.findById(id)
                .ifPresent(task -> {
                    task.updateFrom(toUpdate);
                    repository.save(task);
                });
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @PatchMapping("/{id}")
    public ResponseEntity<?> toggleTask(@PathVariable int id) {
        if(!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.findById(id)
                .map(Task::toggle)
                .ifPresent(eventPublisher::publishEvent);
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Task> removeTask(@PathVariable int id) {
        if(!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.removeById(id);
        return ResponseEntity.noContent().build();
    }

    @ModelAttribute("tasks")
    List<Task> getAllTasks() {
        return repository.findAll();
    }

}
