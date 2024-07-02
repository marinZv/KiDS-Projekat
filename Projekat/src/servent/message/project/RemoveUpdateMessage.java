package servent.message.project;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class RemoveUpdateMessage extends BasicMessage {

    private ServentInfo toRemove;

    private ServentInfo sender;
    private ServentInfo receiver;

    public RemoveUpdateMessage(ServentInfo sender, ServentInfo receiver, ServentInfo toRemove) {
        super(MessageType.REMOVE_UPDATE, sender.getListenerPort(), receiver.getListenerPort());
        this.toRemove = toRemove;
        this.receiver = receiver;
        this.sender = sender;
    }

    public ServentInfo getToRemove() {
        return toRemove;
    }

    public void setToRemove(ServentInfo toRemove) {
        this.toRemove = toRemove;
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
}
