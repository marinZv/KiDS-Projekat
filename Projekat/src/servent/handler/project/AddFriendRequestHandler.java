package servent.handler.project;

import app.AppConfig;
import app.ChordState;
import app.ServentInfo;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.project.AddFriendReplyMessage;
import servent.message.project.AddFriendRequestMessage;
import servent.message.util.MessageUtil;

public class AddFriendRequestHandler implements MessageHandler {

    private Message clientMessage;

    public AddFriendRequestHandler(Message clientMessage){
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.ADD_FRIEND_REQUEST){

            AddFriendRequestMessage addFriendRequestMessage = (AddFriendRequestMessage) clientMessage;
            if(AppConfig.myServentInfo.getListenerPort() == addFriendRequestMessage.getReceiverPort()){
                System.out.println("Dosao je zahtev za prijateljstvo do mene");
                AppConfig.chordState.addToFriends(addFriendRequestMessage.getInitiator());
                System.out.println("Moji prijatelji su sada " + AppConfig.chordState.getFriends());
                AddFriendReplyMessage addFriendReplyMessage = new AddFriendReplyMessage(AppConfig.myServentInfo.getListenerPort(), addFriendRequestMessage.getSenderPort(), AppConfig.myServentInfo);
                MessageUtil.sendMessage(addFriendReplyMessage);
                System.out.println("Poslao sam reply poruku " + addFriendReplyMessage.getReceiverPort() + " ");
            }else{
                ServentInfo nextNode = AppConfig.chordState.getNextNodeForFriend(ChordState.chordHash(addFriendRequestMessage.getReceiverPort()));
                AddFriendRequestMessage newAddFriendRequest = new AddFriendRequestMessage(AppConfig.myServentInfo.getListenerPort(), nextNode.getListenerPort(), addFriendRequestMessage.getInitiator());
                MessageUtil.sendMessage(newAddFriendRequest);
            }

        }

    }
}
