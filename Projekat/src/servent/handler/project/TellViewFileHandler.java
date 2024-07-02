package servent.handler.project;

import app.AppConfig;
import app.MyFile;
import mutex.Mutex;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.project.TellViewFileMessage;

public class TellViewFileHandler implements MessageHandler {

    private Message clientMessage;

    private Mutex mutex;

    public TellViewFileHandler(Message clientMessage, Mutex mutex) {
        this.clientMessage = clientMessage;
        this.mutex = mutex;
    }

    @Override
    public void run() {
        if(clientMessage.getMessageType() == MessageType.TELL_VIEW_FILE){
            TellViewFileMessage tellViewFileMessage = (TellViewFileMessage) clientMessage;

//            AppConfig.timestampedStandardPrint(tellViewFileMessage.toString());

            if(AppConfig.myServentInfo.getListenerPort() == tellViewFileMessage.getReceiverPort()) {


                AppConfig.timestampedStandardPrint("Dobio sam fajlove koje sam trazio, a to su");
                for (MyFile value : tellViewFileMessage.getValues()) {
                    AppConfig.timestampedStandardPrint(value.toString());
                }

                mutex.unlock(false);
                System.out.println("Otpustio sam lock za view_file u tell_view_file");
            }
        }
    }
}
