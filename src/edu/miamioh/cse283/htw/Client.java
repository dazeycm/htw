package edu.miamioh.cse283.htw;

import java.net.*;
import java.util.Scanner;

/** Client (player) for the Hunt the Wumpus game.
 * 
 * The Client class takes the following command-line parameters:
 * 
 * <Hostname of CaveSystemServer> <port number of CaveSystemServer>
 * 
 * E.g., "localhost 1234" 
 *
 */
public class Client {
	protected Scanner kb;
	protected boolean isAlive = true;
	
	/** Proxy object that connects the client to its current cave. */
	protected CaveProxy cave;

	/** Constructor. */
	public Client(CaveProxy cave) {
		this.cave = cave;
	}
	
	/** Returns true if the player is still alive. */
	//public synchronized boolean isAlive(boolean bool) {
	//	return bool;
	//}

	/** Plays the game.
	 * 
	 * @param args holds address and port number for the 
	 * CaveSystemServer this client will connect to.
	 */
	public void run() {
		try {
			// all clients initially experience a handoff:
			cave = cave.handoff();
			System.out.println(cave.getMessage());
			
			kb = new Scanner(System.in);
			
			// now start the sense and respond loop:
			while(this.isAlive) {
				String str;
				System.out.println(cave.getMessage());
				
				str = cave.getMessage();
				if (str.length() > 0)	{
					System.out.println(str);
					if(str.contains("fallen") || str.contains("depths"))	{
						//this.isAlive = false;
					}
				}
				
				str = cave.getSenses();
				if (str.length() > 0)	{
					System.out.println(str);
				}
				
				System.out.println(cave.getMessage());
				
				String action = "";
				action = kb.nextLine();
				cave.sendAction(action);
			}
			
		} catch(Exception ex) {
			// If an exception is thrown, we can't fix it here -- Crash.
			ex.printStackTrace();
			System.exit(1);
		}
	}
	
	/** Main method for clients.
	 * 
	 * @param args contains the hostname and port number of the server that 
	 * this client should connect to.
	 */
	public static void main(String[] args) throws Exception {
		InetAddress addr=InetAddress.getByName("localhost");
		int cavePortBase=1234;
		
		if(args.length > 0) {
			addr = InetAddress.getByName(args[0]);
			cavePortBase = Integer.parseInt(args[1]);
		}

		CaveProxy cave = new CaveProxy(new Socket(addr, cavePortBase));
		Client c = new Client(cave);
		c.run();
	}
}
