package servent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.AppConfig;
import app.Cancellable;
import cli.CLIParser;
import mutex.Mutex;
import mutex.SuzukiKasamiMutex;
import servent.handler.AskGetHandler;
import servent.handler.MessageHandler;
import servent.handler.NewNodeHandler;
import servent.handler.NullHandler;
import servent.handler.PutHandler;
import servent.handler.SorryHandler;
import servent.handler.TellGetHandler;
import servent.handler.UpdateHandler;
import servent.handler.WelcomeHandler;
import servent.handler.project.*;
import servent.message.Message;
import servent.message.util.MessageUtil;

public class SimpleServentListener implements Runnable, Cancellable {

	private volatile boolean working = true;

	private Mutex mutex;
	private CLIParser parser;

	public SimpleServentListener(Mutex mutex) {
		this.mutex =  mutex;
	}

	/*
	 * Thread pool for executing the handlers. Each client will get it's own handler thread.
	 */
	private final ExecutorService threadPool = Executors.newWorkStealingPool();
	
	@Override
	public void run() {
		ServerSocket listenerSocket = null;
		try {
			listenerSocket = new ServerSocket(AppConfig.myServentInfo.getListenerPort(), 100);
			/*
			 * If there is no connection after 1s, wake up and see if we should terminate.
			 */
			listenerSocket.setSoTimeout(1000);
		} catch (IOException e) {
			AppConfig.timestampedErrorPrint("Couldn't open listener socket on: " + AppConfig.myServentInfo.getListenerPort());
			System.exit(0);
		}
		
		
		while (working) {
			try {
				Message clientMessage;
				
				Socket clientSocket = listenerSocket.accept();
				
				//GOT A MESSAGE! <3
				clientMessage = MessageUtil.readMessage(clientSocket);
				
				MessageHandler messageHandler = new NullHandler(clientMessage);
				
				/*
				 * Each message type has it's own handler.
				 * If we can get away with stateless handlers, we will,
				 * because that way is much simpler and less error prone.
				 */
				switch (clientMessage.getMessageType()) {

					case NEW_NODE:
						messageHandler = new NewNodeHandler(clientMessage, (SuzukiKasamiMutex) mutex);
						break;
					case WELCOME:
						messageHandler = new WelcomeHandler(clientMessage);
						break;
					case SORRY:
						messageHandler = new SorryHandler(clientMessage);
						break;
					case UPDATE:
						messageHandler = new UpdateHandler(clientMessage);
						break;
//					case PUT:
//						messageHandler = new PutHandler(clientMessage);
//						break;
//					case ASK_GET:
//						messageHandler = new AskGetHandler(clientMessage);
//						break;
					case TELL_GET:
						messageHandler = new TellGetHandler(clientMessage);
						break;
					case POISON:
						break;
					case ADD_FRIEND_REQUEST:
						messageHandler = new AddFriendRequestHandler(clientMessage);
						break;
					case ADD_FRIEND_REPLY:
						messageHandler = new AddFriendReplyHandler(clientMessage, mutex);
						break;
					case TOKEN:
						messageHandler = new TokenHandler(clientMessage, mutex);
						break;
					case REQUEST_TOKEN:
						messageHandler = new RequestTokenHandler(clientMessage, mutex);
						break;
					case ADD_FILE:
						messageHandler = new AddFileHandler(clientMessage);
						break;
					case REMOVE_FILE:
						messageHandler = new RemoveFileHandler(clientMessage);
						break;
					case VIEW_FILE:
						messageHandler = new ViewFileHandler(clientMessage);
						break;
					case QUIT_NODE:
						messageHandler = new QuitNodeHandler(clientMessage, mutex);
						break;
					case TELL_ADD_FILE:
						messageHandler = new TellAddFileHandler(clientMessage, mutex);
						break;
					case TELL_REMOVE_FILE:
						messageHandler = new TellRemoveFileHandler(clientMessage, mutex);
						break;
					case TELL_ADD_FRIEND:
						messageHandler = new TellAddFriendHandler(clientMessage);
						break;
					case TELL_VIEW_FILE:
						messageHandler = new TellViewFileHandler(clientMessage, mutex);
						break;
					case TELL_QUIT_NODE:
						messageHandler = new TellQuitNodeHandler(clientMessage, mutex, this, parser);
						break;
					case GOODBYE:
						messageHandler = new GoodbyeHandler(clientMessage, mutex);
						break;
					case TELL_GOODBYE:
						messageHandler = new TellGoodbyeHandler(clientMessage, mutex, this, parser);
						break;
					case TELL_WELCOME:
						messageHandler = new TellWelcomeHandler(clientMessage, mutex);
						break;
					case REMOVE_UPDATE:
						messageHandler = new RemoveUpdateHandler(clientMessage);
						break;
					case BACKUP:
						messageHandler = new BackupHandler(clientMessage);
						break;
					case ALIVE:
						messageHandler = new AliveHandler(clientMessage);
						break;
					case BUDDY:
						messageHandler = new BuddyHandler(clientMessage);
						break;
					case CONFIRMATION:
						messageHandler = new ConfirmationHandler(clientMessage);
						break;
					case IS_ALIVE:
						messageHandler = new IsAliveHandler(clientMessage);
						break;
					case PING:
						messageHandler = new PingHandler(clientMessage);
						break;
					case PONG:
						messageHandler = new PongHandler(clientMessage);
						break;
					}

				
				threadPool.submit(messageHandler);
			} catch (SocketTimeoutException timeoutEx) {
				//Uncomment the next line to see that we are waking up every second.
//				AppConfig.timedStandardPrint("Waiting...");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void stop() {
		this.working = false;
	}

	public void setParser(CLIParser parser) {
		this.parser = parser;
	}

	public Mutex getMutex() {
		return mutex;
	}
}
