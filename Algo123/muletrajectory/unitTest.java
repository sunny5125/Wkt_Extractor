package muletrajectory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class unitTest
{

    public static void main(String args[]) throws IOException
    {

       Scanner scanner = new Scanner(System.in);

       //Uncomment the following lines to get the output of huge data set.

       /*

       System.out.println("Enter the number of intervals: ");
       int interval= scanner.nextInt();
       for(int i=1;i<=interval;i++)
        	GroupFormation.getGroups(8,i*60);
        */

        System.out.println("Enter Latency: ");
        int latncy= scanner.nextInt();

        GroupFormation.getGroups(1,latncy);

        ExternalMovement.getInterval();
    }

}
