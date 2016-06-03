/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */

/** 
 * Random waypoint movement where the coordinates are restricted to circular
 * area defined by a central point and range.
 * @author teemuk
 */
package movement;

import core.Coord;
import core.Settings;
import java.util.Random;

public class PossionPostOfficeClusterMovement extends RandomWaypoint {
    /** Range of the cluster */
    public static final String CLUSTER_RANGE = "clusterRange";
    /** Center point of the cluster */
    public static final String CLUSTER_CENTER = "clusterCenter";
    
    public static final String CLUSTER_DB_LOC = "dblocation";
    //Speed of movement
    public static final String SPEED = "speed";
    // Rate parameter for Possion Movementmodel
    public static final String CLUSTER_RATE = "clusterRate";
    
    private boolean flag = false;
    private int     p_x_center = 100, p_y_center = 100;
    private int db_x, db_y;
    private double last_x,last_y;
    private double  p_range = 100.0;
    private int clusterRate=0;
    private double nextInterval=0;
    private double s1,s2;
    private int RAND_MAX=Integer.MAX_VALUE;
    
    public PossionPostOfficeClusterMovement(Settings s) {
        super(s);
        
        if (s.contains(CLUSTER_RANGE)){
            this.p_range = s.getDouble(CLUSTER_RANGE)/32;
        }
        if (s.contains(CLUSTER_CENTER)){
            int[] center = s.getCsvInts(CLUSTER_CENTER,2);
            this.p_x_center = center[0];
            this.p_y_center = center[1];
            
        }
        if (s.contains(CLUSTER_DB_LOC)){
            int[] dbloc = s.getCsvInts(CLUSTER_DB_LOC,2);
            this.db_x = dbloc[0];
            this.db_y = dbloc[1];
        }
        if(s.contains(SPEED))
        {
            double[] speed =s.getCsvDoubles(SPEED,2);
            this.s1=speed[0];
            this.s2=speed[1];
        }
        if (s.contains(CLUSTER_RATE)){
            this.clusterRate = s.getInt(CLUSTER_RATE);
            //System.out.println("Cluster Rate:"+this.clusterRate);
        }
        last_x=db_x;
        last_y=db_y;
        nextInterval=possionNextInterval(clusterRate);
    }
    
    private PossionPostOfficeClusterMovement(PossionPostOfficeClusterMovement cmv) {
        super(cmv);
        this.p_range = cmv.p_range;
        this.p_x_center = cmv.p_x_center;
        this.p_y_center = cmv.p_y_center;
        this.db_x = cmv.db_x;
        this.db_y = cmv.db_y;
        this.flag = cmv.flag;
        this.clusterRate=cmv.clusterRate;
        this.s1=cmv.s1;
        this.s2=cmv.s2;
        this.last_x=cmv.last_x;
        this.last_y=cmv.last_y;
        this.nextInterval=cmv.nextInterval;
    }
    
    @Override
    protected Coord randomCoord() {
        double x = (rng.nextDouble()*2 - 1)*this.p_range;
        double y = (rng.nextDouble()*2 - 1)*this.p_range;
        while (distance(x,y)>this.p_range &&!check(x,y)) 
		{
            x = (rng.nextDouble()*2 - 1)*this.p_range;
            y = (rng.nextDouble()*2 - 1)*this.p_range;
        }
                
        if(this.flag)
        {
            this.nextInterval=possionNextInterval(clusterRate);
            this.flag = false;
            x=this.db_x;
            y=this.db_y;
	    this.last_x=this.db_x;
	    this.last_y=this.db_y;
	    System.out.println("Hello");
        }
        else
        {
            
            x += this.p_x_center;
            y += this.p_y_center;
            this.last_x=x;
            this.last_y=y;
	    this.flag=timeLeft(this.db_x,this.db_y,x,y);
	    System.out.println("Hi");
        }
        return new Coord(x,y);
    }
    
    @Override
    public int getMaxX() {
        return (int)Math.ceil(this.p_x_center + this.p_range);
    }

    @Override
    public int getMaxY() {
        return (int)Math.ceil(this.p_y_center + this.p_range);
    }
    private double possionNextInterval(int clusterRate)
    {
        Random r=new Random();
        double nextTime=0;
        nextTime = -Math.log(r.nextInt(RAND_MAX)/(RAND_MAX+1.0))*clusterRate;
        //System.out.println(nextTime);
        return nextTime;
    }
	private boolean check(double x,double y)
	{
		if((check_time(this.last_x,this.last_y,x,y)+check_time(this.db_x,this.db_y,x,y))>this.nextInterval)
		{
			//System.out.println("false");
			return false;
		}
		else
		{
			//System.out.println("true");
			return true;
		}
	}
    private double check_time(double last_x,double last_y,double x,double y)
    {
    	double ax,ay;
        double distance;
        double time;
        ax=x-last_x;
        ay=y-last_y;
        ax=ax*ax;
        ay=ay*ay;
        distance=Math.sqrt((double)(ax+ay));
        time=distance/this.s1;
        return time;
    }
    private boolean timeLeft(double db_x,double db_y, double x, double y )
    {
        
        if(check(x,y))
        {
            return true;
        }
        else
        {
            this.nextInterval=this.nextInterval-check_time(this.last_x,this.last_y,x,y);
            return false;
        }
    }
    double distance(double x,double y)
    {
        return Math.sqrt(((x-this.p_x_center)*(x-this.p_x_center))+((y-this.p_y_center)*(y-this.p_y_center)));
    }
    
    @Override
    public PossionPostOfficeClusterMovement replicate() {
        return new PossionPostOfficeClusterMovement(this);
    }
        
}
