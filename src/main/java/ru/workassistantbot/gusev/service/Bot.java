package ru.workassistantbot.gusev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.workassistantbot.gusev.commands.CommandManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


@Component
public class Bot extends TelegramLongPollingBot {
    final BotConfig config;
    @Autowired
    CommandManager command;
    @Autowired
    Logs logs;

    public Bot(BotConfig config) {//Конструктор моего бота
        this.config = config;
        //Добавляю меню боту
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/help", "Помощь"));
        listOfCommands.add(new BotCommand("/add_work", "Добавить сделанную работу"));
        listOfCommands.add(new BotCommand("/add_price", "Добавить стоимость работы"));
        listOfCommands.add(new BotCommand("/get_excel", "Скачать Excel файл"));
        listOfCommands.add(new BotCommand("/how_much", "Примерная стоимость работ"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            logs.getLog("[ErrorInit] -- " + getClass().getName() + " -- " + e);
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {//Реакция на пришедшие сообщения
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (update.getMessage().getText().charAt(0) == '/') {//Если команда
                SendMessage answer = command.getAnswer(update);//Ответ
                if (answer != null) {
                    try {
                        execute(answer);
                    } catch (TelegramApiException e) {
                        logs.getLog("[" + update.getMessage().getChat().getFirstName() + "]: \""
                                + update.getMessage().getText() + "\" -- " + getClass().getName() + " -- " + e);
                    }
                }
                SendDocument sendDocument = command.doSendFile(update);//Если отправить документ
                if (sendDocument != null) {
                    try {
                        execute(sendDocument);
                    } catch (TelegramApiException e) {
                        logs.getLog("[" + update.getMessage().getChat().getFirstName() + "]: \""
                                + update.getMessage().getText() + "\" -- " + getClass().getName() + " -- " + e);
                    }
                }
            } else {//Если просто текст
                SendMessage answer = command.doCommand(update);
                if (answer != null) {
                    try {
                        execute(answer);
                    } catch (TelegramApiException e) {
                        logs.getLog("[" + update.getMessage().getChat().getFirstName() + "]: \""
                                + update.getMessage().getText() + "\" -- " + getClass().getName() + " -- " + e);
                    }
                }
            }
        }
    }
}