package servent.handler.project;

import app.AppConfig;
import app.ServentInfo;
import mutex.Mutex;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.project.TellAddFileMessage;
import servent.message.project.TellRemoveFileMessage;
import servent.message.util.MessageUtil;

public class TellRemoveFileHandler implements MessageHandler {

    private Message clientMessage;

    private Mutex mutex;

    public TellRemoveFileHandler(Message clientMessage, Mutex mutex) {
        this.clientMessage = clientMessage;
        this.mutex = mutex;
    }

    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.TELL_REMOVE_FILE){
            TellRemoveFileMessage tellRemoveFileMessage = (TellRemoveFileMessage)clientMessage;

            if(AppConfig.myServentInfo.getChordId() == tellRemoveFileMessage.getTellReceiver().getChordId()){
                mutex.unlock(false);
                System.out.println("Otkljucao sam lock za remove_file u tell remove file");
            }else{
                ServentInfo nextNode = AppConfig.chordState.getNextNodeForKey(tellRemoveFileMessage.getTellReceiver().getChordId());
                TellRemoveFileMessage newTellRemoveFileMessage = new TellRemoveFileMessage(AppConfig.myServentInfo.getListenerPort(), nextNode.getListenerPort(), tellRemoveFileMessage.getTellReceiver());
                MessageUtil.sendMessage(newTellRemoveFileMessage);
            }
        }
    }
}
