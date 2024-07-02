package servent.handler.project;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.project.AddFileMessage;

public class AddFileHandler implements MessageHandler {

    private Message clientMessage;

    public AddFileHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.ADD_FILE) {

            AddFileMessage addFileMessage = (AddFileMessage) clientMessage;

            AppConfig.chordState.putValue(addFileMessage.getKey(), addFileMessage.getFile(), addFileMessage.getInitiator());

		} else {
			AppConfig.timestampedErrorPrint("Put handler got a message that is not ADD FILE");
		}

    }
}
