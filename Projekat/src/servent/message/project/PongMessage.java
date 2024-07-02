package servent.message.project;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class PongMessage extends BasicMessage {

    public PongMessage(int senderPort, int receiverPort) {
        super(MessageType.PONG, senderPort, receiverPort);
    }
}
