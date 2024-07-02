package servent.handler.project;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.project.BuddyMessage;
import servent.message.project.IsAliveMessage;
import servent.message.util.MessageUtil;

import java.util.concurrent.atomic.AtomicBoolean;

public class BuddyHandler implements MessageHandler {

    private Message clientMessage;
    public BuddyHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if(MessageType.BUDDY == clientMessage.getMessageType()){
            BuddyMessage buddyMessage = (BuddyMessage) clientMessage;
            if(AppConfig.myServentInfo.getListenerPort() == buddyMessage.getReceiverPort()){
                IsAliveMessage isAliveMessage = new IsAliveMessage(AppConfig.myServentInfo.getListenerPort(), buddyMessage.getSendTo().getListenerPort());
                MessageUtil.sendMessage(isAliveMessage);
                AppConfig.chordState.isAliveTimeStart = System.currentTimeMillis();
            }
        }
    }
}
