package servent.handler.project;

import app.AppConfig;
import app.ServentInfo;
import mutex.Mutex;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.project.TellAddFileMessage;
import servent.message.util.MessageUtil;

public class TellAddFileHandler implements MessageHandler {

    private Message clientMessage;
    private Mutex mutex;

    public TellAddFileHandler(Message clientMessage, Mutex mutex) {
        this.clientMessage = clientMessage;
        this.mutex = mutex;
    }

    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.TELL_ADD_FILE){
            TellAddFileMessage tellAddFileMessage = (TellAddFileMessage)clientMessage;

            if(AppConfig.myServentInfo.getListenerPort() == tellAddFileMessage.getTellReceiver().getListenerPort()){
                System.out.println("Otkljucao sam lock za add file");
                mutex.unlock(true);
            }else{
                ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(tellAddFileMessage.getTellReceiver().getChordId());
                TellAddFileMessage newTellAddFileMessage = new TellAddFileMessage(AppConfig.myServentInfo.getListenerPort(), nextNode.getListenerPort(), tellAddFileMessage.getTellReceiver());
                MessageUtil.sendMessage(newTellAddFileMessage);
            }

        }
    }
}
