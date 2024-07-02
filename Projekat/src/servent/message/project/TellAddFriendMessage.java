package servent.message.project;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class TellAddFriendMessage extends BasicMessage {

    private static final long serialVersionUID = 2964071636371496999L;

    private ServentInfo tellReceiver;

    public TellAddFriendMessage(int senderPort, int receiverPort, ServentInfo tellReceiver){
        super(MessageType.TELL_ADD_FRIEND, senderPort, receiverPort);
        this.tellReceiver = tellReceiver;
    }

    public ServentInfo getTellReceiver() {
        return tellReceiver;
    }

    public void setTellReceiver(ServentInfo tellReceiver) {
        this.tellReceiver = tellReceiver;
    }

}
