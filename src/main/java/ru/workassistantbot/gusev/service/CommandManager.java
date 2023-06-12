package ru.workassistantbot.gusev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.workassistantbot.gusev.commands.*;

/**
 * Тут бот понимает что за команда и вызывает соответствующие методы и классы
 */
@Component
public class CommandManager {
    private String lastCommand = "null";//Запоминание последней комманды
    @Autowired
    StartCommand startCommand;
    @Autowired
    HelpCommand helpCommand;
    @Autowired
    AddWorkCommand addWorkCommand;
    @Autowired
    AddPriceCommand addPriceCommand;
    @Autowired
    GetExcelCommand getExcelCommand;
    @Autowired
    HowMuchCommand howMuchCommand;
    @Autowired
    GetLogsCommand getLogsCommand;

    public SendMessage getAnswer(Update update) {//Ответ
        String text = update.getMessage().getText();
        switch (text) {//Тут список команд
            case "/start" -> {
                lastCommand = "/start";
                return startCommand.answer(update);
            }
            case "/help" -> {
                lastCommand = "/help";
                return helpCommand.answer(update);
            }
            case "/add_work" -> {// запись Работа и колличество
                lastCommand = "/add_work";
                return addWorkCommand.answer(update);
            }
            case "/add_price" -> {//Запись работа и стоимость единицы
                lastCommand = "/add_price";
                return addPriceCommand.answer(update);
            }
            case "/how_much" -> {//получить примерный заработок за дени и за весь месяц
                lastCommand = "/how_much";
                return howMuchCommand.answer(update);
            }
            case "/get_excel" -> {//получить примерный заработок за дени и за весь месяц
                lastCommand = "/get_excel";
                return getExcelCommand.answer(update);
            }
            case "/get_logs" -> {//получить примерный заработок за дени и за весь месяц
                lastCommand = "/get_logs";
                return getLogsCommand.answer(update);
            }
            default -> {//Если не команда
                String chatId = String.valueOf(update.getMessage().getChatId());
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Такой комманды нет. Если что-то непонятно, используйте комманду /help.");
                return message;
            }
        }
    }
    public SendMessage doCommand(Update update) {//Действия с ответом
        switch (lastCommand) {//Тут список команд
            case "/add_work" ->{
                lastCommand = "null";
                return addWorkCommand.run(update);
            }
            case "/add_price" ->{
                lastCommand = "null";
                return addPriceCommand.run(update);
            }
            default -> {
                SendMessage message = new SendMessage();
                message.setChatId(String.valueOf(update.getMessage().getChatId()));
                message.setText("Я этого не понимаю. Если что-то непонятно, используйте комманду /help.");
                return message;
            }
        }
    }
    public SendDocument doSendFile(Update update) {//Действия с ответом
        switch (lastCommand) {//Тут список команд
            case "/get_excel" ->{
                lastCommand = "null";
                return getExcelCommand.run(update);
            }
            case "/get_logs" ->{
                lastCommand = "null";
                return getLogsCommand.run(update);
            }
            default -> {
                return null;
            }
        }
    }
}