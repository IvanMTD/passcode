package ru.main.passcode.interfaces;

import ru.main.passcode.models.Content;

public interface MessageService {
    void sendMessage(Content content);
    Content receive();
}
