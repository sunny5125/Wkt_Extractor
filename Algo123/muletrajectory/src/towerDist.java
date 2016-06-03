package muletrajectory;
import java.util.*;
import java.io.*;

public class towerDist
{

static int towDis[][];

public static void getDistance()throws FileNotFoundException
{
	Scanner scan= new Scanner(new File("muletrajectory/Input/distance.txt"));
	int towerNum;
	towerNum= scan.nextInt();
	towDis= new int[towerNum+1][towerNum+1];
	
	
	ArrayList<ArrayList<Float>> pairs= new ArrayList();
	int j=0;
	while(j++ < towerNum)
	{
		float x= scan.nextFloat();
		float y= scan.nextFloat();
		ArrayList<Float> temp= new ArrayList();
		temp.add(x);
		temp.add(y);
		pairs.add(temp);
	}
	
	float x1,x2, y1, y2;	
	//build up the distance array
	for(int m=0;m<towerNum;m++)
	{
		x1= pairs.get(m).get(0);
		y1= pairs.get(m).get(1);
		
		for(int n=0;n<towerNum;n++)
		{
			x2= pairs.get(n).get(0);
			y2= pairs.get(n).get(1);

			towDis[m+1][n+1]= (int)Math.sqrt(Math.pow((x2-x1),2)+ Math.pow((y2-y1),2));
		}
	}
	
	//now test the array
	/*for(int s=1;s<=towerNum;s++)
	{
		for(int d=1;d<=towerNum;d++)
		{
			System.out.print(towDis[s][d]+" ");
		}
		System.out.println();
	}*/

}

}
