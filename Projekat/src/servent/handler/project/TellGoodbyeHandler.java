package servent.handler.project;

import app.AppConfig;
import cli.CLIParser;
import mutex.Mutex;
import mutex.SuzukiKasamiMutex;
import servent.SimpleServentListener;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.project.TellGoodbyeMessage;

import java.util.Arrays;

public class TellGoodbyeHandler implements MessageHandler {

    private Message clientMessage;
    private Mutex mutex;
    private SimpleServentListener simpleServentListener;
    private CLIParser parser;

    public TellGoodbyeHandler(Message clientMessage, Mutex mutex, SimpleServentListener simpleServentListener, CLIParser parser) {
        this.clientMessage = clientMessage;
        this.mutex = mutex;
        this.parser= parser;
        this.simpleServentListener = simpleServentListener;

    }

    @Override
    public void run() {
        try {

            if(MessageType.TELL_GOODBYE == clientMessage.getMessageType()) {

                TellGoodbyeMessage message = (TellGoodbyeMessage) clientMessage;
                mutex.unlock(true);

                simpleServentListener.stop();
                parser.stop();

            } else {
                AppConfig.timestampedErrorPrint("GoodbyeTellHandler got a message that's not GOODBYE_TELL");
            }

        } catch (Exception e) {
            AppConfig.timestampedErrorPrint("Exception in GoodbyeTellHandler. Message : " + e.getMessage() + "\nError : " + Arrays.toString(e.getStackTrace()));
        }
    }


}
