package servent.message;

public class NewNodeMessage extends BasicMessage {

	private static final long serialVersionUID = 3899837286642127636L;

	private int rbs;

	public NewNodeMessage(int senderPort, int receiverPort, int rbs) {
		super(MessageType.NEW_NODE, senderPort, receiverPort);
		this.rbs = rbs;
	}

	public int getRbs() {
		return rbs;
	}
}
