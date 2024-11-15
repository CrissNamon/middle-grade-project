package ru.danilarassokhin.game.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.danilarassokhin.game.util.impl.PropertiesFactoryImpl;

public class PropertyFactoryImplTest {

  private static final String TEST_PROPERTY_NAME = "test.property";
  private static final String TEST_PROPERTY_VALUE = "123";

  private final PropertiesFactoryImpl propertiesFactory = new PropertiesFactoryImpl();

  @Test
  @DisplayName("it should get existing property as string successfully")
  public void itShouldGetExistingPropertyAsStringSuccessfully() {
    var actual = propertiesFactory.getAsString(TEST_PROPERTY_NAME);
    Assertions.assertEquals(TEST_PROPERTY_VALUE, actual.get());
  }

  @Test
  @DisplayName("it should get non existing property as string and return empty optional")
  public void itShouldGetNonExistingPropertyAsStringAndReturnEmptyOptional() {
    var actual = propertiesFactory.getAsString("wrong property name");
    Assertions.assertTrue(actual.isEmpty());
  }

  @Test
  @DisplayName("it should get existing int property as in successfully")
  public void itShouldGetExistingIntPropertyAsInSuccessfully() {
    var actual = propertiesFactory.getAsInt(TEST_PROPERTY_NAME);
    Assertions.assertEquals(Integer.valueOf(TEST_PROPERTY_VALUE), actual.get());
  }

  @Test
  @DisplayName("it should get non existing int property as int and return empty optional")
  public void itShouldGetNonExistingIntPropertyAsIntAndReturnEmptyOptional() {
    var actual = propertiesFactory.getAsInt("wrong property name");
    Assertions.assertTrue(actual.isEmpty());
  }

}
