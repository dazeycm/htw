package edu.miamioh.cse283.htw;

import java.util.*;

public class Room {
	public ArrayList<ClientProxy> players;
	public ArrayList<Room> neighbors;
	public int roomNum;
	
	public Room(int roomNum) {
		players = new ArrayList<ClientProxy>();
		neighbors = new ArrayList<Room>();
		this.roomNum = roomNum;
	}
	
	public int getNumNeighbors()	{
		return neighbors.size();
	}
	
	public String printNeighbors()	{
		String ret = "";
		for(int i = 0; i < getNumNeighbors(); i++){
			ret += this.neighbors.get(i).roomNum + ", ";
		}
		return ret;
	}
	
}
