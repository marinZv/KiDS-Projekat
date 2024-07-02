package servent.message.project;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class TellRemoveFileMessage extends BasicMessage {

    private static final long serialVersionUID = 2094214040006787963L;

    private ServentInfo tellReceiver;

    public TellRemoveFileMessage(int senderPort, int receiverPort, ServentInfo tellReceiver){
        super(MessageType.TELL_REMOVE_FILE, senderPort, receiverPort);
        this.tellReceiver = tellReceiver;
    }

    public ServentInfo getTellReceiver() {
        return tellReceiver;
    }

    public void setTellReceiver(ServentInfo tellReceiver) {
        this.tellReceiver = tellReceiver;
    }
}
