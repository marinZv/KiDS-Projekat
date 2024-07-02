package servent.handler.project;

import app.AppConfig;
import app.ChordState;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.project.BackupMessage;

public class BackupHandler implements MessageHandler {

    private Message clientMessage;;

    public BackupHandler(Message clientMessage){
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.BACKUP){

            BackupMessage backupMessage = (BackupMessage) clientMessage;

            AppConfig.chordState.addValueToBackup(backupMessage.getSender().getListenerPort(), backupMessage.getValue());
            AppConfig.chordState.setBackupPredecessor(backupMessage.getPredecessor());

            AppConfig.timestampedStandardPrint("Dodao sam kod sebe backup od " + backupMessage.getSender().getChordId());

        }

    }
}
