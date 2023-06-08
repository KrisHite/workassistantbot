package ru.workassistantbot.gusev.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;


@Component
public class Logs {
    @Value("${logs.path}")
    String logsPath;
    public String getLog(String e){
        System.out.println(e);
        CreateLogFile();//Создаю файл если его нет
        String data = getTimeDay();
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(logsPath + "/" + data + ".txt", true));
            writer.println(getTimeSeconds() + e + "\n");//Добавить в логи время в формате HH-mm-ss
            writer.close();
        }catch (FileNotFoundException ex){
            CreateLogFile();
        }catch(IOException exception){
            System.out.println("Ошибка!");
        }
        return "Ошибка! Проверьте правильно ли вы ввели данные. Если возникают трудности, используйте команду /help.";
    }
    private void CreateLogFile() {
        String data = getTimeDay();
        File file = new File(logsPath);
        file.mkdirs();
        file = new File(logsPath,data + ".txt");
        if(!file.exists()){
            try {
                file.createNewFile();
            }catch (IOException e){
                System.out.println("Ошибка создания log файла!");
            }
        }
    }
    private String getTimeDay(){
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return date.format(formatter);
    }
    private String getTimeSeconds(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("[HH:mm:ss]");
        return formatter.format(date);
    }
}
