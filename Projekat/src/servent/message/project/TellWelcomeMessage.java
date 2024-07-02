package servent.message.project;

import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

public class TellWelcomeMessage extends BasicMessage {

    private int rbs;

    public TellWelcomeMessage(ServentInfo sender, ServentInfo receiver, int rbs){
        super(MessageType.TELL_WELCOME, sender.getListenerPort(), receiver.getListenerPort());
        this.rbs = rbs;
    }

    public int getRbs() {
        return rbs;
    }
}
