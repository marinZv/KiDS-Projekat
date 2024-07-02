package servent.message.project;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class AddFriendReplyMessage extends BasicMessage {

    private static final long serialVersionUID = 2060979403774412389L;

    private ServentInfo initiator;

    public AddFriendReplyMessage(int senderPort, int receiverPort, ServentInfo initiator) {
        super(MessageType.ADD_FRIEND_REPLY, senderPort, receiverPort);
        this.initiator = initiator;
    }

    public ServentInfo getInitiator() {
        return initiator;
    }

    public void setInitiator(ServentInfo initiator) {
        this.initiator = initiator;
    }

}
