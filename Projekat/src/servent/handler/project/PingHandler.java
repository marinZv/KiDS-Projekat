package servent.handler.project;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.project.PingMessage;
import servent.message.project.PongMessage;
import servent.message.util.MessageUtil;

public class PingHandler implements MessageHandler {

    private Message clientMessage;

    public PingHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.PING){
            PingMessage pingMessage = (PingMessage) clientMessage;

            if(AppConfig.myServentInfo.getListenerPort() == pingMessage.getReceiverPort()){
                PongMessage pongMessage = new PongMessage(AppConfig.myServentInfo.getListenerPort(), pingMessage.getSenderPort());
                MessageUtil.sendMessage(pongMessage);
            }

        }
    }
}
