package cli.command.project;

import app.AppConfig;
import app.ChordState;
import app.MyFile;
import cli.command.CLICommand;
import mutex.Mutex;
import servent.message.project.AddFriendRequestMessage;
import servent.message.project.ViewFileMessage;
import servent.message.util.MessageUtil;

import java.util.List;

public class ViewFileCommand implements CLICommand {

    private Mutex mutex;

    public ViewFileCommand(Mutex mutex){
        this.mutex = mutex;
    }

    @Override
	public String commandName() {
		return "view_file";
	}

	@Override
	public void execute(String args) {
        String[] splitArgs = args.split(" ");

        if (splitArgs.length == 2) {
            String host = null;
            int port = 0;
            try {
                host = splitArgs[0];
                port = Integer.parseInt(splitArgs[1]);

                if (host == null) {
                    throw new Exception("host is not defined");
                }
                if (port < 1000 || port > 3000) {
                    throw new Exception("Bad port defined");
                }

                mutex.lock();
                System.out.println("Usao sam u lock za view file");

                List<MyFile> values = AppConfig.chordState.getValue(ChordState.chordHash(port), AppConfig.myServentInfo);

                if (values != null) {
                    mutex.unlock(false);
                    AppConfig.timestampedStandardPrint("Otkljucao sam lock za u view_file komandi");
                }

            } catch (NumberFormatException e) {
                AppConfig.timestampedErrorPrint("Invalid key and value pair." + ChordState.CHORD_SIZE
                        + " ");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            AppConfig.timestampedErrorPrint("Invalid arguments for view_file");
        }
    }

}
