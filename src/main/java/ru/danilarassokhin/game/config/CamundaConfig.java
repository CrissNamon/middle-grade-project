package ru.danilarassokhin.game.config;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import ru.danilarassokhin.game.util.PropertiesFactory;
import tech.hiddenproject.progressive.annotation.Configuration;
import tech.hiddenproject.progressive.annotation.GameBean;

@Configuration
public class CamundaConfig {

  public static String CAMUNDA_JOB_EXECUTOR_ACTIVATE_PROPERTY_NAME = "camunda.bpm.job-executor.activate";
  public static String CAMUNDA_DEPLOYMENTS_PROPERTY_NAME = "camunda.bpm.deployments";

  @GameBean
  public ProcessEngine processEngine(PropertiesFactory propertiesFactory) {
    return ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration()
        .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_CREATE_DROP)
        .setJobExecutorActivate(propertiesFactory.getAsBoolean(CAMUNDA_JOB_EXECUTOR_ACTIVATE_PROPERTY_NAME)
                                    .orElse(true))
        .buildProcessEngine();
  }

  @GameBean(order = 1)
  public RuntimeService runtimeService(ProcessEngine processEngine) {
    return processEngine.getRuntimeService();
  }

  @GameBean(order = 2)
  public RepositoryService repositoryService(ProcessEngine processEngine) {
    return processEngine.getRepositoryService();
  }

}
