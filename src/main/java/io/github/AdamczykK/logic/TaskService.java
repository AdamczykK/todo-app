package io.github.AdamczykK.logic;


import io.github.AdamczykK.model.Task;
import io.github.AdamczykK.model.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    @Async
    public CompletableFuture<List<Task>> findAllAsync() {
        logger.info("Supply async!");
        return CompletableFuture.supplyAsync(repository::findAll);
    }

    public List<Task> findAllTasksBeforeDeadline() {
        return repository.findAll().stream()
                .filter(task -> !task.isDone())
                .filter(task -> task.getDeadline().isBefore(LocalDateTime.now().withHour(23).withMinute(59).withSecond(59)))
                .collect(Collectors.toList());
    }

    public void removeById(Integer id) {
        repository.removeById(id);
    }
}
