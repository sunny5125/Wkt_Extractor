package muletrajectory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.*;
/*
This module gives information about the groups, the data mule contained in the
groups and their properties.
*/
public class GroupFormation {

	public static Set<Integer> GroupCentre= new HashSet<Integer>();
    public static Set<Integer> timeStamps= new HashSet<Integer>();
	
	public static void getGroups(int MCS,float latency) throws IOException
	{
            Graph.readGraph();
            towerDist.getDistance();
            int size=Graph.size;
            //System.out.println("Size of gr: "+size);
            MuleTrajectory muletraj= new MuleTrajectory(latency,size);
            ArrayList<Group> groupList= new ArrayList();
            groupList.add(muletraj.GetMuleTrajPlan(MCS));
            Graph.removeGraph();
            GroupCentre.add(MCS);
            //Searching for the next GroupCentre among the remaining IDB's
            //create a copy of visitP and Explored and visited[]
            while(MuleTrajectory.visitP.size()< size)
            {
                //while all the IDBs has not been visited
                    Set<Integer> visitPcopy= new HashSet<Integer>();
                    visitPcopy.addAll(MuleTrajectory.visitP);
                    Set<Integer> exploredCopy= new HashSet<Integer>();
                    exploredCopy.addAll(MuleTrajectory.Explored);
                    int copyVisited[]= new int[size+1];
                    for(int i=1;i<=size;i++)
                            copyVisited[i]=MuleTrajectory.visited[i];

                    Set<Integer> IDBremaining= new HashSet<Integer>();
                    
                    for(int i=1;i<=size;i++)
                            IDBremaining.add(i);
                            
                    IDBremaining.removeAll(visitPcopy);
                    
                    
                    Set<Integer> IDBinRange= new HashSet<Integer>();
                    
                    //Now keep consider only those IDB's which are in range with at least one GC
                    
                    
                    for(Integer gc: GroupCentre)
                    {
						for(Integer i : IDBremaining)
						{
							//System.out.println("gc: "+gc+" i: "+i);
							if(towerDist.towDis[gc][i]<=DisasterArea.wifiRange)
								IDBinRange.add(i);
						}
					}
                    
                    
                    //System.out.println("IDBRemaining: "+IDBremaining.size()+"\nIDBinRange: "+IDBinRange.size());
                    
                    int maximumIDBvisited=-1;

                    int temp=1;
                    //Search for an IDB from the list of the remaining IDBs
                    //from which maximum IDBs can be covered and
                    //add it to the group Centre list.
                    for(Integer tempGC: IDBinRange)
                    {
                            MuleTrajectory trajTemp= new MuleTrajectory(latency, size);
                            trajTemp.GetMuleTrajPlan(tempGC);
                            if(MuleTrajectory.visitP.size() > maximumIDBvisited)
                            {
                                    temp=tempGC;
                                    maximumIDBvisited=MuleTrajectory.visitP.size();
                            }
                            MuleTrajectory.visitP.addAll(visitPcopy);
                            MuleTrajectory.Explored.addAll(exploredCopy);
                            for(int i=1;i<=size;i++)
                                    MuleTrajectory.visited[i]=copyVisited[i];
                    }
                    //add the IDB from which maximum IDBs can be covered to the Group Centre list
                    GroupCentre.add(temp);
                    MuleTrajectory trajTemp= new MuleTrajectory(latency, size);
                    groupList.add(trajTemp.GetMuleTrajPlan(temp));
                    MuleTrajectory.visitP.addAll(visitPcopy);
                    //remove all the edges which has been included in a group
                    //this has been done so as to prevent data mules from one group
                    //from entering another
                    Graph.removeGraph();
            }
            printGroupInfo(groupList,latency);
            printDataMule(groupList);
	}

    public static void printDataMule(ArrayList<Group> groupList) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        PrintWriter writer;
        //print out each data mule's information in separate files
        Graph.readGraph();
        int muleID= 01;
        int tempCount= muleID;
        for(Group group: groupList)
        {
                for(DataMule dm: group.groupMule)
                {
                	if(dm.trajectory.size()>1)
                	{
                        String filename= "muletrajectory/Mules/Mule"+tempCount;
                        writer= new PrintWriter(filename, "UTF-8");
						writer.println(tempCount++ + " [Mule ID]");
                        for(Integer tr: dm.trajectory)
                            writer.print(tr+" ");
                        writer.println("[trajectory]");

                        int prev= dm.trajectory.get(0);
                        for(int i=1;i<dm.trajectory.size();i++)
                        {
                            int current= dm.trajectory.get(i);
                            //reduce total weight of graph by 3/4-th.
                            //Situation of roads might improve. Hence, time taken
                            //to move from one idb to another will decrease.
                            writer.print((int)Graph.aMat[prev][current]*60*3/4+" ");
                            timeStamps.add((int)Graph.aMat[prev][current]*60*3/4);
                            prev=current;
                        }
                        writer.println(" [Time taken to traverse between clusters] ");
                        writer.println((int)(dm.Tserv*60)+" [Service Time]");
                        writer.println();
                        writer.close();
                    }
                }
        }
    }

    public static void printGroupInfo(ArrayList<Group> groupList,float latency) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer= new PrintWriter("muletrajectory/Output/output"+latency, "UTF-8");
        int count=1, tempCount=1;
        int numOfDataMule=0;
        

        for(Group group: groupList)
        {
                writer.println();
                writer.println("\nGroup: "+count++);
                for(DataMule dm: group.groupMule)
                {
                	if(dm.trajectory.size()>1)
                	{
                        writer.println();
                        writer.println(tempCount++ + " [Mule ID]");
                        for(Integer tr: dm.trajectory)
                            writer.print(tr+",");
                        writer.println("[trajectory]");
                        writer.println(dm.Tround+ " [Tround]");
                        writer.println(dm.Tworse+" [Tworse]");
                        writer.println(dm.Tserv+" [Tserv]");
                        writer.println();
                        numOfDataMule++;
                     }
                    else
                    {
                    	writer.println("No data Mule. ");
                    	writer.println("LWC at "+dm.trajectory.get(0));
                    	writer.println();
                    }
                }
                writer.println();
        }
        System.out.println("Latency: "+latency+"\t"+"Number Of dataMules: "+numOfDataMule+"\t"+"Groups: "+groupList.size());
        writer.close();
    }
}
