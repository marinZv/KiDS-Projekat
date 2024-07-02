package servent.message.project;

import servent.message.BasicMessage;
import servent.message.Message;
import servent.message.MessageType;

public class AliveMessage extends BasicMessage {

    private static final long serialVersionUID = -9074854064753430116L;

    public AliveMessage(int senderPort, int receiverPort) {
        super(MessageType.ALIVE, senderPort, receiverPort);
    }
}
