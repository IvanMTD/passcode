package ru.main.passcode.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import ru.main.passcode.dto.IncomingMessage;
import ru.main.passcode.dto.OutgoingMessage;
import ru.main.passcode.interfaces.RabbitMessageService;

@Service
@PropertySource("classpath:application.properties")
public class IOVideoRabbitRabbitMessageService implements RabbitMessageService {
    @Value("${message.outgoing}")
    private String OUTGOING_QUEUE;
    @Value("${message.incoming}")
    private String INCOMING_QUEUE;
    private RabbitTemplate rabbit;
    @Autowired
    public IOVideoRabbitRabbitMessageService(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }
    @Override
    public void sendMessage(OutgoingMessage outgoingMessage) {
        rabbit.convertAndSend(OUTGOING_QUEUE,outgoingMessage);
    }

    @Override
    public IncomingMessage receive() {
        return rabbit.receiveAndConvert(INCOMING_QUEUE,new ParameterizedTypeReference<IncomingMessage>(){});
    }
}
