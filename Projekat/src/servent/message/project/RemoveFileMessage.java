package servent.message.project;

import app.MyFile;
import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class RemoveFileMessage extends BasicMessage {

    private static final long serialVersionUID = 4413428653451192076L;

    private MyFile file;
    private ServentInfo initiator;
    private int key;

    public RemoveFileMessage(int senderPort, int receiverPort, int key, MyFile file, ServentInfo initiator){
        super(MessageType.REMOVE_FILE, senderPort, receiverPort);
        this.key = key;
        this.file = file;
        this.initiator = initiator;
    }

    public MyFile getFile() {
        return file;
    }

    public void setFile(MyFile file) {
        this.file = file;
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
