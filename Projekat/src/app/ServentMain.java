package app;

import cli.CLIParser;
import mutex.Mutex;
import mutex.SuzukiKasamiMutex;
import servent.DetectionFailureWorker;
import servent.SimpleServentListener;

/**
 * Describes the procedure for starting a single Servent
 *
 * @author bmilojkovic
 */
public class ServentMain {

	/**
	 * Command line arguments are:
	 * 0 - path to servent list file
	 * 1 - this servent's id
	 */
	public static void main(String[] args) {
		if (args.length != 2) {
			AppConfig.timestampedErrorPrint("Please provide servent list file and id of this servent.");
		}
		
		int serventId = -1;
		int portNumber = -1;
		
		String serventListFile = args[0];
		
		try {
			serventId = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			AppConfig.timestampedErrorPrint("Second argument should be an int. Exiting...");
			System.exit(0);
		}
		
		AppConfig.readConfig(serventListFile, serventId);
		
		try {
			portNumber = AppConfig.myServentInfo.getListenerPort();
			
			if (portNumber < 1000 || portNumber > 2000) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			AppConfig.timestampedErrorPrint("Port number should be in range 1000-2000. Exiting...");
			System.exit(0);
		}
		
		AppConfig.timestampedStandardPrint("Starting servent " + AppConfig.myServentInfo);

		Mutex mutex = new SuzukiKasamiMutex();

		SimpleServentListener simpleListener = new SimpleServentListener(mutex);
		Thread listenerThread = new Thread(simpleListener);
		listenerThread.start();


		//Ovo treba otkomentarisati da bi se videlo da li radi
		DetectionFailureWorker detectionFailureWorker = new DetectionFailureWorker(mutex);
		Thread detectionFailureThread = new Thread(detectionFailureWorker);
		detectionFailureThread.start();

		CLIParser cliParser = new CLIParser(simpleListener, detectionFailureWorker);
		simpleListener.setParser(cliParser);
		Thread cliThread = new Thread(cliParser);
		cliThread.start();
		
		ServentInitializer serventInitializer = new ServentInitializer();
		Thread initializerThread = new Thread(serventInitializer);
		initializerThread.start();

	}
}
