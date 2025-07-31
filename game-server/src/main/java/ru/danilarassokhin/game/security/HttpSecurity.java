package ru.danilarassokhin.game.security;

import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.springframework.util.AntPathMatcher;

@RequiredArgsConstructor
public class HttpSecurity {

  private final AntPathMatcher antPathMatcher = new AntPathMatcher();

  private final Set<String> paths;

  public boolean requiresAuth(String path) {
    return paths.stream().anyMatch(securedPath -> antPathMatcher.match(securedPath, path));
  }

}
