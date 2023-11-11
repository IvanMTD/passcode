package ru.main.passcode.interfaces;

import ru.main.passcode.dto.IncomingMessage;
import ru.main.passcode.dto.OutgoingMessage;
import ru.main.passcode.models.Content;

public interface RabbitMessageService {
    void sendMessage(OutgoingMessage outgoingMessage);
    IncomingMessage receive();
}
