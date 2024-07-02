package cli.command;

import app.AppConfig;
import cli.CLIParser;
import servent.DetectionFailureWorker;
import servent.SimpleServentListener;

public class StopCommand implements CLICommand {

	private CLIParser parser;
	private SimpleServentListener listener;
	private DetectionFailureWorker detectionFailureWorker;
	
	public StopCommand(CLIParser parser, SimpleServentListener listener, DetectionFailureWorker detectionFailureWorker) {
		this.parser = parser;
		this.listener = listener;
		this.detectionFailureWorker = detectionFailureWorker;
	}
	
	@Override
	public String commandName() {
		return "stop";
	}

	@Override
	public void execute(String args) {
		AppConfig.timestampedStandardPrint("Stopping...");
		parser.stop();
		listener.stop();
		detectionFailureWorker.stop();
	}

}
