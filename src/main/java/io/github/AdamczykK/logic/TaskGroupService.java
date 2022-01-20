package io.github.AdamczykK.logic;

import io.github.AdamczykK.TaskConfigurationProperties;
import io.github.AdamczykK.model.Project;
import io.github.AdamczykK.model.TaskGroup;
import io.github.AdamczykK.model.TaskGroupRepository;
import io.github.AdamczykK.model.TaskRepository;
import io.github.AdamczykK.model.projection.GroupReadModel;
import io.github.AdamczykK.model.projection.GroupWriteModel;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


public class TaskGroupService {
    private TaskGroupRepository repository;
    private TaskRepository taskRepository;

    public TaskGroupService(TaskGroupRepository repository, final TaskRepository taskRepository) {
        this.repository = repository;
        this.taskRepository = taskRepository;
    }

    public GroupReadModel createGroup(final GroupWriteModel source) {
        return createGroup(source, null);
    }

    public GroupReadModel createGroup(GroupWriteModel source, Project project) {
        TaskGroup result = repository.save(source.toGroup(project));
        return new GroupReadModel(result);
    }

    public List<GroupReadModel> readAll() {
        return repository.findAll().stream()
                .map(GroupReadModel::new)
                .collect(Collectors.toList());
    }

    public void toggleGroup(int groupId) {
        if(taskRepository.existsByDoneIsFalseAndGroup_Id(groupId)) {
            throw new IllegalStateException("There are tasks in this group that are not done. Do all tasks first!");
        }
        TaskGroup result = repository.findById(groupId).orElseThrow(() -> new IllegalArgumentException("TaskGroup with given id was not found."));
        result.setDone(!result.isDone());
        repository.save(result);
    }
}
