package app;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import servent.message.AskGetMessage;
import servent.message.PutMessage;
import servent.message.WelcomeMessage;
import servent.message.project.*;
import servent.message.util.MessageUtil;

/**
 * This class implements all the logic required for Chord to function.
 * It has a static method <code>chordHash</code> which will calculate our chord ids.
 * It also has a static attribute <code>CHORD_SIZE</code> that tells us what the maximum
 * key is in our system.
 * 
 * Other public attributes and methods:
 * <ul>
 *   <li><code>chordLevel</code> - log_2(CHORD_SIZE) - size of <code>successorTable</code></li>
 *   <li><code>successorTable</code> - a map of shortcuts in the system.</li>
 *   <li><code>predecessorInfo</code> - who is our predecessor.</li>
 *   <li><code>valueMap</code> - DHT values stored on this node.</li>
 *   <li><code>init()</code> - should be invoked when we get the WELCOME message.</li>
 *   <li><code>isCollision(int chordId)</code> - checks if a servent with that Chord ID is already active.</li>
 *   <li><code>isKeyMine(int key)</code> - checks if we have a key locally.</li>
 *   <li><code>getNextNodeForKey(int key)</code> - if next node has this key, then return it, otherwise returns the nearest predecessor for this key from my successor table.</li>
 *   <li><code>addNodes(List<ServentInfo> nodes)</code> - updates the successor table.</li>
 *   <li><code>putValue(int key, int value)</code> - stores the value locally or sends it on further in the system.</li>
 *   <li><code>getValue(int key)</code> - gets the value locally, or sends a message to get it from somewhere else.</li>
 * </ul>
 * @author bmilojkovic
 *
 */
public class ChordState {

	public static int CHORD_SIZE;
	public static int chordHash(int value) {
		return 61 * value % CHORD_SIZE;
	}
	
	private int chordLevel; //log_2(CHORD_SIZE)
	
	private ServentInfo[] successorTable;
	private ServentInfo predecessorInfo;
	
	//we DO NOT use this to send messages, but only to construct the successor table
	private List<ServentInfo> allNodeInfo;
	
	private Map<Integer, List<MyFile>> valueMap;

	private List<ServentInfo> friends = new ArrayList<>();

	private Map<Integer, List<MyFile>> backupMap = new ConcurrentHashMap<>();


	public static Object LOCK = new Object();

	public ServentInfo backupPredecessor = null;

	private AtomicBoolean isAlive = new AtomicBoolean(false);

	public Long WEAK = 4000L;
	public Long STRONG = 11000L;

	public static Object IS_ALIVE_LOCK = new Object();

	private Map<Integer, AtomicBoolean> pingPongMap = new ConcurrentHashMap<>();

	public long isAliveTimeStart;
	public long isAliveTimeEnd;
	
	public ChordState() {
		this.chordLevel = 1;
		int tmp = CHORD_SIZE;
		while (tmp != 2) {
			if (tmp % 2 != 0) { //not a power of 2
				throw new NumberFormatException();
			}
			tmp /= 2;
			this.chordLevel++;
		}
		
		successorTable = new ServentInfo[chordLevel];
		for (int i = 0; i < chordLevel; i++) {
			successorTable[i] = null;
		}
		
		predecessorInfo = null;
		valueMap = new HashMap<>();
		allNodeInfo = new ArrayList<>();
	}
	
	/**
	 * This should be called once after we get <code>WELCOME</code> message.
	 * It sets up our initial value map and our first successor so we can send <code>UPDATE</code>.
	 * It also lets bootstrap know that we did not collide.
	 */
	public void init(WelcomeMessage welcomeMsg) {
		//set a temporary pointer to next node, for sending of update message
		successorTable[0] = new ServentInfo("localhost", welcomeMsg.getSenderPort());
		this.valueMap = welcomeMsg.getValues();
		
		//tell bootstrap this node is not a collider
		try {
			Socket bsSocket = new Socket("localhost", AppConfig.BOOTSTRAP_PORT);
			
			PrintWriter bsWriter = new PrintWriter(bsSocket.getOutputStream());
			bsWriter.write("New\n" + AppConfig.myServentInfo.getListenerPort() + "\n");
			
			bsWriter.flush();
			bsSocket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getChordLevel() {
		return chordLevel;
	}
	
	public ServentInfo[] getSuccessorTable() {
		return successorTable;
	}
	
	public int getNextNodePort() {
		return successorTable[0].getListenerPort();
	}
	
	public ServentInfo getPredecessor() {
		return predecessorInfo;
	}
	
	public void setPredecessor(ServentInfo newNodeInfo) {
		this.predecessorInfo = newNodeInfo;
	}

	public Map<Integer, List<MyFile>> getValueMap() {
		return valueMap;
	}
	
	public void setValueMap(Map<Integer, List<MyFile>> valueMap) {
		this.valueMap = valueMap;
	}
	
	public boolean isCollision(int chordId) {
		if (chordId == AppConfig.myServentInfo.getChordId()) {
			return true;
		}
		for (ServentInfo serventInfo : allNodeInfo) {
			if (serventInfo.getChordId() == chordId) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns true if we are the owner of the specified key.
	 */
	public boolean isKeyMine(int key) {
		if (predecessorInfo == null) {
			return true;
		}
		
		int predecessorChordId = predecessorInfo.getChordId();
		int myChordId = AppConfig.myServentInfo.getChordId();
		
		if (predecessorChordId < myChordId) { //no overflow
			if (key <= myChordId && key > predecessorChordId) {
				return true;
			}
		} else { //overflow
			if (key <= myChordId || key > predecessorChordId) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Main chord operation - find the nearest node to hop to to find a specific key.
	 * We have to take a value that is smaller than required to make sure we don't overshoot.
	 * We can only be certain we have found the required node when it is our first next node.
	 */
	public ServentInfo getNextNodeForKey(int key) {
		if (isKeyMine(key)) {
			return AppConfig.myServentInfo;
		}
		
		//normally we start the search from our first successor
		int startInd = 0;
		
		//if the key is smaller than us, and we are not the owner,
		//then all nodes up to CHORD_SIZE will never be the owner,
		//so we start the search from the first item in our table after CHORD_SIZE
		//we know that such a node must exist, because otherwise we would own this key
		if (key < AppConfig.myServentInfo.getChordId()) {
			int skip = 1;
			while (successorTable[skip].getChordId() > successorTable[startInd].getChordId()) {
				startInd++;
				skip++;
			}
		}
		
		int previousId = successorTable[startInd].getChordId();
		
		for (int i = startInd + 1; i < successorTable.length; i++) {
			if (successorTable[i] == null) {
				AppConfig.timestampedErrorPrint("Couldn't find successor for " + key);
				break;
			}
			
			int successorId = successorTable[i].getChordId();
			
			if (successorId >= key) {
				return successorTable[i-1];
			}
			if (key > previousId && successorId < previousId) { //overflow
				return successorTable[i-1];
			}
			previousId = successorId;
		}
		//if we have only one node in all slots in the table, we might get here
		//then we can return any item
		return successorTable[0];
	}

	private void updateSuccessorTable() {
		//first node after me has to be successorTable[0]
		
		int currentNodeIndex = 0;
		ServentInfo currentNode = allNodeInfo.get(currentNodeIndex);
		successorTable[0] = currentNode;
		
		int currentIncrement = 2;
		
		ServentInfo previousNode = AppConfig.myServentInfo;
		
		//i is successorTable index
		for(int i = 1; i < chordLevel; i++, currentIncrement *= 2) {
			//we are looking for the node that has larger chordId than this
			int currentValue = (AppConfig.myServentInfo.getChordId() + currentIncrement) % CHORD_SIZE;
			
			int currentId = currentNode.getChordId();
			int previousId = previousNode.getChordId();
			
			//this loop needs to skip all nodes that have smaller chordId than currentValue
			while (true) {
				if (currentValue > currentId) {
					//before skipping, check for overflow
					if (currentId > previousId || currentValue < previousId) {
						//try same value with the next node
						previousId = currentId;
						currentNodeIndex = (currentNodeIndex + 1) % allNodeInfo.size();
						currentNode = allNodeInfo.get(currentNodeIndex);
						currentId = currentNode.getChordId();
					} else {
						successorTable[i] = currentNode;
						break;
					}
				} else { //node id is larger
					ServentInfo nextNode = allNodeInfo.get((currentNodeIndex + 1) % allNodeInfo.size());
					int nextNodeId = nextNode.getChordId();
					//check for overflow
					if (nextNodeId < currentId && currentValue <= nextNodeId) {
						//try same value with the next node
						previousId = currentId;
						currentNodeIndex = (currentNodeIndex + 1) % allNodeInfo.size();
						currentNode = allNodeInfo.get(currentNodeIndex);
						currentId = currentNode.getChordId();
					} else {
						successorTable[i] = currentNode;
						break;
					}
				}
			}
		}

		for (ServentInfo serventInfo : successorTable) {
			System.out.println(serventInfo);
		}
		
	}

	/**
	 * This method constructs an ordered list of all nodes. They are ordered by chordId, starting from this node.
	 * Once the list is created, we invoke <code>updateSuccessorTable()</code> to do the rest of the work.
	 * 
	 */
	public void addNodes(List<ServentInfo> newNodes) {
		allNodeInfo.addAll(newNodes);
		
		allNodeInfo.sort(new Comparator<ServentInfo>() {
			
			@Override
			public int compare(ServentInfo o1, ServentInfo o2) {
				return o1.getChordId() - o2.getChordId();
			}
			
		});
		
		List<ServentInfo> newList = new ArrayList<>();
		List<ServentInfo> newList2 = new ArrayList<>();
		
		int myId = AppConfig.myServentInfo.getChordId();
		for (ServentInfo serventInfo : allNodeInfo) {
			if (serventInfo.getChordId() < myId) {
				newList2.add(serventInfo);
			} else {
				newList.add(serventInfo);
			}
		}
		
		allNodeInfo.clear();
		allNodeInfo.addAll(newList);
		allNodeInfo.addAll(newList2);
		if (newList2.size() > 0) {
			predecessorInfo = newList2.get(newList2.size()-1);
		} else {
			predecessorInfo = newList.get(newList.size()-1);
		}
		
		updateSuccessorTable();
	}

	/**
	 * The Chord put operation. Stores locally if key is ours, otherwise sends it on.
	 */
	public void putValue(int key, MyFile value, ServentInfo initiator) {
		if (isKeyMine(key)) {
			System.out.println("Ulazim u isKeyMine za putValue");
//			valueMap.put(key, value);
			if(!valueMap.containsKey(key)){
				valueMap.put(key, new ArrayList<>());
			}
			valueMap.get(key).add(value);
			AppConfig.timestampedStandardPrint("Sada su moji fajlovi ");
			for(Map.Entry<Integer, List<MyFile>> entry : valueMap.entrySet()){
				for(MyFile file : entry.getValue()){
					AppConfig.timestampedStandardPrint(file.toString());
				}
			}

			createFileInFileSystem(value);

			BackupMessage backupMessage = new BackupMessage(AppConfig.myServentInfo, AppConfig.chordState.getNextNode(), value, predecessorInfo);
			AppConfig.timestampedStandardPrint("Poslao sam backup message sledbeniku");
			MessageUtil.sendMessage(backupMessage);

			ServentInfo receiver = getNextNodeForKey(initiator.getChordId());
			TellAddFileMessage tellAddFileMessage = new TellAddFileMessage(AppConfig.myServentInfo.getListenerPort(), receiver.getListenerPort(), initiator);
			AppConfig.timestampedStandardPrint("Poslao sam tellAddFileMessage");
			MessageUtil.sendMessage(tellAddFileMessage);

		} else {
			ServentInfo nextNode = getNextNodeForKey(key);
			AddFileMessage addFileMessage = new AddFileMessage(AppConfig.myServentInfo.getListenerPort(), nextNode.getListenerPort(), key, value, initiator);
			MessageUtil.sendMessage(addFileMessage);
		}
	}
	
	/**
	 * The chord get operation. Gets the value locally if key is ours, otherwise asks someone else to give us the value.
	 * @return <ul>
	 *			<li>The value, if we have it</li>
	 *			<li>-1 if we own the key, but there is nothing there</li>
	 *			<li>-2 if we asked someone else</li>
	 *		   </ul>
	 */
	public List<MyFile> getValue(int key, ServentInfo initiator) {
//		if (isKeyMine(key)) {
//			if (valueMap.containsKey(key)) {
//				return valueMap.get(key);
//			} else {
//				return null;
//			}
//		}

		List<MyFile> values = new ArrayList<>();
		if(AppConfig.myServentInfo.getChordId() == key){
			for(Map.Entry<Integer, List<MyFile>> entry : valueMap.entrySet()){
				values.addAll(entry.getValue());
			}

			return values;
		}
		
		ServentInfo nextNode = getNextNodeForKey(key);
		ViewFileMessage viewFileMessage = new ViewFileMessage(AppConfig.myServentInfo.getListenerPort(), nextNode.getListenerPort(), key, initiator);
		MessageUtil.sendMessage(viewFileMessage);
		
		return null;
	}

	public void deleteValue(int key, MyFile value, ServentInfo initiator) {
		if (isKeyMine(key)) {
			if(valueMap.get(key).contains(value)) {
				valueMap.get(key).remove(value);
				AppConfig.timestampedStandardPrint("Nakon brisanja moja valueMap izgleda:");
				for (Map.Entry<Integer, List<MyFile>> entry : valueMap.entrySet()){
					for(MyFile v : entry.getValue()){
						AppConfig.timestampedStandardPrint(v.toString());
					}
				}
				deleteFileFromFileSystem(value);
				ServentInfo receiver = getNextNodeForKey(initiator.getChordId());
				TellRemoveFileMessage tellRemoveFileMessage = new TellRemoveFileMessage(AppConfig.myServentInfo.getListenerPort(), receiver.getListenerPort(), initiator);
				MessageUtil.sendMessage(tellRemoveFileMessage);
				System.out.println("Poslao sam tell remove file ka " + receiver);
			}
		} else {
			ServentInfo nextNode = getNextNodeForKey(key);
			RemoveFileMessage removeFileMessage = new RemoveFileMessage(AppConfig.myServentInfo.getListenerPort(), nextNode.getListenerPort(), key, value, initiator);
			MessageUtil.sendMessage(removeFileMessage);
		}
	}

	public List<ServentInfo> getAllNodeInfo() {
		return allNodeInfo;
	}


	public void acceptingData(Map<Integer, List<MyFile>> newValues, ServentInfo newPredecessor){
		this.predecessorInfo = newPredecessor;
		this.valueMap.putAll(newValues);


		//Ovo treba proveriti da li radi, ako radi, super!
//		for(Map.Entry<Integer, List<MyFile>> entry : newValues.entrySet()){
//			for(MyFile myFile : entry.getValue()){
//				createFileInFileSystem(myFile);
//			}
//		}

	}

	public ServentInfo getNextNode(){
		return successorTable[0];
	}

	public void addToFriends(ServentInfo friend){
		friends.add(friend);
	}

	public List<ServentInfo> getFriends() {
		return friends;
	}

	public ServentInfo getNextNodeForFriend(int key) {

		//normally we start the search from our first successor
		int startInd = 0;

		//if the key is smaller than us, and we are not the owner,
		//then all nodes up to CHORD_SIZE will never be the owner,
		//so we start the search from the first item in our table after CHORD_SIZE
		//we know that such a node must exist, because otherwise we would own this key

		int previousId = successorTable[startInd].getChordId();

		for (int i = startInd + 1; i < successorTable.length; i++) {
			if (successorTable[i] == null) {
				AppConfig.timestampedErrorPrint("Couldn't find successor for " + key);
				break;
			}

			int successorId = successorTable[i].getChordId();

			if (successorId >= key) {
				return successorTable[i-1];
			}
			if (key > previousId && successorId < previousId) { //overflow
				return successorTable[i-1];
			}
			previousId = successorId;
		}
		//if we have only one node in all slots in the table, we might get here
		//then we can return any item
		return successorTable[0];
	}


	public void turnOffNode() {
		ServentInfo sender = AppConfig.myServentInfo;
		ServentInfo receiver = this.successorTable[0];

		String message = "Node " + sender.getListenerPort() + " is turning off!";

		if(allNodeInfo.size() > 0) {

			QuitNodeMessage quitNodeMessage = new QuitNodeMessage(sender, receiver.getListenerPort(), this.predecessorInfo, valueMap);
			MessageUtil.sendMessage(quitNodeMessage);

			for(Map.Entry<Integer, List<MyFile>> entry : valueMap.entrySet()){
				for(MyFile myFile : entry.getValue()){
					myFile.getFile().delete();
				}
			}

			AppConfig.timestampedErrorPrint("turnOffNode " + message + " Goodbye Node " + receiver.getListenerPort());
		} else {

			AppConfig.timestampedErrorPrint("turnOffNode " + message);
		}


		try {
			Socket bsSocket = new Socket("localhost", AppConfig.BOOTSTRAP_PORT);

			PrintWriter bsWriter = new PrintWriter(bsSocket.getOutputStream());
			bsWriter.write("Remove\n" + AppConfig.myServentInfo.getListenerPort() + "\n");

			bsWriter.flush();
			bsSocket.close();
		} catch (UnknownHostException e) {
			AppConfig.timestampedErrorPrint(e.getMessage() + "Error " + Arrays.toString(e.getStackTrace()));
		} catch (IOException e) {
			AppConfig.timestampedErrorPrint(e.getMessage() + "Errors" + Arrays.toString(e.getStackTrace()));
		}
	}

	public void restructureSystem(){
		ServentInfo sender = AppConfig.myServentInfo;
		ServentInfo receiver = getNextNode();

		AppConfig.timestampedStandardPrint("Restructuring started");
		acceptingData(backupMap, backupPredecessor);

		RemoveUpdateMessage removeUpdateMessage = new RemoveUpdateMessage(sender , receiver, sender);
		MessageUtil.sendMessage(removeUpdateMessage);
	}

	public void notifyBootstrap() {

		//tell bootstrap this node is not a collider
		try {
			Socket bsSocket = new Socket("localhost", AppConfig.BOOTSTRAP_PORT);

			PrintWriter bsWriter = new PrintWriter(bsSocket.getOutputStream());
			bsWriter.write("New\n" + AppConfig.myServentInfo.getListenerPort() + "\n");

			bsWriter.flush();
			bsSocket.close();
		} catch (UnknownHostException e) {
			AppConfig.timestampedErrorPrint("notifyBootstrap -> Message : " + e.getMessage() + "\nErrors : " + Arrays.toString(e.getStackTrace()));
		} catch (IOException e) {
			AppConfig.timestampedErrorPrint("notifyBootstrap -> Message : " + e.getMessage() + "\nErrors : " + Arrays.toString(e.getStackTrace()));
		}

		AppConfig.timestampedStandardPrint("Bootstrap notified for node " + AppConfig.myServentInfo.getChordId());
	}

	public boolean isMyFriend(ServentInfo serventInfo){
		if(friends.contains(serventInfo)){
			return true;
		}else {
			return false;
		}
	}


	public void removeNode(List<ServentInfo> nodes) {


		this.allNodeInfo.removeAll(nodes);

		if (allNodeInfo.size() == 0){
			setPredecessor(null);
			successorTable = new ServentInfo[chordLevel];
		}else {
			updateSuccessorTable();
		}

	}

	public void addValueToBackup(Integer chordId, MyFile value){
		backupMap.get(chordId).add(value);
	}

	public void createFileInFileSystem(MyFile myFile){
		File workingDirectory = new File(System.getProperty("user.dir"));

		File directory = new File(workingDirectory, String.valueOf(AppConfig.myServentInfo.getListenerPort()));

		if (!directory.exists()) {
			if (!directory.mkdirs()) {
				AppConfig.timestampedErrorPrint("Nisam uspeo da kreiram direktorijum: " + directory.getAbsolutePath());
				throw new RuntimeException("Failed to create directory: " + directory.getAbsolutePath());
			}
		}

		File file = new File(directory, myFile.getFile().getName());

		if (!file.exists()) {
			try {
				if (file.createNewFile()) {
					System.out.println("File created: " + file.getAbsolutePath());
					writeFileContent(file, myFile);
				} else {
					AppConfig.timestampedErrorPrint("Nisam uspeo da kreiram fajl: " + file.getAbsolutePath());
					throw new RuntimeException("Failed to create file: " + file.getAbsolutePath());
				}
			} catch (IOException e) {
				AppConfig.timestampedErrorPrint("Nisam uspeo da kreiram fajl u radnom direktorijumu unutar foldera " + AppConfig.myServentInfo.getListenerPort());
				throw new RuntimeException(e);
			}
		} else {
			System.out.println("File already exists: " + file.getAbsolutePath());
		}
	}

	public void deleteFileFromFileSystem(MyFile myFile) {
		File workingDirectory = new File(System.getProperty("user.dir"));

		File directory = new File(workingDirectory, String.valueOf(AppConfig.myServentInfo.getListenerPort()));

		File file = new File(directory, myFile.getFile().getName());

		if (file.exists()) {
			if (file.delete()) {
				System.out.println("File deleted: " + file.getAbsolutePath());
			} else {
				AppConfig.timestampedErrorPrint("Nisam uspeo da obrišem fajl: " + file.getAbsolutePath());
				throw new RuntimeException("Failed to delete file: " + file.getAbsolutePath());
			}
		} else {
			System.out.println("File does not exist: " + file.getAbsolutePath());
		}
	}

	private void writeFileContent(File file, MyFile myFile){
		try(FileWriter fileWriter = new FileWriter(file)){
			fileWriter.write("File :" + myFile.getFile().getName() + " \n");
			fileWriter.write("Path: " + myFile.getPath() + "\n");
			fileWriter.write("Content written to file: " + myFile.getVisibility());
		}catch (IOException e){
			AppConfig.timestampedErrorPrint("Nisam uspeo da upisem podatke u fajl: " + file.getAbsolutePath());
			throw new RuntimeException("Failed to write content to file: " + file.getAbsolutePath(), e);
		}
	}


	public ServentInfo getBackupPredecessor() {
		return backupPredecessor;
	}

	public void setBackupPredecessor(ServentInfo backupPredecessor) {
		this.backupPredecessor = backupPredecessor;
	}

	public Map<Integer, AtomicBoolean> getPingPongMap() {
		return pingPongMap;
	}

	public AtomicBoolean getIsAlive() {
		return isAlive;
	}


}
