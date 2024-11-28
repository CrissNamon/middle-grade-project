package ru.danilarassokhin.game.config;

import javax.cache.Cache;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;

import ru.danilarassokhin.game.entity.CatalogueDungeonEntity;
import tech.hiddenproject.progressive.annotation.Configuration;
import tech.hiddenproject.progressive.annotation.GameBean;

@Configuration
public class CacheConfig {

  @GameBean(name = "dungeonCatalogueCache")
  public Cache<String, CatalogueDungeonEntity> dungeonCatalogueCache() {
    var provider = Caching.getCachingProvider();
    var manager = provider.getCacheManager();
    return manager.createCache("dungeonCatalogueCache", new MutableConfiguration<>());
  }

}
