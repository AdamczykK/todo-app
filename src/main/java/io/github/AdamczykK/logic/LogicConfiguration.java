package io.github.AdamczykK.logic;


import io.github.AdamczykK.TaskConfigurationProperties;
import io.github.AdamczykK.model.ProjectRepository;
import io.github.AdamczykK.model.TaskGroupRepository;
import io.github.AdamczykK.model.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LogicConfiguration {
    @Bean
    ProjectService projectService(final ProjectRepository projectRepository, final TaskGroupRepository taskGroupRepository, final TaskConfigurationProperties config, final TaskGroupService taskGroupService) {
        return new ProjectService(projectRepository, taskGroupRepository, config, taskGroupService);
    }
    @Bean
    TaskGroupService taskGroupService(final TaskGroupRepository taskGroupRepository, final TaskRepository taskRepository) {
        return new TaskGroupService(taskGroupRepository, taskRepository);
    }
}
