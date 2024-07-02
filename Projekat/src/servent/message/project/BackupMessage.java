package servent.message.project;

import app.MyFile;
import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class BackupMessage extends BasicMessage {

    public static final long serialVersionUID = -7225571276615472342L;
    private ServentInfo sender;
    private ServentInfo receiver;

    private MyFile value;

    private ServentInfo predecessor;

    public BackupMessage(ServentInfo sender, ServentInfo receiver, MyFile value, ServentInfo predecessor){
        super(MessageType.BACKUP,sender.getListenerPort(), receiver.getListenerPort());
        this.sender = sender;
        this.receiver = receiver;
        this.value = value;
        this.predecessor = predecessor;
    }

    public ServentInfo getSender() {
        return sender;
    }

    public void setSender(ServentInfo sender) {
        this.sender = sender;
    }

    public ServentInfo getReceiver() {
        return receiver;
    }

    public void setReceiver(ServentInfo receiver) {
        this.receiver = receiver;
    }

    public MyFile getValue() {
        return value;
    }

    public void setValue(MyFile value) {
        this.value = value;
    }

    public ServentInfo getPredecessor() {
        return predecessor;
    }
}
