package servent.message.project;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class BuddyMessage extends BasicMessage {

    private static final long serialVersionUID = -8465649366982036473L;

    private ServentInfo sendTo;

    public BuddyMessage(int senderPort, int receiverPort, ServentInfo sendTo) {
        super(MessageType.BUDDY, senderPort, receiverPort);
        this.sendTo = sendTo;
    }

    public ServentInfo getSendTo() {
        return sendTo;
    }
}
