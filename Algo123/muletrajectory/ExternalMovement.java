package muletrajectory;
import java.util.*;

public class ExternalMovement {

    public static int getGCD()
    {
        ArrayList<Integer> ts= new ArrayList<Integer>();
 //       System.out.println("TimeStamps: "+GroupFormation.timeStamps);
        ts.addAll(GroupFormation.timeStamps);
        System.out.println("TimeStamps: "+ts);

        if(ts.size()==0)
            return 0;

        if(ts.size() == 1)
            return ts.get(0);

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
    }
}
