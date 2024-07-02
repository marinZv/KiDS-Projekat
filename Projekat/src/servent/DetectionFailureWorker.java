package servent;

import app.AppConfig;
import app.Cancellable;
import app.ChordState;
import mutex.Mutex;
import servent.message.project.BuddyMessage;
import servent.message.project.PingMessage;
import servent.message.util.MessageUtil;

import java.util.concurrent.atomic.AtomicBoolean;

public class DetectionFailureWorker implements Runnable, Cancellable {

    private volatile boolean working;
    private Mutex mutex;

    public DetectionFailureWorker(Mutex mutex) {
        working = true;
        this.mutex = mutex;
    }

    @Override
    public void run() {

        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


//        AppConfig.chordState.getPingPongMap().get(AppConfig.chordState.getPredecessor())
        AppConfig.chordState.getPingPongMap().put(AppConfig.chordState.getPredecessor().getListenerPort(), new AtomicBoolean(false));
        System.out.println("Ovo je ping pong mapa kad se startuje detection failure worker");
        System.out.println(AppConfig.chordState.getPingPongMap().get(AppConfig.chordState.getPredecessor().getListenerPort()));
        while(working){

            PingMessage pingMessage = new PingMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getPredecessor().getListenerPort());

            MessageUtil.sendMessage(pingMessage);
            try {
                Thread.sleep(AppConfig.chordState.WEAK);
            } catch (InterruptedException e) {
                AppConfig.timestampedErrorPrint("Puklo je cekajuci na Pong poruku");
                throw new RuntimeException(e);
            }

            System.out.println("Moj prethodnik je " + AppConfig.chordState.getPredecessor());
            System.out.println("Moj sledbenik je " + AppConfig.chordState.getNextNode());

            if(!AppConfig.chordState.getPingPongMap().get(AppConfig.chordState.getPredecessor().getListenerPort()).get()){
                //Ovde treba poslati mom buddy da posalje isAlive poruku
                BuddyMessage buddyMessage = new BuddyMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getNextNode().getListenerPort(), AppConfig.chordState.getPredecessor());
                MessageUtil.sendMessage(buddyMessage);
            }else{
                AppConfig.chordState.getPingPongMap().get(AppConfig.chordState.getPredecessor().getListenerPort()).set(false);
                continue;
            }

            //Ovo treba zameniti sa wait
//            try {
//                Thread.sleep(13000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }

            synchronized (ChordState.IS_ALIVE_LOCK){
                try {
                    ChordState.IS_ALIVE_LOCK.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            if(!AppConfig.chordState.getIsAlive().get()){
                //Ovde treba zapoceti proces restruktuiranja
                mutex.lock();
                AppConfig.chordState.restructureSystem();
            }
        }
    }


    @Override
    public void stop() {
        this.working = true;
    }
}
