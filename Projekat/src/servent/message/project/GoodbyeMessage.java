package servent.message.project;

import app.MyFile;
import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

import java.util.List;
import java.util.Map;

public class GoodbyeMessage extends BasicMessage {

    private Map<Integer, List<MyFile>> values;
    private ServentInfo predecessor;

    private ServentInfo sender;
    private ServentInfo receiver;

    public GoodbyeMessage(ServentInfo sender, ServentInfo receiver, Map<Integer, List<MyFile>> values, ServentInfo predecessor) {
        super(MessageType.GOODBYE, sender.getListenerPort(), receiver.getListenerPort());
        this.values = values;
        this.predecessor = predecessor;
        this.sender = sender;
    }

    public Map<Integer, List<MyFile>> getValues() {
        return values;
    }

    public ServentInfo getPredecessor() {
        return predecessor;
    }

    public void setValues(Map<Integer, List<MyFile>> values) {
        this.values = values;
    }

    public void setPredecessor(ServentInfo predecessor) {
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
}
