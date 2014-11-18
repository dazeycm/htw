package edu.miamioh.cse283.htw;

import java.net.*;
import java.util.*;

/**
 * The CaveServer class takes the following command-line parameters:
 * 
 * <Hostname of CaveSystemServer> <port number of CaveSystemServer> <port number
 * of this CaveServer>
 * 
 * E.g., "localhost 1234 2000"
 */
public class CaveServer {

	/** Port base for this cave server. */
	protected int portBase;

	/** Socket for accepting connections from players. */
	protected ServerSocket clientSocket;

	/** Proxy to the CaveSystemServer. */
	protected CaveSystemServerProxy caveSystem;

	/** Rooms in this CaveServer. */
	protected ArrayList<Room> rooms;

	/** Constructor. */
	public CaveServer(CaveSystemServerProxy caveSystem, int portBase) {
		this.caveSystem = caveSystem;
		this.portBase = portBase;

		rooms = new ArrayList<Room>();
		rooms.add(new Room(0));
		rooms.add(new Room(1));
		rooms.add(new Room(2));
		rooms.get(0).neighbors.add(rooms.get(1));

		rooms.get(1).neighbors.add(rooms.get(0));
		rooms.get(1).neighbors.add(rooms.get(2));

		rooms.get(2).neighbors.add(rooms.get(1));

		for (int i = 3; i < 20; ++i) {
			Room room = new Room(i);
			// room.roomNum = i;
			Random r = new Random();
			rooms.add(room);
			if (i == 3) {
				rooms.get(2).neighbors.add(rooms.get(3));
				for (int j = 0; j < 2; j++) {
					room.neighbors.add(rooms.get(r.nextInt(rooms.size()))); // add random neighbors
				}
			} else {
				for (int j = 0; j < 3; j++) {
					room.neighbors.add(rooms.get(r.nextInt(rooms.size()))); // add  random neighbors
				}
			}
		}
	}

	/** Returns the port number to use for accepting client connections. */
	public int getClientPort() {
		return portBase;
	}

	/** This is the thread that handles a single client connection. */
	public class ClientThread implements Runnable {
		/**
		 * This is our "client" (actually, a proxy to the network-connected
		 * client).
		 */
		protected ClientProxy client;

		/** Constructor. */
		public ClientThread(ClientProxy client) {
			this.client = client;
		}

		/**
		 * Play the game with this client.
		 */
		public void run() {
			try {
				client.message("Abandon all hope ye who enter here!");
				// put the player in a room (any room is fine)
				rooms.get(0).players.add(client);
				int curRoom = 0;
				int newRoom = 0;
				while (true) {

					client.senses("You are in an empty room.");
					client.message("The connecting rooms are "
							+ rooms.get(curRoom).printNeighbors());
					String action = client.getAction();

					if (action.contains("move")) {
						action = action.replaceAll("[^-?0-9]", "");
						newRoom = Integer.parseInt(action);
						rooms.get(curRoom).players.remove(client);
						rooms.get(newRoom).players.add(client);
						curRoom = newRoom;
					}
					
					// -- and retrieve their action:
					// -- and perform the action
					// client.message(action);
				}

			} catch (Exception ex) {
				// If an exception is thrown, we can't fix it here -- Crash.
				ex.printStackTrace();
				System.exit(1);
			}
		}
	}

	/** Runs the CaveSystemServer. */
	public void run() {
		try {
			clientSocket = new ServerSocket(getClientPort());
			caveSystem.register(clientSocket);

			while (true) {
				// and now loop forever, accepting client connections:
				while (true) {
					ClientProxy client = new ClientProxy(clientSocket.accept());
					(new Thread(new ClientThread(client))).start();
				}
			}
		} catch (Exception ex) {
			// If an exception is thrown, we can't fix it here -- Crash.
			ex.printStackTrace();
			System.exit(1);
		}
	}

	/** Main method (run the CaveServer). */
	public static void main(String[] args) throws Exception {
		InetAddress addr = InetAddress.getByName("localhost");
		int cssPortBase = 1234;
		int cavePortBase = 2000;

		if (args.length > 0) {
			addr = InetAddress.getByName(args[0]);
			cssPortBase = Integer.parseInt(args[1]);
			cavePortBase = Integer.parseInt(args[2]);
		}

		// first, we need our proxy object to the CaveSystemServer:
		CaveSystemServerProxy caveSystem = new CaveSystemServerProxy(
				new Socket(addr, cssPortBase + 1));

		// now construct this cave server, and run it:
		CaveServer cs = new CaveServer(caveSystem, cavePortBase);
		cs.run();
	}
}
