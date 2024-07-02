package cli.command.project;

import app.AppConfig;
import app.ChordState;
import app.MyFile;
import cli.command.CLICommand;
import mutex.Mutex;

import java.io.File;

public class RemoveFileCommand implements CLICommand {

    private Mutex mutex;

    public RemoveFileCommand(Mutex mutex){
        this.mutex = mutex;
    }


    @Override
    public String commandName() {
        return "remove_file";
    }

    @Override
    public void execute(String args) {
        String[] splitArgs = args.split(" ");

        if (splitArgs.length == 1) {
            String path = null;
//            String visibility = null;
            try {
                path = splitArgs[0];
//                visibility = splitArgs[1];

                if (path == null) {
                    throw new Exception("path is not defined");
                }
//                if (visibility == null) {
//                    throw new Exception("visibility is not defined");
//                }

//                AppConfig.chordState.putValue(hashPath(path), new MyFile(new File(path), path, visibility), AppConfig.myServentInfo);

                mutex.lock();
                AppConfig.timestampedStandardPrint("Usao sam u lock za remove file");

                AppConfig.chordState.deleteValue(hashPath(path), new MyFile(new File(path), path, null), AppConfig.myServentInfo);
            } catch (NumberFormatException e) {
                AppConfig.timestampedErrorPrint("Invalid key and value pair" + ChordState.CHORD_SIZE
                        + " ");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            AppConfig.timestampedErrorPrint("Invalid arguments for remove_file");
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
