package edu.miamioh.cse283.htw;

import java.util.*;

public class Room {
	public ArrayList<ClientProxy> players;
	public ArrayList<Room> neighbors;
	public int roomNum;
	boolean hasBats;
	boolean hasWumpus;
	boolean hasPit;
	
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
			if(getNumNeighbors() == 1)
			ret += this.neighbors.get(i).roomNum;
			
			if(getNumNeighbors() == 2)	{
				if (i == 0)	{
					ret += this.neighbors.get(i).roomNum + " and ";
				}
				if (i == 1)	{
					ret += this.neighbors.get(i).roomNum;
				}
			}
			
			if(getNumNeighbors() == 3)	{
				if (i == 0 || i == 1)	{
					ret += this.neighbors.get(i).roomNum + ", ";
				}
				if (i == 2)
					ret += "and " + this.neighbors.get(i).roomNum; 
			}
			if(getNumNeighbors() > 3)	{
				ret+= "errrrrror";
			}
		}
		return ret;
	}
	
}
