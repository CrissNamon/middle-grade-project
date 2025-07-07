package ru.danilarassokhin.game.config;

import javax.cache.Cache;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;

import ru.danilarassokhin.game.entity.CatalogueDungeonEntity;
import tech.hiddenproject.progressive.annotation.Configuration;
import tech.hiddenproject.progressive.annotation.GameBean;

@Configuration
public class CacheConfig {

  public static final String CACHE_NAME_DUNGEON_CATALOGUE = "dungeonCatalogueCache";

  @GameBean(name = CACHE_NAME_DUNGEON_CATALOGUE)
  public Cache<String, CatalogueDungeonEntity> dungeonCatalogueCache() {
    var provider = Caching.getCachingProvider();
    var manager = provider.getCacheManager();
    return manager.createCache(CACHE_NAME_DUNGEON_CATALOGUE, new MutableConfiguration<>());
  }

}
