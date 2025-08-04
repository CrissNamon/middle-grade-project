package ru.danilarassokhin.game.security;

import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.AntPathMatcher;

@RequiredArgsConstructor
public class HttpSecurity {

  private final AntPathMatcher antPathMatcher = new AntPathMatcher();

  private final Set<String> paths;
  @Getter
  private final String loginUrl;
  @Getter
  private final String codeUrl;

  public boolean requiresAuth(String path) {
    return paths.stream().anyMatch(securedPath -> antPathMatcher.match(securedPath, path));
  }

}
