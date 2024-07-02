package servent.handler.project;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.project.PongMessage;

import java.util.concurrent.atomic.AtomicBoolean;

public class PongHandler implements MessageHandler {

    private Message clientMessage;

    public PongHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.PONG){
            PongMessage pongMessage = (PongMessage) clientMessage;
            if(AppConfig.myServentInfo.getListenerPort() == pongMessage.getReceiverPort()){
                //Ovde treba raditi nesto za mapom.
                if(AppConfig.chordState.getPingPongMap().get(pongMessage.getSenderPort()) == null){
                    AppConfig.chordState.getPingPongMap().put(pongMessage.getSenderPort(), new AtomicBoolean(true));
                }
                AppConfig.chordState.getPingPongMap().get(pongMessage.getSenderPort()).set(true);
            }
        }
    }
}
