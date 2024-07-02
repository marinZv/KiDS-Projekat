package servent.handler.project;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.project.AliveMessage;
import servent.message.project.ConfirmationMessage;
import servent.message.util.MessageUtil;

public class AliveHandler implements MessageHandler {

    private Message clientMessage;
    public AliveHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {

        if(clientMessage.getMessageType() == MessageType.ALIVE){
            AliveMessage aliveMessage = (AliveMessage) clientMessage;
            if(AppConfig.myServentInfo.getListenerPort() == aliveMessage.getReceiverPort()){
                boolean isAlive = false;
                AppConfig.chordState.isAliveTimeEnd = System.currentTimeMillis();
                if((AppConfig.chordState.isAliveTimeEnd - AppConfig.chordState.isAliveTimeStart)
                    < AppConfig.chordState.STRONG){
                    isAlive = true;
                }

                ConfirmationMessage confirmationMessage = new ConfirmationMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getPredecessor().getListenerPort(), isAlive);
                MessageUtil.sendMessage(confirmationMessage);
            }
        }
    }
}
