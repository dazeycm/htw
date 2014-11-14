package edu.miamioh.cse283.htw;

import java.util.*;

public class Room {
	public ArrayList<ClientProxy> players;
	public ArrayList<Room> neighbors;
	
	public Room() {
		players = new ArrayList<ClientProxy>();
	}
	
	public int getNumNeighbors()	{
		return neighbors.size();
	}
	
}
