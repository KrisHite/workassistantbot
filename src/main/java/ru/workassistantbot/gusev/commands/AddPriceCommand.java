package ru.workassistantbot.gusev.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.workassistantbot.gusev.service.Excel;
import ru.workassistantbot.gusev.service.Logs;

@Component
public class AddPriceCommand {
    @Autowired
    Excel excel;
    @Autowired
    Logs logs;
    public SendMessage answer(Update update){
        String chatId = String.valueOf(update.getMessage().getChatId());
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Введите наименование работы и расценки за единицу:");
        return message;
    }
    public SendMessage run(Update update){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(update.getMessage().getChatId()));
        //Логика
        String text = update.getMessage().getText().replaceAll(" ", "");//Удаляю все пробелы
        String[] dataOfText = text.split(",");
    try {
        excel.AddInExcel(dataOfText[0], Double.valueOf(dataOfText[1]));
        message.setText("Добавлено!");
    }catch (NumberFormatException e){
        message.setText(logs.getLog("[" + update.getMessage().getChat().getFirstName() + "]: \""
                + text + "\" -- " + getClass().getName() + " -- " + e));
    }
        return message;
    }

}
