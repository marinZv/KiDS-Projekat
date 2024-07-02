package servent.handler.project;

import app.AppConfig;
import mutex.Mutex;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.project.GoodbyeMessage;
import servent.message.project.QuitNodeMessage;
import servent.message.project.RemoveUpdateMessage;
import servent.message.util.MessageUtil;

public class QuitNodeHandler implements MessageHandler {

    private Message clientMessage;
    private Mutex mutex;


    public QuitNodeHandler(Message clientMessage, Mutex mutex){
        this.clientMessage = clientMessage;
        this.mutex = mutex;
    }

    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.QUIT_NODE) {

            System.out.println("Usao sam u Quit_Node handler");

            QuitNodeMessage quitNodeMessage = (QuitNodeMessage) clientMessage;
            AppConfig.chordState.acceptingData(quitNodeMessage.getValues(), quitNodeMessage.getPredecessor());

            RemoveUpdateMessage removeUpdateMessage = new RemoveUpdateMessage(AppConfig.myServentInfo, AppConfig.chordState.getNextNode(), quitNodeMessage.getInitiator());
            MessageUtil.sendMessage(removeUpdateMessage);
            System.out.println("Poslao sam remove update message ");

        } else {
            AppConfig.timestampedErrorPrint("QuitHandler got a message that is not a QUIT_NODE");
        }

    }
}
