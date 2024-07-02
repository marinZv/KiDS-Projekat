package servent.message.project;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class TellGoodbyeMessage extends BasicMessage {

    private ServentInfo sender;

    public TellGoodbyeMessage(ServentInfo sender, ServentInfo receiver){
        super(MessageType.TELL_GOODBYE, sender.getListenerPort(), receiver.getListenerPort());
        this.sender = sender;
    }

}
