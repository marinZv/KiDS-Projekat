package servent.handler.project;

import app.AppConfig;
import mutex.Mutex;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.project.TellWelcomeMessage;
import servent.message.util.MessageUtil;

public class TellWelcomeHandler implements MessageHandler {

    private Message clientMessage;
    private Mutex mutex;

    public TellWelcomeHandler(Message clientMessage, Mutex mutex){
        this.clientMessage = clientMessage;
        this.mutex = mutex;
    }

    @Override
    public void run() {
        if(MessageType.TELL_WELCOME == clientMessage.getMessageType()){

            TellWelcomeMessage tellWelcomeMessage = (TellWelcomeMessage) clientMessage;

            if(AppConfig.myServentInfo.getChordId() == tellWelcomeMessage.getRbs()){
                mutex.unlock(false);
                AppConfig.timestampedStandardPrint("Povezao sam se ");
            }else{
                TellWelcomeMessage newTellWelcomeMessage = new TellWelcomeMessage(AppConfig.myServentInfo, AppConfig.chordState.getNextNodeForKey(tellWelcomeMessage.getRbs()), tellWelcomeMessage.getRbs());
                MessageUtil.sendMessage(newTellWelcomeMessage);
            }
        }
    }
}
