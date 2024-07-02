package servent.message.project;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class TellAddFileMessage extends BasicMessage {

    private static final long serialVersionUID = -4233330148016993103L;

    private ServentInfo tellReceiver;

    public TellAddFileMessage(int senderPort, int receiverPort, ServentInfo tellReceiver){
        super(MessageType.TELL_ADD_FILE, senderPort, receiverPort);
        this.tellReceiver = tellReceiver;
    }

    public ServentInfo getTellReceiver() {
        return tellReceiver;
    }

    public void setTellReceiver(ServentInfo tellReceiver) {
        this.tellReceiver = tellReceiver;
    }

}
