package ru.workassistantbot.gusev.commands;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class GetLogsCommand {
    @Value("${logs.path}")
    private String filePath;//Путь к файлам Excel
    public SendMessage answer(Update update){
        String chatId = String.valueOf(update.getMessage().getChatId());
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String data = date.format(formatter);

        File doc = new File(filePath, data + ".txt");
        if(!doc.exists()){//Если файла с логами нет
            message.setText("Файл с логами отсутствует!");
        }else {
            message.setText("Отправляю файл с логами на сегодня.");
        }
        return message;
    }
    public SendDocument run(Update update){
        SendDocument sendDocumentRequest = new SendDocument();
        String chatId = String.valueOf(update.getMessage().getChatId());
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String data = date.format(formatter);

        File doc = new File(filePath, data + ".txt");
        InputFile getDocument = new InputFile(doc, data + ".txt");
        sendDocumentRequest.setChatId(chatId);
        sendDocumentRequest.setDocument(getDocument);
        sendDocumentRequest.setCaption("");
        return sendDocumentRequest;
    }
}
