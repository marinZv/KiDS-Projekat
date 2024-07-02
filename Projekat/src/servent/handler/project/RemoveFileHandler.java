package servent.handler.project;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.project.RemoveFileMessage;

public class RemoveFileHandler implements MessageHandler {

    private Message clientMessage;

    public RemoveFileHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.REMOVE_FILE){

            RemoveFileMessage removeFileMessage = (RemoveFileMessage) clientMessage;

            AppConfig.chordState.deleteValue(removeFileMessage.getKey(), removeFileMessage.getFile(), removeFileMessage.getInitiator());

        }else{
            AppConfig.timestampedErrorPrint("RemoveFileHandler got a message that is not REMOVE FILE");
        }

    }
}
