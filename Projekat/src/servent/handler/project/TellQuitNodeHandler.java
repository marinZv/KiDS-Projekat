package servent.handler.project;

import app.AppConfig;
import app.ChordState;
import cli.CLIParser;
import mutex.Mutex;
import servent.SimpleServentListener;
import servent.handler.MessageHandler;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.project.TellGoodbyeMessage;
import servent.message.project.TellQuitNodeMessage;

import java.util.Arrays;

public class TellQuitNodeHandler implements MessageHandler {

    private Message clientMessage;
    private Mutex mutex;
    private SimpleServentListener simpleServentListener;
    private CLIParser parser;

    public TellQuitNodeHandler(Message clientMessage, Mutex mutex, SimpleServentListener simpleServentListener, CLIParser parser) {
        this.clientMessage = clientMessage;
        this.mutex = mutex;
        this.parser= parser;
        this.simpleServentListener = simpleServentListener;

    }

    @Override
    public void run() {
        try {

            if(MessageType.TELL_QUIT_NODE == clientMessage.getMessageType()) {

                TellQuitNodeMessage message = (TellQuitNodeMessage) clientMessage;
                mutex.unlock(true);

                synchronized (ChordState.LOCK){
                    ChordState.LOCK.wait();
                }
                simpleServentListener.stop();
                parser.stop();

            } else {
                AppConfig.timestampedErrorPrint("TellQuitNodeHandler got a message that's not TELL_QUIT");
            }

        } catch (Exception e) {
            AppConfig.timestampedErrorPrint("Exception in GoodbyeTellHandler. Message : " + e.getMessage() + "\nError : " + Arrays.toString(e.getStackTrace()));
        }
    }
}
