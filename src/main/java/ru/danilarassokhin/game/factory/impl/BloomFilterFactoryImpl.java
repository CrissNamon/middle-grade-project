package ru.danilarassokhin.game.factory.impl;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;
import ru.danilarassokhin.game.exception.ApplicationException;
import ru.danilarassokhin.game.factory.BloomFilterFactory;
import ru.danilarassokhin.game.model.resilience.BloomFilterWithPresence;
import tech.hiddenproject.progressive.annotation.GameBean;

/**
 * Implementation of {@link BloomFilterFactory} using {@link BloomFilter}.
 */
@GameBean
public class BloomFilterFactoryImpl implements BloomFilterFactory {

  private final static Integer DEFAULT_EXPECTED_ELEMENTS = 10_000_00;
  private final static Double DEFAULT_EXPECTED_FALSE_POSITIVE_PROBABILITY = 0.01;

  private final Map<String, BloomFilterWithPresence<?>> filters = new HashMap<>();

  @Override
  @SuppressWarnings("unchecked")
  public <T> BloomFilterWithPresence<T> create(String name, Class<T> elementsType) {
    return (BloomFilterWithPresence<T>) filters.computeIfAbsent(name, filterName -> {
      var contentFilter = BloomFilter.create(getFunnelFromClass(elementsType), DEFAULT_EXPECTED_ELEMENTS, DEFAULT_EXPECTED_FALSE_POSITIVE_PROBABILITY);
      var presenceFilter = BloomFilter.create(getFunnelFromClass(elementsType), DEFAULT_EXPECTED_ELEMENTS, DEFAULT_EXPECTED_FALSE_POSITIVE_PROBABILITY);
      return new BloomFilterWithPresence(contentFilter, presenceFilter);
    });
  }

  private Funnel<?> getFunnelFromClass(Class<?> elementsType) {
    if (elementsType.equals(String.class)) {
      return Funnels.stringFunnel(Charset.defaultCharset());
    }
    if (elementsType.equals(Integer.class)) {
      return Funnels.integerFunnel();
    }
    throw new ApplicationException("Funnel not found for type: " + elementsType);
  }
}
