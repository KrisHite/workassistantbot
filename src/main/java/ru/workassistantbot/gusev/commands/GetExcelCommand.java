package ru.workassistantbot.gusev.commands;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;

@Component
public class GetExcelCommand {
    @Value("${excel.path_users}")
    private String filePath;//Путь к файлам Excel
    public SendMessage answer(Update update){
        String chatId = String.valueOf(update.getMessage().getChatId());
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        File doc = new File(filePath + update.getMessage().getChat().getFirstName() + ".xlsx");
        if(!doc.exists()){
            message.setText("Excel файл отсутствует!");
        }else {
            message.setText("Отправляю файл...");
        }
        return message;
    }
    public SendDocument run(Update update){
        SendDocument sendDocumentRequest = new SendDocument();
        String chatId = String.valueOf(update.getMessage().getChatId());
        String userName = update.getMessage().getChat().getFirstName();

        File doc = new File(filePath + userName + ".xlsx");
        InputFile getDocument = new InputFile(doc, "Prices.xlsx");

        sendDocumentRequest.setChatId(chatId);
        sendDocumentRequest.setDocument(getDocument);
        sendDocumentRequest.setCaption("");
        return sendDocumentRequest;
    }
}
