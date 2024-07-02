package cli.command.project;

import app.AppConfig;
import app.ChordState;
import cli.command.CLICommand;
import mutex.Mutex;
import servent.message.project.AddFriendRequestMessage;
import servent.message.util.MessageUtil;

public class AddFriendCommand implements CLICommand {

    private Mutex mutex;

    public AddFriendCommand(Mutex mutex){
        this.mutex = mutex;
    }

    @Override
    public String commandName() {
        return "add_friend";
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
                AppConfig.timestampedStandardPrint("Usao sam u lock za add_friend");

                AddFriendRequestMessage addFriendRequestMessage = new AddFriendRequestMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getNextNodeForFriend(ChordState.chordHash(port)).getListenerPort(), AppConfig.myServentInfo);
                MessageUtil.sendMessage(addFriendRequestMessage);
            } catch (NumberFormatException e) {
                AppConfig.timestampedErrorPrint("Invalid key and value pair." + ChordState.CHORD_SIZE
                        + " ");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            AppConfig.timestampedErrorPrint("Invalid arguments for add_friend");
        }

    }

}
