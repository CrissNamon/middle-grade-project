package ru.danilarassokhin.game.repository.impl;

import static ru.danilarassokhin.game.config.CamundaConfig.CAMUNDA_DEPLOYMENTS_PROPERTY;
import static ru.danilarassokhin.game.config.CamundaConfig.CAMUNDA_PROCESS_ID_PROPERTY;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import io.camunda.tasklist.CamundaTaskListClient;
import io.camunda.tasklist.dto.Pagination;
import io.camunda.tasklist.dto.Task;
import io.camunda.tasklist.dto.TaskSearch;
import io.camunda.tasklist.dto.TaskState;
import io.camunda.tasklist.exception.TaskListException;
import io.camunda.tasklist.generated.model.TaskByVariables;
import io.camunda.tasklist.generated.model.TaskByVariables.OperatorEnum;
import io.camunda.zeebe.client.ZeebeClient;
import ru.danilarassokhin.game.entity.CamundaActionEntity;
import ru.danilarassokhin.game.entity.CamundaProcessEntity;
import ru.danilarassokhin.game.exception.CamundaException;
import ru.danilarassokhin.game.mapper.CamundaMapper;
import ru.danilarassokhin.game.repository.CamundaRepository;
import ru.danilarassokhin.game.util.PropertiesFactory;
import tech.hiddenproject.progressive.annotation.Autofill;
import tech.hiddenproject.progressive.annotation.GameBean;

/**
 * Implementation of {@link CamundaRepository} based on UserTasks.
 */
@GameBean
public class CamundaRepositoryImpl implements CamundaRepository {

  private static final String BUSINESS_KEY_VARIABLE_NAME = "businessKey";
  private static final String ACTION_VARIABLE_NAME = "action";
  private static final String ACTIONS_VARIABLE_NAME = "actions";
  private static final Pagination EMPTY_PAGINATION = new Pagination();

  private final ZeebeClient zeebeClient;
  private final CamundaTaskListClient taskListClient;
  private final PropertiesFactory propertiesFactory;
  private final CamundaMapper camundaMapper;
  private final String processId;

  @Autofill
  public CamundaRepositoryImpl(
      ZeebeClient zeebeClient,
      PropertiesFactory propertiesFactory,
      CamundaTaskListClient taskListClient,
      CamundaMapper camundaMapper
  ) {
    this.zeebeClient = zeebeClient;
    this.propertiesFactory = propertiesFactory;
    this.taskListClient = taskListClient;
    this.camundaMapper = camundaMapper;
    this.processId = propertiesFactory.getAsString(CAMUNDA_PROCESS_ID_PROPERTY).orElseThrow();
    deployAllProcesses();
  }

  @Override
  public Future<CamundaProcessEntity> createProcess(Integer businessKey) {
    return zeebeClient.newCreateInstanceCommand()
        .bpmnProcessId(processId)
        .latestVersion()
        .variable(BUSINESS_KEY_VARIABLE_NAME, businessKey)
        .send()
        .thenApply(camundaMapper::processInstanceEventToEntity)
        .toCompletableFuture();
  }

  @Override
  public List<CamundaActionEntity> getAvailableActions(Integer businessKey) {
    try {
      return getActionsFromUserTasks(taskListClient.getTasks(createTaskSearch(businessKey)).getItems());
    } catch (TaskListException e) {
      throw new CamundaException(e);
    }
  }

  @Override
  public void doAction(CamundaActionEntity action) {
    try {
      taskListClient.completeTask(action.taskId(), Map.of(ACTION_VARIABLE_NAME, action.id()));
    } catch (TaskListException e) {
      throw new CamundaException(e);
    }
  }

  private TaskSearch createTaskSearch(Integer businessKey) {
    return new TaskSearch()
        .fetchVariable(BUSINESS_KEY_VARIABLE_NAME)
        .addVariableFilter(createBusinessKeyFilter(businessKey))
        .setState(TaskState.CREATED)
        .setPagination(EMPTY_PAGINATION);
  }

  private TaskByVariables createBusinessKeyFilter(Integer businessKey) {
    return new TaskByVariables()
        .name(BUSINESS_KEY_VARIABLE_NAME)
        .operator(OperatorEnum.EQ)
        .value(businessKey.toString());
  }

  private List<CamundaActionEntity> getActionsFromUserTasks(List<Task> tasks) {
    return tasks.stream().flatMap(task -> getActionsFromUserTask(task).stream()
        .map(action -> new CamundaActionEntity(action, task.getId()))).toList();
  }

  private List<String> getActionsFromUserTask(Task userTask) {
    try {
      return taskListClient.getVariables(userTask.getId(), true).stream()
          .filter(variable -> variable.getName().equals(ACTIONS_VARIABLE_NAME))
          .flatMap(variable -> ((List<String>) variable.getValue()).stream())
          .toList();
    } catch (TaskListException e) {
      throw new CamundaException(e);
    }
  }

  private void deployAllProcesses() {
    propertiesFactory.getAsString(CAMUNDA_DEPLOYMENTS_PROPERTY)
        .ifPresent(this::deployProcess);
  }

  private void deployProcess(String resource) {
    try {
      zeebeClient.newDeployResourceCommand()
          .addResourceFromClasspath(resource)
          .requestTimeout(Duration.ofSeconds(30))
          .send()
          .get();
    } catch (InterruptedException | ExecutionException e) {
      throw new CamundaException("Error occurred during process deployment", e);
    }
  }

}
