package muletrajectory;

import java.util.ArrayList;

public class Group {

	public ArrayList<DataMule> groupMule;

	public Group(ArrayList<DataMule> muleList)
	{
		groupMule= new ArrayList<DataMule>();
		groupMule.addAll(muleList);
	}
	
	
}
