package ru.workassistantbot.gusev.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.workassistantbot.gusev.service.Excel;

@Component
public class HowMuchCommand {
    @Autowired
    Excel excel;
    public SendMessage answer(Update update){
        String chatId = String.valueOf(update.getMessage().getChatId());
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(excel.getValueInExcel(update.getMessage().getChat().getFirstName()));
        return message;
    }
}
