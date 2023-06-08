package ru.workassistantbot.gusev.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.workassistantbot.gusev.service.Excel;


import java.io.File;

@Component
public class StartCommand {
    @Autowired
    Excel excel;
    @Value("${excel.path_users}")
    private String filePath;
    public SendMessage answer(Update update){
        String chatId = String.valueOf(update.getMessage().getChatId());
        String userName = update.getMessage().getChat().getFirstName();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        if(!new File(filePath + userName + ".xlsx").exists()){
            excel.createExcelFile(userName);
            message.setText("Привет, " + userName + ". Я твой бот помощник. Я буду считать твою сделанную работу " +
                    "и скажу сколько ты примерно заработал. Более подробно ты узнаешь по команде /help");
        }else {
            message.setText("Снова привет, " + userName + "! Продолжим работу?");
        }
        return message;
    }
}
