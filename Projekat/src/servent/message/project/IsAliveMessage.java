package servent.message.project;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class IsAliveMessage extends BasicMessage {

    private static final long serialVersionUID = -1770964358125641308L;
    public IsAliveMessage(int senderPort, int receiverPort) {
        super(MessageType.IS_ALIVE, senderPort, receiverPort);
    }
}
