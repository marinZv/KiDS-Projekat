package servent.message.project;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class RequestTokenMessage extends BasicMessage {

    private static final long serialVersionUID = 138201216204301188L;

    private int SN;

    private ServentInfo sender;
    private ServentInfo receiver;

    public RequestTokenMessage(ServentInfo sender, ServentInfo receiver, int SN) {
        super(MessageType.REQUEST_TOKEN, sender.getListenerPort(), receiver.getListenerPort());
        this.SN = SN;
        this.sender = sender;
        this.receiver = receiver;
    }

    public int getSN() {
        return SN;
    }

    public void setSN(int SN) {
        this.SN = SN;
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
