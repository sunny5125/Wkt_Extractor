package muletrajectory;
import java.util.*;

public class ExternalMovement {
    
    public static int getGCD()
    {
        ArrayList<Integer> ts= new ArrayList<Integer>();
        ts.addAll(GroupFormation.timeStamps);
        int m= ts.get(0),n= ts.get(1);
        for(int i=2;i< ts.size(); i++)
        {
            int t=0;
            while(n!=0)
            {
                t=n;
                n=m%n;
                m=t;
            }
            n=m;
            m= ts.get(i);
        }
        return n;
    }
    
    public static void getInterval()
    {
        System.out.println("Hop Interval: "+getGCD());
        //System.out.println("timeStamps: "+GroupFormation.timeStamps);
    }
}
