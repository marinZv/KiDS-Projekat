package servent.message.project;

import app.MyFile;
import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

import java.util.List;
import java.util.Map;

public class QuitNodeMessage extends BasicMessage {

    private static final long serialVersionUID = 2670099197657294203L;

    private ServentInfo initiator;
    private ServentInfo predecessor;

    private Map<Integer, List<MyFile>> values;

    public QuitNodeMessage(ServentInfo initiator, int receiverPort,ServentInfo predecessor, Map<Integer,List<MyFile>> values) {
        super(MessageType.QUIT_NODE, initiator.getListenerPort(), receiverPort);
        this.values = values;
        this.predecessor = predecessor;
        this.initiator = initiator;
    }

    public ServentInfo getInitiator() {
        return initiator;
    }

    public void setInitiator(ServentInfo initiator) {
        this.initiator = initiator;
    }

    public ServentInfo getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(ServentInfo predecessor) {
        this.predecessor = predecessor;
    }

    public Map<Integer, List<MyFile>> getValues() {
        return values;
    }

    public void setValues(Map<Integer, List<MyFile>> values) {
        this.values = values;
    }
}
