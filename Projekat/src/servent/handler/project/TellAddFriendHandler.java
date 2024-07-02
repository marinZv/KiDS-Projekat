package servent.handler.project;

import servent.handler.MessageHandler;
import servent.message.Message;

public class TellAddFriendHandler implements MessageHandler {

    private Message clientMessage;

    public TellAddFriendHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {

    }
}
