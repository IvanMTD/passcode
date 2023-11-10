package ru.main.passcode.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.main.passcode.interfaces.MessageService;
import ru.main.passcode.models.Content;

@Service
public class RabbitMessageService implements MessageService {
    private RabbitTemplate rabbit;
    @Autowired
    public RabbitMessageService(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }
    @Override
    public void sendMessage(Content content) {
        rabbit.convertAndSend("passcode.all",content);
    }
}
