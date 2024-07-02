package servent.message;

import app.MyFile;

import java.util.List;
import java.util.Map;

public class WelcomeMessage extends BasicMessage {

	private static final long serialVersionUID = -8981406250652693908L;

	private Map<Integer, List<MyFile>> values;
	
	public WelcomeMessage(int senderPort, int receiverPort, Map<Integer, List<MyFile>> values) {
		super(MessageType.WELCOME, senderPort, receiverPort);
		
		this.values = values;
	}
	
	public Map<Integer, List<MyFile>> getValues() {
		return values;
	}
}
