package ru.danilarassokhin.game.config;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;

import io.camunda.tasklist.CamundaTaskListClient;
import io.camunda.tasklist.auth.Authentication;
import io.camunda.tasklist.auth.SimpleAuthentication;
import io.camunda.tasklist.auth.SimpleCredential;
import io.camunda.tasklist.dto.Pagination;
import io.camunda.tasklist.dto.TaskSearch;
import io.camunda.tasklist.exception.TaskListException;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.command.ClientException;
import io.camunda.zeebe.client.api.command.ClientStatusException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import ru.danilarassokhin.game.exception.CamundaException;
import ru.danilarassokhin.game.service.DungeonService;
import ru.danilarassokhin.game.service.PlayerService;
import ru.danilarassokhin.game.worker.CamundaWorkerContainer;
import ru.danilarassokhin.game.worker.jobs.AttackCamundaWorker;
import ru.danilarassokhin.game.worker.jobs.GetDungeonWorker;
import ru.danilarassokhin.resilience.factory.CircuitBreakerFactory;
import ru.danilarassokhin.util.PropertiesFactory;
import tech.hiddenproject.progressive.annotation.Configuration;
import tech.hiddenproject.progressive.annotation.GameBean;

@Configuration
public class CamundaConfig {

  public static final String CAMUNDA_CIRCUIT_BREAKER_NAME = "camundaCircuitBreaker";
  public static final String CAMUNDA_DEPLOYMENTS_PROPERTY = "camunda.deployments";
  public static final String CAMUNDA_ZEEBE_URL_PROPERTY = "camunda.zeebe.url";
  public static final String CAMUNDA_TASK_LIST_URL_PROPERTY = "camunda.tasklist.url";
  public static final String CAMUNDA_TASK_LIST_LOGIN_PROPERTY = "camunda.tasklist.login";
  public static final String CAMUNDA_TASK_LIST_PASSWORD_PROPERTY = "camunda.tasklist.password";
  public static final String CAMUNDA_PROCESS_ID_PROPERTY = "camunda.process-id";

  @GameBean(order = 1)
  public ZeebeClient zeebeClient(PropertiesFactory propertiesFactory) {
    return ZeebeClient.newClientBuilder()
        .gatewayAddress(propertiesFactory.getAsString(CAMUNDA_ZEEBE_URL_PROPERTY).orElseThrow())
        .usePlaintext()
        .build();
  }

  @GameBean
  public Authentication taskListAuthentication(PropertiesFactory propertiesFactory) {
    try {
      var url = URI.create(propertiesFactory.getAsString(CAMUNDA_TASK_LIST_URL_PROPERTY).orElseThrow()).toURL();
      var basicAuth = new SimpleCredential(
          propertiesFactory.getAsString(CAMUNDA_TASK_LIST_LOGIN_PROPERTY).orElseThrow(),
          propertiesFactory.getAsString(CAMUNDA_TASK_LIST_PASSWORD_PROPERTY).orElseThrow(),
          url, Duration.ofMinutes(10)
      );
      return new SimpleAuthentication(basicAuth);
    } catch (MalformedURLException e) {
      throw new CamundaException(e);
    }
  }

  @GameBean(order = 2)
  public CamundaTaskListClient camundaTaskListClient(Authentication authentication, PropertiesFactory propertiesFactory) {
    try {
      var client = CamundaTaskListClient.builder()
          .taskListUrl(propertiesFactory.getAsString(CAMUNDA_TASK_LIST_URL_PROPERTY).orElseThrow())
          .shouldReturnVariables()
          .authentication(authentication)
          .build();
      //Simple query for authentication test
      client.getTasks(new TaskSearch().setPagination(new Pagination().setPageSize(1)));
      return client;
    } catch (TaskListException e) {
      throw new CamundaException("Error while bootstrapping tasklist client", e);
    }
  }

  @GameBean(order = 3)
  public AttackCamundaWorker attackCamundaWorker(DungeonService dungeonService, PlayerService playerService) {
    return new AttackCamundaWorker(dungeonService, playerService);
  }

  @GameBean(order = 3)
  public GetDungeonWorker getDungeonWorker(PlayerService playerService) {
    return new GetDungeonWorker(playerService);
  }

  @GameBean(order = 4)
  public CamundaWorkerContainer camundaWorkerFactory(
      ZeebeClient zeebeClient,
      AttackCamundaWorker attackCamundaWorker,
      GetDungeonWorker getDungeonWorker
  ) {
    return new CamundaWorkerContainer(zeebeClient, attackCamundaWorker, getDungeonWorker);
  }

  @GameBean(name = CAMUNDA_CIRCUIT_BREAKER_NAME)
  public CircuitBreaker camundaCircuitBreaker(CircuitBreakerFactory circuitBreakerFactory) {
    return circuitBreakerFactory.create(CAMUNDA_CIRCUIT_BREAKER_NAME, ClientStatusException.class,
                                        ClientException.class, CamundaException.class);
  }

}
