package ru.danilarassokhin.game.service.impl;

import java.lang.reflect.Proxy;

import lombok.RequiredArgsConstructor;
import ru.danilarassokhin.game.service.annotation.GameRepository;
import tech.hiddenproject.progressive.annotation.GameBean;
import tech.hiddenproject.progressive.basic.injection.BeanDefinition;
import tech.hiddenproject.progressive.injection.BeanFactory;
import tech.hiddenproject.progressive.injection.DIContainer;
import tech.hiddenproject.progressive.injection.GameBeanCreationPolicy;
import tech.hiddenproject.progressive.util.ComponentAnnotationProcessor;

/**
 * {@link BeanFactory} for repositories.
 */
@RequiredArgsConstructor
public class RepositoryBeanFactory implements BeanFactory {

  private final DIContainer diContainer;

  @Override
  public BeanDefinition createBeanMetaInformationFromClass(Class<?> beanClass) {
    GameRepository annotation = ComponentAnnotationProcessor.findAnnotation(
        beanClass, GameRepository.class);
    if (annotation == null) {
      throw new RuntimeException("No @GameRepository annotation specified!");
    }
    String beanName = annotation.name();
    if (beanName.isEmpty()) {
      beanName = beanClass.getSimpleName().toLowerCase();
    }
    var defaultRepository = diContainer.getBean(DefaultRepository.class);
    defaultRepository.setEntityType(annotation.value());
    var methodInterceptor = new RepositoryMethodInterceptor(defaultRepository);
    var bean = Proxy.newProxyInstance(beanClass.getClassLoader(), new Class[]{beanClass}, methodInterceptor);
    BeanDefinition beanDefinitionData = new BeanDefinition();
    beanDefinitionData.setName(beanName);
    beanDefinitionData.setVariant(GameBean.GLOBAL_VARIANT);
    beanDefinitionData.setCreationPolicy(GameBeanCreationPolicy.SINGLETON);
    beanDefinitionData.setRealType(beanClass);
    beanDefinitionData.setBean(bean);
    beanDefinitionData.setCreated(true);
    beanDefinitionData.setReady(true);
    return beanDefinitionData;
  }

  @Override
  public boolean isShouldBeProcessed(Class<?> beanClass) {
    return beanClass.isAnnotationPresent(GameRepository.class);
  }

  @Override
  public boolean isShouldBeCreated(Class<?> beanClass) {
    return false;
  }
}
