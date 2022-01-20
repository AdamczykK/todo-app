package io.github.AdamczykK.reports;

import io.github.AdamczykK.model.event.TaskDone;
import io.github.AdamczykK.model.event.TaskUndone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ChangedTaskEventListener {
    private static final Logger logger = LoggerFactory.getLogger(ChangedTaskEventListener.class);
    private final PersistedTaskEventRepository repository;

    public ChangedTaskEventListener(PersistedTaskEventRepository repository) {
        this.repository = repository;
    }

    @Async
    @EventListener
    public void on (TaskDone event) {
        logger.info("Got " + event);
        repository.save(new PersistedTaskEvent(event));
    }

    @Async
    @EventListener
    public void on (TaskUndone event) {
        logger.info("Got " + event);
        repository.save(new PersistedTaskEvent(event));
    }
}
