package servent.message.project;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class AddFriendRequestMessage extends BasicMessage {

    private static final long serialVersionUID = 4142540085271274088L;

    private ServentInfo initiator;

    public AddFriendRequestMessage(int senderPort, int receiverPort, ServentInfo initiator) {
        super(MessageType.ADD_FRIEND_REQUEST, senderPort, receiverPort);
        this.initiator = initiator;
    }

    public ServentInfo getInitiator() {
        return initiator;
    }

    public void setInitiator(ServentInfo initiator) {
        this.initiator = initiator;
    }
}
