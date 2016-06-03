package muletrajectory;

import java.io.*;
import java.util.Scanner;

public class Graph {
    
    public static float aMat[][];
    public static int size;
    
    public static void addEdge(int v1,int v2, float weight)
    {
        Graph.aMat[v1][v2]=weight;
        Graph.aMat[v2][v1]=weight;
    }
    
    public static void removeEdge(int v1,int v2)
    {
    	Graph.aMat[v1][v2]=-1;
    	Graph.aMat[v2][v1]=-1;
    }
    //read the graph information from file and keep it in memory
    public static void readGraph()throws IOException
    {
         Scanner scan= new Scanner(new File("muletrajectory/Input/mst.txt"));
         size= scan.nextInt();
         
         aMat= new float[size+1][size+1];
         for(int i=1;i<=size;i++)
             for(int j=1;j<=size;j++)
                 aMat[i][j]= -1;
         
         for(int i=1;i<=size;i++)
         {
             for(int j=1;j<=size;j++)
             {
                 if(scan.hasNextInt())
                 {
                     int k=scan.nextInt();
                     if(k==1000 || i==j) k=-1;
                     Graph.addEdge(i, j,k);
                 }
             }
         }
    	
    }
    
    public static void removeGraph()
    {
    	for(int i=1;i<=size;i++)
        {
        	for(int j=1;j<=size;j++)
        	{
				//removing the edges between the visited Nodes only
				//WARNING: redundant nodes may be present among groups
				//since a dataMule from one group might traverse at least one node from the other.
        		if(MuleTrajectory.visitP.contains(i) && MuleTrajectory.visitP.contains(j) && Graph.aMat[i][j]!=-1)
        		{
        			//System.out.println("In graph: removing edge between "+i+" and "+j);	
        			Graph.removeEdge(i, j);
        		}
        		
        		/*if(MuleTrajectory.visitP.contains(i))
        		{
          			Graph.removeEdge(i, j);
        		}*/
        	}
        }
    }
    
}
