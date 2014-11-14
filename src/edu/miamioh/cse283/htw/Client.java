package edu.miamioh.cse283.htw;

import java.net.*;

/** Client (player) for the Hunt the Wumpus game.
 *	Command Line:
 *	CaveSystemServer: 1234
 *	CaveServer: localhost 1234 2000
 *	Client: loclahost 1234
 */
public class Client {
	
	/** Proxy object that connects the client to its current cave. */
	protected CaveProxy cave;

	/** Constructor. */
	public Client(CaveProxy cave) {
		this.cave = cave;
	}
	
	/** Returns true if the player is still alive. */
	public synchronized boolean isAlive() {
		return true;
	}

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
			
			// now start the sense and respond loop:
			while(isAlive()) {
				System.out.println(cave.getSenses());
				
				// get an action from the player, and
				// send it to the cave server.
				cave.sendAction("move 1");
				
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
		CaveProxy cave = new CaveProxy(new Socket(InetAddress.getByName(args[0]), Integer.parseInt(args[1])));
		Client c = new Client(cave);
		c.run();
	}
}
