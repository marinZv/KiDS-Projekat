package servent.handler.project;

import app.AppConfig;
import mutex.Mutex;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.project.GoodbyeMessage;
import servent.message.project.RemoveUpdateMessage;
import servent.message.util.MessageUtil;

public class GoodbyeHandler implements MessageHandler {

    private Message clientMessage;
    private Mutex mutex;


    public GoodbyeHandler(Message clientMessage, Mutex mutex){
        this.clientMessage = clientMessage;
        this.mutex = mutex;
    }

    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.GOODBYE) {


            GoodbyeMessage goodbyeMessage = (GoodbyeMessage) clientMessage;
            AppConfig.chordState.acceptingData(goodbyeMessage.getValues(), goodbyeMessage.getPredecessor());

            RemoveUpdateMessage update = new RemoveUpdateMessage(AppConfig.myServentInfo, AppConfig.chordState.getNextNode(), goodbyeMessage.getSender());
            MessageUtil.sendMessage(update);

        } else {
            AppConfig.timestampedErrorPrint("Goodbye handler got a message that is not a GOODBYE");
        }

    }
}
