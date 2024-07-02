package servent.message.project;

import servent.message.BasicMessage;
import servent.message.MessageType;

public class ConfirmationMessage extends BasicMessage {

    private static final long serialVersionUID = 7157182197920518072L;
    private boolean isAlive;

    public ConfirmationMessage(int senderPort, int receiverPort, boolean isAlive){
        super(MessageType.CONFIRMATION, senderPort, receiverPort);
        this.isAlive = isAlive;
    }

    public boolean isAlive() {
        return isAlive;
    }
}
