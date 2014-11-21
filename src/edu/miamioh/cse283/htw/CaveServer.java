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
		Random r = new Random();

		for (int i = 0; i < 20; ++i) {
			Room room = new Room(i);
			rooms.add(room);
		}
		
		for(int i = 0; i < 19; i++)	{
			rooms.get(i).neighbors.add(rooms.get(i + 1));
			rooms.get(i + 1).neighbors.add(rooms.get(i));
		}
		
		for(int i = 3; i < 20; i++)	{
			int rand = 19 - r.nextInt(18);
			while(rooms.get(i).getNumNeighbors() < 3)	{
				if(rooms.get(rand).getNumNeighbors() < 3)	{	
					rooms.get(i).neighbors.add(rooms.get(rand));
					rooms.get(rand).neighbors.add(rooms.get(i));
				}
				else
					rand = 19 - r.nextInt(18);
			}
		}
		
		for(int i = 4; i < 20; i++)	{
			for (int j = 0; j < 3; j++){
				int chance = r.nextInt(20);
				if (j == 0){
					if (chance < 5 && !rooms.get(i).hasThreat())
						rooms.get(i).hasBats = true;
				}
				if (j == 1)	{
					if (chance < 5 && !rooms.get(i).hasThreat())
						rooms.get(i).hasPit = true;
				}
				if (j == 2)	{
					if (chance < 5 && !rooms.get(i).hasThreat())
						rooms.get(i).hasWumpus = true;
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
					client.message("You are now in room " + curRoom);
					StringBuilder sb = new StringBuilder();
					
					if(rooms.get(curRoom).hasBats)	{
						sb.append("You've been carried away by the bats! ");
						Random r = new Random();
						newRoom = r.nextInt(20);
						rooms.get(curRoom).players.remove(client);	
						rooms.get(newRoom).players.add(client);
						sb.append("\nYou are now in room " + newRoom + ". ");
						curRoom = newRoom;
					}
					
					if(rooms.get(curRoom).hasPit)	
						sb.append("Help! You've fallen in a pit and can't get out! ");
					
					if(rooms.get(curRoom).hasWumpus)	{
						sb.append("Kyle slithers out from the depths and eats you whole. ");
					}
					
					client.message(sb.toString());
					sb.setLength(0);
					
					for(Room r : rooms.get(curRoom).neighbors)	{
						if (r.hasBats)	{
							if(!sb.toString().contains("bats"))
								sb.append("You sense bats in a nearby room. ");
						}
						if (r.hasPit)	{
							if(!sb.toString().contains("draft"))
								sb.append("You feel a draft in a nearby room. ");
						}
						if (r.hasWumpus)	{
							if(!sb.toString().contains("Kyle"))
								sb.append("Something that smells like Kyle is in a nearby room. ");
						}
					}
					//if(sb.length() == 0)
						//sb.append("The surrounding rooms look safe to me, man. You can trust me, right?");
					client.senses(sb.toString());
					sb.setLength(0); //empty the stringbuilder
					
					
					
					if (rooms.get(curRoom).getNumNeighbors() == 1)
						client.message("The connecting room is " + rooms.get(curRoom).printNeighbors());
					else
						client.message("The connecting rooms are " + rooms.get(curRoom).printNeighbors());
					
					String action = client.getAction();

					if (action.contains("move")) {
						action = action.replaceAll("[^-?0-9]", "");
						newRoom = Integer.parseInt(action);	//need to make sure the room they want to go to is attached to the current room
						if(rooms.get(curRoom).neighbors.contains(rooms.get(newRoom)))	{
							rooms.get(curRoom).players.remove(client);	
							rooms.get(newRoom).players.add(client);
							curRoom = newRoom;
						}
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
