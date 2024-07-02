package servent.handler.project;

import app.AppConfig;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.project.AliveMessage;
import servent.message.project.IsAliveMessage;
import servent.message.util.MessageUtil;

public class IsAliveHandler implements MessageHandler {

    private Message clientMessage;

    public IsAliveHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if(MessageType.IS_ALIVE == clientMessage.getMessageType()){
            IsAliveMessage isAliveMessage = (IsAliveMessage) clientMessage;
            if(AppConfig.myServentInfo.getListenerPort() == isAliveMessage.getReceiverPort()){
                AliveMessage aliveMessage = new AliveMessage(AppConfig.myServentInfo.getListenerPort(), isAliveMessage.getSenderPort());
                MessageUtil.sendMessage(aliveMessage);
                AppConfig.timestampedStandardPrint("Poslao sam Alive message nazad");
            }
        }
    }
}
