package ru.danilarassokhin.game;

import java.util.List;
import java.util.Set;

import ru.danilarassokhin.game.config.ApplicationConfig;
import ru.danilarassokhin.game.config.CacheConfig;
import ru.danilarassokhin.game.config.CamundaConfig;
import ru.danilarassokhin.game.config.ComponentsConfig;
import ru.danilarassokhin.game.config.DataSourceConfig;
import ru.danilarassokhin.game.config.HttpConfig;
import ru.danilarassokhin.game.config.KafkaConfig;
import ru.danilarassokhin.game.config.SecurityConfig;
import ru.danilarassokhin.game.security.HttpSecurity;
import ru.danilarassokhin.game.security.JwtHttpFilter;
import ru.danilarassokhin.game.service.TokenService;
import ru.danilarassokhin.injection.BeanProxyCreator;
import ru.danilarassokhin.injection.ReflectionsPackageScanner;
import ru.danilarassokhin.resilience.CacheableMethodDecorator;
import ru.danilarassokhin.resilience.CircuitBreakerMethodDecorator;
import ru.danilarassokhin.resilience.config.ResilienceConfig;
import ru.danilarassokhin.server.GameServer;
import ru.danilarassokhin.server.config.WebConfig;
import ru.danilarassokhin.sql.config.SqlConfig;
import ru.danilarassokhin.sql.service.impl.TransactionalMethodDecorator;
import tech.hiddenproject.progressive.BasicComponentManager;

public class GameApplication {

  public static void main(String[] args) {
    var diContainer = BasicComponentManager.getDiContainer();

    var transactionalMethodDecorator = new TransactionalMethodDecorator(diContainer);
    var circuitBreakerMethodDecorator = new CircuitBreakerMethodDecorator(diContainer);
    var cacheableMethodDecorator = new CacheableMethodDecorator(diContainer);
    new BeanProxyCreator(List.of(cacheableMethodDecorator, transactionalMethodDecorator, circuitBreakerMethodDecorator));

    var packageScanner = new ReflectionsPackageScanner();
    var configurations = List.of(ApplicationConfig.class, ResilienceConfig.class,
                                 SqlConfig.class, CacheConfig.class, ComponentsConfig.class,
                                 DataSourceConfig.class, CamundaConfig.class, WebConfig.class,
                                 HttpConfig.class, SecurityConfig.class, KafkaConfig.class);
    configurations.forEach(c -> diContainer.loadConfiguration(c, packageScanner));

    GameServer.start(diContainer, Set.of(
        new JwtHttpFilter(diContainer.getBean(HttpSecurity.class),
                          diContainer.getBean("tokenservice", TokenService.class))
    ));
  }

}
