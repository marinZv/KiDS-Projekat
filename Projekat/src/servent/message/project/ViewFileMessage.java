package servent.message.project;

import app.MyFile;
import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class ViewFileMessage extends BasicMessage {

    private static final long serialVersionUID = -1593242530055843938L;
    private ServentInfo initiator;
    private int key;

    public ViewFileMessage(int senderPort, int receiverPort, int key, ServentInfo initiator){
        super(MessageType.VIEW_FILE, senderPort, receiverPort);
        this.key = key;
        this.initiator = initiator;
    }

    public ServentInfo getInitiator() {
        return initiator;
    }

    public void setInitiator(ServentInfo initiator) {
        this.initiator = initiator;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

}
