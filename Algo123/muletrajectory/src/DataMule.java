package muletrajectory;
import java.util.*;
/*
Data mule contains information about the data mule properties such as trajectory,
Tround, Tworse, Tserv.
*/

public class DataMule {
	
	 ArrayList<Integer> trajectory;
	 float Tround;
	 float Tworse;
	 float Tserv;
	 
	DataMule(ArrayList<Integer> trajectory, float Tround, float Tworse, float Tserv)
	{
		this.trajectory= new ArrayList<Integer>();
		this.trajectory.addAll(trajectory);
		this.Tround= Tround;
		this.Tworse= Tworse;
		this.Tserv= Tserv;	
	}
}
