package servent.handler.project;

import app.AppConfig;
import app.ChordState;
import app.MyFile;
import app.ServentInfo;
import servent.handler.MessageHandler;
import servent.message.AskGetMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.TellGetMessage;
import servent.message.project.TellViewFileMessage;
import servent.message.project.ViewFileMessage;
import servent.message.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewFileHandler implements MessageHandler {

    private Message clientMessage;

    public ViewFileHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.VIEW_FILE) {

            ViewFileMessage viewFileMessage = (ViewFileMessage) clientMessage;

            try {
                int key = viewFileMessage.getKey();
                if (AppConfig.myServentInfo.getChordId() == key) {
                    System.out.println("Usao sam u if za viewHandler");
                    Map<Integer, List<MyFile>> valueMap = AppConfig.chordState.getValueMap();
                    List<MyFile> value = new ArrayList<>();
                    //ovde treba poslati fajlove u zavisnosti od prijateljstva
                    boolean all = false;
                    if(AppConfig.chordState.isMyFriend(viewFileMessage.getInitiator())){
                        all = true;
                    }
                    for(Map.Entry<Integer, List<MyFile>> entry : valueMap.entrySet()){
                        if(all){
                            value.addAll(entry.getValue());
                        }else{
                            for(MyFile file : entry.getValue()){
                                if(file.getVisibility().equalsIgnoreCase("public")){
                                    value.add(file);
                                }
                            }
                        }
                    }

//                    TellViewFileMessage tellViewFileMessage = new TellViewFileMessage(AppConfig.myServentInfo.getListenerPort(), clientMessage.getSenderPort(), value);
                    TellViewFileMessage tellViewFileMessage = new TellViewFileMessage(AppConfig.myServentInfo.getListenerPort(), viewFileMessage.getInitiator().getListenerPort(), value);
                    MessageUtil.sendMessage(tellViewFileMessage);
                    System.out.println("Poslao sam tell View");
                } else {
                    AppConfig.chordState.getValue(key, viewFileMessage.getInitiator());
                }
            } catch (NumberFormatException e) {
                AppConfig.timestampedErrorPrint("Got ask get with bad text: " + clientMessage.getMessageText());
            }

        } else {
            AppConfig.timestampedErrorPrint("Ask get handler got a message that is not ASK_GET");
        }
    }
}
