package cli.command.project;

import app.AppConfig;
import app.ChordState;
import app.MyFile;
import cli.command.CLICommand;
import mutex.Mutex;

import java.io.File;

public class AddFileCommand implements CLICommand {

    private Mutex mutex;

    public AddFileCommand(Mutex mutex){
        this.mutex = mutex;
    }

	@Override
	public String commandName() {
		return "add_file";
	}

	@Override
	public void execute(String args) {
		String[] splitArgs = args.split(" ");

        System.out.println(splitArgs.length);

		if (splitArgs.length == 2) {
			String path = null;
			String visibility = null;
			try {
                path = splitArgs[0];
                visibility = splitArgs[1];

				if (path == null) {
					throw new Exception("path is not defined");
				}
				if (visibility == null) {
					throw new Exception("visibility is not defined");
				}

				visibility.toLowerCase();
                mutex.lock();
				AppConfig.timestampedStandardPrint("Usao sam u lock za add_file");

				System.out.println("Hash za fajl.txt " +  hashPath(path));
				AppConfig.chordState.putValue(hashPath(path), new MyFile(new File(path), path, visibility), AppConfig.myServentInfo);
			} catch (NumberFormatException e) {
				AppConfig.timestampedErrorPrint("Invalid key and value pair. Path and visibility should be string and listed" + ChordState.CHORD_SIZE
						+ " ");
			} catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
			AppConfig.timestampedErrorPrint("Invalid arguments for add_file");
		}

	}

    private int hashPath(String path){
        int sum = 0;
        for(int i = 0; i < path.length(); i++){
            sum += path.charAt(i);
        }

        int key = ChordState.chordHash(sum);

        return key;
    }

}
