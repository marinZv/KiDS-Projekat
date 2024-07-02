package servent.handler.project;

import app.AppConfig;
import app.ServentInfo;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.project.RemoveUpdateMessage;
import servent.message.project.TellQuitNodeMessage;
import servent.message.util.MessageUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RemoveUpdateHandler implements MessageHandler {

    private Message clientMessage;

    public RemoveUpdateHandler(Message message) {
        this.clientMessage = message;
    }

    @Override
    public void run() {

        try {

            if(clientMessage.getMessageType() == MessageType.REMOVE_UPDATE) {

                RemoveUpdateMessage removeUpdateMessage = (RemoveUpdateMessage) clientMessage;

                List<ServentInfo> nodes = new ArrayList<ServentInfo>();
                nodes.add(removeUpdateMessage.getToRemove());
                AppConfig.chordState.removeNode(nodes);

                if(removeUpdateMessage.getSenderPort() != AppConfig.myServentInfo.getListenerPort()) {

                    RemoveUpdateMessage newRemoveUpdateMessage = new RemoveUpdateMessage(removeUpdateMessage.getSender(), AppConfig.chordState.getNextNode(), removeUpdateMessage.getToRemove());
                    MessageUtil.sendMessage(newRemoveUpdateMessage);

                } else if ((removeUpdateMessage.getSenderPort() == AppConfig.myServentInfo.getListenerPort()) || (AppConfig.chordState.getAllNodeInfo().size() == 0)){

                    TellQuitNodeMessage tellMessage = new TellQuitNodeMessage(AppConfig.myServentInfo, removeUpdateMessage.getToRemove().getListenerPort());
                    MessageUtil.sendMessage(tellMessage);
                }

            } else {
                AppConfig.timestampedErrorPrint("RemoveUpdateHandler got a message that's not REMOVE_UPDATE");
            }

        } catch (Exception e) {
            AppConfig.timestampedErrorPrint("Exception in RemoveUpdateHandler. Message : " + e.getMessage() + "\nError : " + Arrays.toString(e.getStackTrace()));
        }

    }

}
