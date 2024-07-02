package servent.message.project;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class TellQuitNodeMessage extends BasicMessage {

    private static final long serialVersionUID = 4203386831113222901L;

    private ServentInfo sender;

    public TellQuitNodeMessage(ServentInfo sender, int receiverPort){
        super(MessageType.TELL_QUIT_NODE, sender.getListenerPort(), receiverPort);
        this.sender = sender;
    }

    public ServentInfo getSender() {
        return sender;
    }
}
