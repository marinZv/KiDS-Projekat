package servent.message.project;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class PingMessage extends BasicMessage {

    private static final long serialVersionUID = 9061883892224377294L;

    public PingMessage(int senderPort, int receiverPort) {
        super(MessageType.PING, senderPort, receiverPort);
    }
}
