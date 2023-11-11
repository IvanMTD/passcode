package ru.main.passcode.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import ru.main.passcode.interfaces.MessageService;
import ru.main.passcode.models.Content;

@Service
public class RabbitMessageService implements MessageService {
    private RabbitTemplate rabbit;
    private MessageConverter converter;
    @Autowired
    public RabbitMessageService(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
        this.converter = rabbit.getMessageConverter();
    }
    @Override
    public void sendMessage(Content content) {
        rabbit.convertAndSend("passcode.all",content);
    }

    @Override
    public Content receive() {
        /*Message message = rabbit.receive("passcode.all",30000);
        return message != null
                ? (Content) converter.fromMessage(message)
                : null;*/
        return rabbit.receiveAndConvert("passcode.all",new ParameterizedTypeReference<Content>(){});
    }
}
