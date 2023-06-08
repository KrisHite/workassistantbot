package ru.workassistantbot.gusev.commands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class HelpCommand {
    public SendMessage answer(Update update){
        String chatId = String.valueOf(update.getMessage().getChatId());
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Для того чтобы добавить выполненную тобой работу используй команду /add_work. Далее " +
                "напиши наименование работы (соблюдая регистр) и после запятой колличество, которое ты сделал." +
                "Для того чтобы я посчитал твой примерный заработок, необходимо записать известные тебе расценки на" +
                " ту работу, которую ты делаешь. Для этого воспользуйся командой /add_price. Далее напиши наименование работы" +
                " и через запятую расценку за единицу выполненой работы. Если хочешь посмотреть таблицу с данными о твоей работе" +
                " воспользуйся коммандой /get_excel. Если хочешь чтобы я посчитал примерную стоимость твоих работ на сегодня " +
                "используй /how_much.");
        return message;
    }
}
