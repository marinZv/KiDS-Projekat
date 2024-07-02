package servent.handler.project;

import app.AppConfig;
import app.ServentInfo;
import mutex.Mutex;
import mutex.SuzukiKasamiMutex;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.project.TokenMessage;
import servent.message.util.MessageUtil;

import java.util.Arrays;

public class TokenHandler implements MessageHandler {

    private Message clientMessage;
    private SuzukiKasamiMutex suzukiKasamiMutex;

    public TokenHandler(Message clientMessage, Mutex mutex) {
        this.clientMessage = clientMessage;
        this.suzukiKasamiMutex = (SuzukiKasamiMutex) mutex;
    }

    @Override
    public void run() {
        try {

            if(MessageType.TOKEN == clientMessage.getMessageType()) {

                TokenMessage tokenMessage = (TokenMessage) clientMessage;

                if(tokenMessage.getTokenReceiver().getChordId() == AppConfig.myServentInfo.getChordId()) {

                    AppConfig.timestampedErrorPrint("Node " + AppConfig.myServentInfo.getChordId() + " receieved Token from " + tokenMessage.getSender().getChordId());

                    SuzukiKasamiMutex.setToken(tokenMessage);

                } else {

                    AppConfig.timestampedErrorPrint("Node " + AppConfig.myServentInfo.getChordId() + " received Token from " + tokenMessage.getSender().getChordId());

                    ServentInfo tokenReceiver = tokenMessage.getTokenReceiver();

                    ServentInfo receiver = AppConfig.chordState.getNextNodeForKey(tokenReceiver.getChordId());

                    TokenMessage token = tokenMessage.createNewToken(AppConfig.myServentInfo, receiver, tokenReceiver);
                    MessageUtil.sendMessage(token);

                }


            } else {
                AppConfig.timestampedErrorPrint("Token handler got a message that is not TOKEN");
            }


        } catch (Exception e) {
            AppConfig.timestampedErrorPrint("Exception in TokenHandler. Message : " + e.getMessage() + "\nError : " + Arrays.toString(e.getStackTrace()));
        }



    }

}
