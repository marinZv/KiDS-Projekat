package servent.message.project;

import app.MyFile;
import servent.message.BasicMessage;
import servent.message.MessageType;

import java.util.List;

public class TellViewFileMessage extends BasicMessage {

    private static final long serialVersionUID = 6878149738654463577L;

    private List<MyFile> values;

    public TellViewFileMessage(int senderPort, int receiverPort, List<MyFile> values){
        super(MessageType.TELL_VIEW_FILE, senderPort, receiverPort);
        this.values = values;
    }

    public List<MyFile> getValues() {
        return values;
    }

    public void setValues(List<MyFile> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "TellViewFileMessage{" +
                "values=" + values +
                '}';
    }
}
