package ru.workassistantbot.gusev.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("application.properties")
public class BotConfig {
    @Value("${bot.name}")
    String botName;
    @Value("${bot.token}")
    String botToken;
    @Value("${excel.path_users}")
    String excelUserPath;
    @Value("${excel.path_prices}")
    String excelPricePath;
}
