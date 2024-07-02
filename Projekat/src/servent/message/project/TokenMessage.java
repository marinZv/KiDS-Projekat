package servent.message.project;

import app.AppConfig;
import app.ChordState;
import app.ServentInfo;
import servent.message.BasicMessage;
import servent.message.MessageType;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class TokenMessage extends BasicMessage {

    private static final long serialVersionUID = 6979633641343130206L;

    private CopyOnWriteArrayList<Integer> LN;
    private ArrayBlockingQueue<ServentInfo> Q;

    private ServentInfo tokenReceiver;

    private ServentInfo sender;
    private ServentInfo receiver;

    public TokenMessage(ServentInfo sender, ServentInfo receiver, ServentInfo tokenReceiver){
        super(MessageType.TOKEN, sender.getListenerPort(), receiver.getListenerPort());

        this.tokenReceiver = tokenReceiver;
        this.sender = sender;
        this.receiver = receiver;

        LN = new CopyOnWriteArrayList<>();
        for (int i = 0; i < ChordState.CHORD_SIZE; i++) {
            LN.add(0);
        }

        Q = new ArrayBlockingQueue(AppConfig.SERVENT_COUNT);
    }

    public TokenMessage(ServentInfo sender, ServentInfo receiver, ServentInfo tokenReceiver, CopyOnWriteArrayList<Integer> LN, ArrayBlockingQueue Q){
        super(MessageType.TOKEN, sender.getListenerPort(), receiver.getListenerPort());
        this.tokenReceiver = tokenReceiver;
        this.sender = sender;
        this.receiver = receiver;
        this.LN = LN;
        this.Q = Q;
    }


    public TokenMessage createNewToken(ServentInfo sender, ServentInfo receiver, ServentInfo tokenReceiver){
        return new TokenMessage(sender, receiver, tokenReceiver, new CopyOnWriteArrayList<>(LN), Q);
    }

    public CopyOnWriteArrayList<Integer> getLN() {
        return LN;
    }

    public void setLN(CopyOnWriteArrayList<Integer> LN) {
        this.LN = LN;
    }

    public ArrayBlockingQueue<ServentInfo> getQ() {
        return Q;
    }

    public void setQ(ArrayBlockingQueue q) {
        Q = q;
    }

    public ServentInfo getTokenReceiver() {
        return tokenReceiver;
    }

    public void setTokenReceiver(ServentInfo tokenReceiver) {
        this.tokenReceiver = tokenReceiver;
    }

    public ServentInfo getSender() {
        return sender;
    }

    public ServentInfo getReceiver() {
        return receiver;
    }
}
