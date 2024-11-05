package ru.danilarassokhin.game.service.impl;

import ru.danilarassokhin.game.service.annotation.GameRepository;
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
