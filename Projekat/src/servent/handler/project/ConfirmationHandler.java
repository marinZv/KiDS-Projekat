package servent.handler.project;

import app.AppConfig;
import app.ChordState;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.project.ConfirmationMessage;

public class ConfirmationHandler implements MessageHandler {

    private Message clientMessage;

    public ConfirmationHandler(Message clientMessage){
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if(MessageType.CONFIRMATION == clientMessage.getMessageType()){
            ConfirmationMessage confirmationMessage = (ConfirmationMessage) clientMessage;
            if(AppConfig.myServentInfo.getListenerPort() == confirmationMessage.getReceiverPort()
                && AppConfig.chordState.getNextNode().getListenerPort() == confirmationMessage.getSenderPort()){
                if(confirmationMessage.isAlive()){
                    AppConfig.chordState.getIsAlive().set(true);
                }else{
                    AppConfig.chordState.getIsAlive().set(false);
                }

                synchronized (ChordState.IS_ALIVE_LOCK){
                    ChordState.IS_ALIVE_LOCK.notifyAll();
                }

            }
        }
    }
}
