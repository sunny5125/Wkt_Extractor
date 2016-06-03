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
            //Graph.showGraph();
            towerDist.getDistance();
            int size=Graph.size;
            //System.out.println("Size of gr: "+size);
            MuleTrajectory muletraj= new MuleTrajectory(latency,size);
            ArrayList<Group> groupList= new ArrayList();
            groupList.add(muletraj.GetMuleTrajPlan(MCS));
            Graph.removeGraph();
            GroupCentre.add(MCS);
            //Searching for the next GroupCentre among the remaining IDB's which are in range of the LWCs
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
							if(towerDist.towDis[gc][i]<=DisasterArea.wifiRange)
                            {
								IDBinRange.add(i);
                            }
						}
					}

                    //A HACK
                    //sometimes, all IDB except one or two could be could
                    //form a single group (with MCS as the group center)
                    //now, a wifi tower placed at MCS might not cover the remaining nodes
                    //in that case this loop would continue infinitely to search for an IDB 
                    if(IDBinRange.size() == 0)
                    {
                       // DisasterArea.wifiRange+=1000;
                        for(Integer i : IDBremaining)
                        {
                            ArrayList<Integer> traj= new ArrayList();
                            traj.add(i);
                            DataMule dm = new DataMule(traj,0,0,0);
                            ArrayList<DataMule> muleList= new ArrayList();
                            muleList.add(dm);
                            Group grp= new Group(muleList);
                            groupList.add(grp);
                            //System.out.println("Adding "+i+"to group");
                        }
                        break;
                    }

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
          //  System.out.println("Time: "+GroupFormation.timeStamps);
	}



    public static void printDataMule(ArrayList<Group> groupList) throws FileNotFoundException, UnsupportedEncodingException, IOException {

        PrintWriter wr= new PrintWriter("muletrajectory/Output/id_info","UTF-8");
        PrintWriter writer;
        //print out each data mule's information in separate files
        Graph.readGraph();
        int muleID= Graph.size*2+GroupCentre.size();
        wr.println(muleID);
       // System.out.println("GC: "+GroupCentre);
        for(Integer i : GroupCentre)
            wr.print((Graph.size+i)+ " ");
        wr.close();
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
                            writer.print((Graph.size+tr)+" ");
                        writer.println("[trajectory]");

                        int prev= dm.trajectory.get(0);
                        for(int i=1;i<dm.trajectory.size();i++)
                        {
                            int current= dm.trajectory.get(i);
                            //change total weight of graph by multiplier.
                            //Situation of roads might improve/worsen. Hence, time taken
                            //to move from one idb to another will decrease.
                            int multiplier=1;
                            writer.print((int)Graph.aMat[prev][current]*60*multiplier+" ");
                            timeStamps.add((int)Graph.aMat[prev][current]*60*multiplier);
                            //System.out.println("Now timeStamp: "+timeStamps);
                            prev=current;
                        }
                        writer.println(" [Time taken to traverse between clusters] ");
                        int trSize= dm.trajectory.size();

                        for(int i=1;i<trSize;i++)
                        	writer.print((int)(dm.Tserv*60)+" ");
                        writer.print("[Tserv]");
                        writer.println();
                        writer.close();
                    }
                }
        }
    }

    public static void printGroupInfo(ArrayList<Group> groupList,float latency) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer= new PrintWriter("muletrajectory/Output/output"+latency, "UTF-8");
        PrintWriter wifi_idb= new PrintWriter("muletrajectory/Output/wifi_idb_mapping"+latency, "UTF-8");
        
        int count=1, tempCount=1;
        int numOfDataMule=0;

        // System.out.println("Group SIZE: "+groupList.size());

        for(Group group: groupList)
        {
                writer.println();
                writer.println("\nGroup: "+count++);
                Set<Integer> idb= new HashSet();
                int currentGC=-1;
                for(DataMule dm: group.groupMule)
                {
                	if(dm.trajectory.size()>1)
                	{
                        currentGC=dm.trajectory.get(0);
                        writer.println();
                        writer.println(tempCount++ + " [Mule ID]");
                        for(Integer tr: dm.trajectory)
                        {
                            idb.add(Graph.size+tr);
                            writer.print(tr+",");
                        }
                        writer.println("[trajectory]");
                        for(Integer tr: dm.trajectory)
                            writer.print((tr+Graph.size)+",");
                        writer.println(" [trajectory id according to ONE default setting ]");
                        writer.println(dm.Tround+ " [Tround]");
                        writer.println(dm.Tworse+" [Tworse]");
                        writer.println(dm.Tserv+" [Tserv]");
                        writer.println();
                        numOfDataMule++;
                     }
                    else
                    {
                        currentGC=dm.trajectory.get(0);
                    	writer.println("No data Mule. ");
                    	writer.println("LWC at "+dm.trajectory.get(0));
                    	writer.println();
                    }
                }
                
                wifi_idb.println((Graph.size+currentGC)+" : "+idb);
                writer.println();
        }
        System.out.println("Latency: "+latency+"\t"+"Number Of dataMules: "+numOfDataMule+"\t"+"Groups: "+groupList.size());
        writer.close();
        wifi_idb.close();
    }
}
