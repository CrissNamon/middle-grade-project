package ru.danilarassokhin.game.util.impl;

import java.util.HashSet;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import tech.hiddenproject.progressive.injection.PackageScanner;

public class ReflectionsPackageScanner implements PackageScanner {

  @Override
  public Set<Class<?>> findAllClassesIn(String packageName) {
    Reflections reflections = new Reflections(packageName, new SubTypesScanner(false));
    return new HashSet<>(reflections.getSubTypesOf(Object.class));
  }
}
