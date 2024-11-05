package ru.danilarassokhin.game.sql.service.impl;

import ru.danilarassokhin.game.sql.annotation.GameRepository;
import tech.hiddenproject.progressive.injection.BeanScanner;

/**
 * {@link BeanScanner} for repositories.
 */
public class RepositoryBeanScanner implements BeanScanner {

  @Override
  public boolean shouldBeLoaded(Class<?> c) {
    return c.isAnnotationPresent(GameRepository.class);
  }
}
