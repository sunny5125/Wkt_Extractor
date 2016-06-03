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

public class PostOfficeClusterMovement extends RandomWaypoint {
	/** Range of the cluster */
	public static final String	CLUSTER_RANGE = "clusterRange";
	/** Center point of the cluster */
	public static final String	CLUSTER_CENTER = "clusterCenter";
	
	public static final String	CLUSTER_DB_LOC = "dblocation";
	
	private static final String CLUSTER_STOPS = "clusterStops";
	
	private int flag = 0;
	private int nrOfStops = 1;
	private int		p_x_center = 100, p_y_center = 100;
	private int db_x, db_y;
	private double	p_range = 100.0;
	
	public PostOfficeClusterMovement(Settings s) {
		super(s);
		
		if (s.contains(CLUSTER_RANGE)){
			this.p_range = s.getDouble(CLUSTER_RANGE);
		}
		if (s.contains(CLUSTER_STOPS)){
			int[] stops = s.getCsvInts(CLUSTER_STOPS,2);
			this.nrOfStops = (int)(((stops[1] - stops[0]) * rng.nextDouble()) + stops[0]);
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
	}
	
	private PostOfficeClusterMovement(PostOfficeClusterMovement cmv) {
		super(cmv);
		this.p_range = cmv.p_range;
		this.p_x_center = cmv.p_x_center;
		this.p_y_center = cmv.p_y_center;
		this.db_x = cmv.db_x;
		this.db_y = cmv.db_y;
		this.flag = cmv.flag;
		this.nrOfStops = cmv.nrOfStops;
	}
	
	@Override
	protected Coord randomCoord() {
		double x = (rng.nextDouble()*2 - 1)*this.p_range;
		double y = (rng.nextDouble()*2 - 1)*this.p_range;
		while (x*x + y*y>this.p_range*this.p_range) {
			x = (rng.nextDouble()*2 - 1)*this.p_range;
			y = (rng.nextDouble()*2 - 1)*this.p_range;
		}
		//System.out.println("cemter("+p_x_center+","+p_y_center+")");
        
		if(this.flag==this.nrOfStops)
		{
			this.flag = 0;
			x=this.db_x;
			y=this.db_y;
		}
		else
		{
			x += this.p_x_center;
			y += this.p_y_center;
			this.flag++;
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
	
	@Override
	public PostOfficeClusterMovement replicate() {
		return new PostOfficeClusterMovement(this);
	}
}
