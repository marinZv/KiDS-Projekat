package servent.handler.project;

import app.AppConfig;
import app.ServentInfo;
import mutex.Mutex;
import mutex.SuzukiKasamiMutex;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.project.RequestTokenMessage;
import servent.message.project.TokenMessage;
import servent.message.util.MessageUtil;

import java.util.Arrays;

public class RequestTokenHandler implements MessageHandler {

    private Message clientMessage;

    private SuzukiKasamiMutex suzukiKasamiMutex;


    public RequestTokenHandler(Message clientMessage, Mutex mutex) {
        this.clientMessage = clientMessage;
        this.suzukiKasamiMutex = (SuzukiKasamiMutex) mutex;
    }

    @Override
    public void run() {
        try {

            if (clientMessage.getMessageType() == MessageType.REQUEST_TOKEN) {

                RequestTokenMessage requestTokenMessage = (RequestTokenMessage)clientMessage;

                if(clientMessage.getSenderPort() != AppConfig.myServentInfo.getListenerPort()) {

                    if(!suzukiKasamiMutex.messageIsOutdated(requestTokenMessage.getSender().getChordId(), requestTokenMessage.getSN())) {

                        if(suzukiKasamiMutex.getHasToken().get() && !suzukiKasamiMutex.getIsInCS().get() && suzukiKasamiMutex.checkOutstandingRequest(requestTokenMessage.getSender().getChordId())) {

                            ServentInfo tokenReceiver = requestTokenMessage.getSender();

                            ServentInfo receiver = AppConfig.chordState.getNextNodeForKey(tokenReceiver.getChordId());

                            TokenMessage token = suzukiKasamiMutex.getTokenMessage().createNewToken(AppConfig.myServentInfo, receiver, tokenReceiver);
                            MessageUtil.sendMessage(token);

                            suzukiKasamiMutex.getHasToken().set(false);
                            AppConfig.timestampedErrorPrint("RequestCSHandler : Node with ID " + AppConfig.myServentInfo.getChordId() + " sent TOKEN to node with ID " + receiver.getChordId());

                        }

                    }

                    RequestTokenMessage newMessage = new RequestTokenMessage(requestTokenMessage.getSender(), AppConfig.chordState.getSuccessorTable()[0], requestTokenMessage.getSN());
                    MessageUtil.sendMessage(newMessage);

                }

            } else {
                AppConfig.timestampedErrorPrint("Request CS handler got a message that is not a REQUEST_CS");
            }

        } catch (Exception e) {
            AppConfig.timestampedErrorPrint("Exception in RequestCSHandler. Message : " + e.getMessage() + "\nError : " + Arrays.toString(e.getStackTrace()));
        }

    }

}
