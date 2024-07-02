package servent.handler.project;

import app.AppConfig;
import app.ChordState;
import app.ServentInfo;
import mutex.Mutex;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.project.AddFriendReplyMessage;
import servent.message.util.MessageUtil;

public class AddFriendReplyHandler implements MessageHandler {

    private Message clientMessage;
    private Mutex mutex;


    public AddFriendReplyHandler(Message clientMessage, Mutex mutex) {
        this.clientMessage = clientMessage;
        this.mutex = mutex;
    }

    @Override
    public void run() {
        if(MessageType.ADD_FRIEND_REPLY == clientMessage.getMessageType()){

            AddFriendReplyMessage addFriendReplyMessage = (AddFriendReplyMessage) clientMessage;

            if(AppConfig.myServentInfo.getListenerPort() == addFriendReplyMessage.getReceiverPort()){
                System.out.println("Odgovoren mi je zahtev na prijateljstvo");
                AppConfig.chordState.addToFriends(addFriendReplyMessage.getInitiator());
                mutex.unlock(false);
                System.out.println("otpustio sam lock za add_friend");
            }else{
                ServentInfo nextNode = AppConfig.chordState.getNextNodeForFriend(ChordState.chordHash(addFriendReplyMessage.getReceiverPort()));
                AddFriendReplyMessage newAddFriendReplyMessage = new AddFriendReplyMessage(AppConfig.myServentInfo.getListenerPort(), nextNode.getListenerPort(), addFriendReplyMessage.getInitiator());
                System.out.println("stigla je friend reply poruka, ali nije namenjena meni, saljem dalje ka " + nextNode.getListenerPort());
                MessageUtil.sendMessage(newAddFriendReplyMessage);
            }
        }
    }
}
