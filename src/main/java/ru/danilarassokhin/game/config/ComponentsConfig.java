package ru.danilarassokhin.game.config;

import tech.hiddenproject.progressive.annotation.ComponentScan;
import tech.hiddenproject.progressive.annotation.Configuration;

@Configuration
@ComponentScan({
    "ru.danilarassokhin.game.mapper",
    "ru.danilarassokhin.game.repository.impl",
    "ru.danilarassokhin.game.service.impl",
    "ru.danilarassokhin.game.controller"
})
public class ComponentsConfig {

}
