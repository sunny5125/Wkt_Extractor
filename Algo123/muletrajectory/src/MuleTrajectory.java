package muletrajectory;
import java.util.*;
public class MuleTrajectory {

	int dataMule = 100;// just an initial value.
	Set<Integer> visitC;
	static Set<Integer> Explored;
	static Set<Integer> visitP;
	Set<Integer> S;
	static int visited[];
	static int GC;
	ArrayList<Integer> trajectory[];
	Stack<Integer> STK;
	Queue<Integer> QUE;
	/*
	Tround[i] contains the Round Trip Time for the iTH mule
	Same for Ttravel, Tworse, Tserv
	*/

	float Tround[];
	float Ttravel[];
	float Tworse[];
	float Tserv[];
	float Tforward[];
	float Treturn[];
	/*
	Cserv contains information about the number of new IDB's visited by the iTH mule
	*/
	int Cserv[];
	/*
	Twait = 0 for now.
	*/
	float Twait;
	static int cIDB;
	/*
	muleCount is the ID number of mules deployed.
	As per this algorithm, many mules might be deployed but only those
	will be added to M( see below ) which contains at least a new IDB in
	it's trajectory
	*/
	int muleCount;
	/*
	old contains the set of those nodes which has been visited by the current trail
	and should not be visited again by the same data mule, once we return from there.
	*/
	Set<Integer> old;
	/*
	newNode contains the set of new nodes in the data mule trajectory.
	It's useful for checking whether we should add this data mule to M.
	*/
	ArrayList<Integer> newNode;
	/*
	M contains the ID of the acceptable data Mule. ID of the mules are just the numbers given by
	the variable muleCount.
	To be clear, M might contain [2,5,9] which means that only mules 2,5 and 9, given by muleCount 
	are acceptable.
	*/
	ArrayList<Integer> M;
	DisasterArea sys;

	public MuleTrajectory(float L, int size) {

		M = new ArrayList<Integer>();
		S = new HashSet();
		visitC = new HashSet();
		visitP = new HashSet();
		Explored = new HashSet();
		visited = new int[size + 1];
		Tforward = new float[size + 1];
		Treturn = new float[size + 1];
		for (int i = 1; i <= size; i++) {
			S.add(i);
			visited[i] = 0;
			Tforward[i] = Treturn[i] = 0;
		}
		trajectory = new ArrayList[dataMule];
		for (int i = 1; i < dataMule; i++)
			trajectory[i] = new ArrayList<Integer>();
		STK = new Stack();
		QUE = new ArrayDeque<Integer>();
		Tround = new float[dataMule];
		Ttravel = new float[dataMule];
		Tworse = new float[dataMule];
		Tserv = new float[dataMule];
		old = new HashSet();
		Twait = 0;
		Cserv = new int[dataMule];
		sys = new DisasterArea(L, size);
		muleCount = 0;
		newNode = new ArrayList<Integer>();
		for (int i = 1; i < dataMule; i++) {
			Tround[i] = Ttravel[i] = Tserv[i] = Tworse[i] = 0;
		}
	}

	public Group GetMuleTrajPlan(int gc) {
		// Step1
		int i = 0;
		GC = gc;
		//MuleList will contain all the properties of a single data mule
		//properties: trajectory, Tround, Tserv, Tworse
		ArrayList<DataMule> muleList= new ArrayList();
		boolean Found = true, Flag = true;
		// Step2
		while ((this.S.size() != MuleTrajectory.visitP.size() - 1)
				&& Flag == true) {
			// Flag denotes that some IDBs still remain unvisited by mules and previous iteration
			// returned a valid mule trajectory
			// step 2.1
			Flag = false;
			Found = true;
			//Setting the Group Centre as the current IDB
			MuleTrajectory.cIDB = GC;
			//mark the GC as visited
			MuleTrajectory.visited[MuleTrajectory.cIDB] = 1;
			/*
			STK means stack. It will contain all the IDB's visited so far.
			*/
			this.STK.push(MuleTrajectory.cIDB);
			i++;
			//Incrementing the muleCount ( the range is [1...100] )
			this.muleCount++;
			//Adding the Current IDB to the trajectory
			this.trajectory[this.muleCount].add(MuleTrajectory.cIDB);
			while (Found == true) {
				// generating Mi's trajectory
				//System.out.println("Explored Now in loop: "+MuleTrajectory.Explored);
                                //System.out.println("Old Now in loop: "+this.old);
				//System.out.println("\t\t\t\tTrajectory so far: "+this.trajectory[i]);				
				//Adding the Current IDB to the current Trail.
				this.visitC.add(MuleTrajectory.cIDB);
				//Search for an unvisited Neighbour
				Found = FindNearestNeighborUnvisited();
				//if an unvisited neighbour is not found, then search for a neigbour
				//which has been visited by any previous data mule
				if(Found == false) {
					Found = FindNearestNeighbourVisitedInPreviousTrail();
				}
				//if still no IDB has been found, search for a node in the current trail

				if (Found == false) {
					Found = FindNearestNeighbourVisitedInCurrentTrail();
				}

				//if no further IDB can be added, perform the activities like adding
				//the Mule to the MuleList M depending upon whether a new node has been found

				if (Found == false) {
					Flag = true;
					this.STK.clear();
					this.QUE.clear();
					this.old.clear();
					for (int ind = 1; ind <= this.sys.size; ind++) {
						Tforward[ind] = Treturn[ind] = 0;
					}
				// Add the mule to M if a new IDB has been found
					if (this.newNode.size() > 0) {
						this.M.add(this.muleCount);
						this.newNode.clear();
					}
					
					else
					{
//					System.out.println("Didn't include this traj: "+this.trajectory[this.muleCount]);
					//System.out.println("Explored : "+ MuleTrajectory.Explored);
					}
				
				//Add a special case when the only IDB in the trajectory is the group centre
					if(this.trajectory[this.muleCount].size()==1 && i==1)
					{
						this.M.add(this.muleCount);
						Flag=false;
					}

				//If a same trajectory is repeated twice by two consecutive data mules,
					//set the flag to false

					if (i > 1
							&& this.trajectory[i]
									.equals(this.trajectory[i - 1])) {
						Flag = false;
					}
				//Add all the IDB's in the current trail to the previously visited IDB's list
					MuleTrajectory.visitP.addAll(this.visitC);
				//remove the GC from visitP
					MuleTrajectory.visitP.remove(GC);
					this.visitC.clear();
				// If all the IDB's has been visited, set the Flag to false

					if (MuleTrajectory.visitP.size() == this.S.size() - 1)
						Flag = false;
				}
			}
		}

		//Complete a group by adding the mules to a muleList
		//and then adding the muleList to the group and returning it.

			this.S.removeAll(MuleTrajectory.visitP);
			this.S.remove(GC);
			MuleTrajectory.visitP.add(GC);
			for (int muleNumber=0;muleNumber<this.M.size();muleNumber++) 
			{
				DataMule mule= new DataMule(this.trajectory[this.M.get(muleNumber)], this.Tround[this.M.get(muleNumber)], this.Tworse[this.M.get(muleNumber)], this.Tserv[this.M.get(muleNumber)]);
				muleList.add(mule);
			}
		Group group= new Group(muleList);
		return group;
	}

	private boolean FindNearestNeighborUnvisited() {

		// STEP 1: find unvisited of cIDB and sort it in ascending order of
		// distance and store it in list X
		boolean Found = false;
		ArrayList<Integer> X = new ArrayList<Integer>();
		MuleTrajectory.visited[MuleTrajectory.cIDB] = 1;
		for(int i = 1; i <= this.sys.size; i++) 
		{
			float weight = Graph.aMat[MuleTrajectory.cIDB][i];
			if (weight > 0 && MuleTrajectory.visited[i] == 0) 
			{
				X.add(i);
			}
		}
		Collections.sort(X, new myCompare());
		// Step 2: For each neighbor check whether it is possible to return from that IDB
		//to the GC
		for (int j : X) {
			int x = j;
			this.Tforward[x] = this.Tforward[MuleTrajectory.cIDB]
					+ (Graph.aMat[MuleTrajectory.cIDB][x] / this.sys.v);
			this.Treturn[x] = this.Treturn[MuleTrajectory.cIDB]
					+ (Graph.aMat[MuleTrajectory.cIDB][x] / this.sys.v);
			float tempWorse = MuleTrajectory.max(this.Tworse[this.muleCount],
					Treturn[x]);
			float tempTravel = this.Tforward[x] + this.Treturn[x];
			// count number of elements in stack but not in visitP
			//this.getCserv();
			// check for feasibility of equation
			this.Tserv[this.muleCount] = Math.abs((tempTravel * this.sys.g * this.sys.p
					* this.sys.n / 60)
					/ ((this.sys.rswifi - this.sys.g * this.sys.p * this.sys.n
							/ 60)));
			float tempRound = /*this.Tserv[this.muleCount] +*/ tempTravel;
			//float totalService = 0;
			//for (Integer index : this.M) 
			//{
				//totalService = this.Tserv[index] * this.Cserv[index];
			//}
			
			//making totalService and tempWorse 0
			//to check the algorithm without these parameters
			//totalService=0;
			//tempWorse=0;

			if ((tempRound /*+ tempWorse + this.Tserv[this.muleCount]*/) < (this.sys.L - this.sys.Fdtn)) {
				this.STK.push(x);
				this.Ttravel[this.muleCount] = tempTravel;
				// this.Treturn[x]= tempServ;
				this.Tworse[this.muleCount] = tempWorse;
				this.Tround[this.muleCount] = tempRound;
				this.trajectory[this.muleCount].add(x);
				// this.Tserv[this.muleCount]+= totalService;
				this.visitC.add(x);
				this.newNode.add(x);
				MuleTrajectory.cIDB = x;
				MuleTrajectory.visited[x] = 1;
				//System.out.println("Adding "+x+" to visited");
				Found = true;
				break;
			}
		}

		return Found;

	}

	private boolean FindNearestNeighbourVisitedInCurrentTrail() 
	{
		boolean Found = false;
		//System.out.println("I was here. ");
		// Step 1: sort neighbors by ascending order of distance
		ArrayList<Integer> X = new ArrayList<Integer>();
		for (int i = 1; i <= this.sys.size; i++) 
		{
			float weight = Graph.aMat[MuleTrajectory.cIDB][i];
			if (weight > 0 && this.visitC.contains(i) && !this.old.contains(i)
					/*&& !MuleTrajectory.Explored.contains(i)*/)
				X.add(i);
		}
		Collections.sort(X, new myCompare());
		// Step 2: For each neighbor check whether it is possible to return from that IDB
		//to the GC
		for (int x : X) 
		{
			this.Tforward[x] = this.Tforward[MuleTrajectory.cIDB]
					+ (Graph.aMat[MuleTrajectory.cIDB][x] / this.sys.v);
			//Add elements to the Queue until previous instance of that elment has been found
					//in the stack
			//while (this.STK.peek() != x) {
				//this.QUE.add(this.STK.pop());
			//}
			float tempTravel = this.Tforward[x] + this.Treturn[x];
			int Temp = x;
			float tempWorse = MuleTrajectory.max(this.Tworse[this.muleCount],
					Treturn[x]);

			//Modify the return time of all the elements in the Queue (this happens in case of any circular path)
			/*while (!this.QUE.isEmpty()) {
				int y = this.QUE.poll();
				this.old.add(y);
				this.Treturn[y] = this.Treturn[Temp]
						+ (Graph.aMat[y][Temp] / this.sys.v);
				this.Tworse[this.muleCount] = MuleTrajectory.max(
						this.Tworse[this.muleCount], this.Treturn[y]);
				this.STK.push(y);
			}*/
			//this.getCserv();
			// check for feasibility of equation
			this.Tserv[this.muleCount] = Math.abs((tempTravel * this.sys.g * this.sys.p
					* this.sys.n / 60)
					/ ((this.sys.rswifi - this.sys.g * this.sys.p * this.sys.n
							/ 60)));
			float tempRound = /*this.Tserv[this.muleCount] +*/ tempTravel;
			
			float totalService = 0;
			for (Integer index : this.M) {
				totalService += this.Tserv[index] * this.Cserv[index];
			}
			//totalService=0;
			//tempWorse=0;
			if ((tempRound /*+ tempWorse + this.Tserv[this.muleCount]*/) < (this.sys.L - this.sys.Fdtn)) 
			{
			//System.out.println("Explored Now: "+MuleTrajectory.Explored);
				this.STK.push(x);
				this.Tworse[this.muleCount] = tempWorse;
				this.Ttravel[this.muleCount] = tempTravel;
				this.trajectory[this.muleCount].add(x);
				this.old.add(MuleTrajectory.cIDB);
				Found = true;
				
				//bug in old tag
				
				//System.out.println("Explored Now: "+MuleTrajectory.Explored);
				//check whether any node is a leaf node.
				
				int count=0;
				for (int i = 1; i <= this.sys.size; i++) 
	                        {
	                       
		                        float weight = Graph.aMat[MuleTrajectory.cIDB][i];
		                        //System.out.println("aMat["+MuleTrajectory.cIDB+"]["+i+"] ="+weight);
		                        if (weight > 0 && !MuleTrajectory.Explored.contains(i) )
		                        {
		                        	//if(MuleTrajectory.cIDB==13)
		                        		//System.out.println("## "+i);
			                        count++;
			                 }
	                        }
	                        
	                        if(count==1 && !MuleTrajectory.Explored.contains(cIDB))
	                        {
                      		//System.out.println("Adding to Explored...."+MuleTrajectory.cIDB);
	                        MuleTrajectory.Explored.add(MuleTrajectory.cIDB);
	                        }
				MuleTrajectory.cIDB = x;
				        break;
			}
				
		}
		return Found;
	}

	private boolean FindNearestNeighbourVisitedInPreviousTrail() {
		boolean Found = false;
		ArrayList<Integer> X = new ArrayList<Integer>();
		for (int i = 1; i <= this.sys.size; i++) {
			float weight = Graph.aMat[MuleTrajectory.cIDB][i];
			if (weight > 0 && MuleTrajectory.visitP.contains(i)
					&& !this.visitC.contains(i) /*&& !this.old.contains(i)*/
					&& !MuleTrajectory.Explored.contains(i))
				X.add(i);
		}
		
		Collections.sort(X, new myCompare());
		for (Integer j : X) {
			int x = j;
			this.Tforward[x] = this.Tforward[MuleTrajectory.cIDB]
					+ Graph.aMat[MuleTrajectory.cIDB][x];
			this.Treturn[x] = this.Treturn[MuleTrajectory.cIDB]
					+ Graph.aMat[MuleTrajectory.cIDB][x];
			float tempWorse = MuleTrajectory.max(this.Tworse[this.muleCount],
					Treturn[x]);
			float tempTravel = this.Tforward[x] + this.Treturn[x];
			this.Tserv[this.muleCount] = Math.abs((tempTravel)
					/ (this.sys.rswifi - this.sys.g * this.sys.n * this.sys.p
							/ 60));
			float tempRound = /*this.Tserv[this.muleCount] +*/ tempTravel;
			//this.getCserv();
			//float totalServiceTime = 0;
			//for (Integer index : this.M) {
			//	totalServiceTime += this.Tserv[index] * this.Cserv[index];
			//}
			
			//totalServiceTime=0;
			//tempWorse=0;
			//check for feasibility of equations 2 and 3
			if ((tempRound /*+ tempWorse + this.Tserv[this.muleCount]*/) < (this.sys.L - this.sys.Fdtn)) {
				this.trajectory[this.muleCount].add(x);
				this.Tround[this.muleCount] = tempRound;
				this.Tworse[this.muleCount] = tempWorse;
				this.Ttravel[this.muleCount] = tempTravel;
				this.STK.push(x);
				this.visitC.add(x);
				MuleTrajectory.cIDB = x;
				Found = true;
				break;
			}
		}
		return Found;
	}

	public static float max(float m, float n) {
		return (m > n ? m : n);
	}

	public static float min(float m, float n) {
		return (m > n ? m : n);
	}

	public void getCserv() {
		Stack<Integer> tempStack = new Stack<Integer>();
		tempStack.addAll(this.STK);
		Set<Integer> remDup = new HashSet<Integer>();
		remDup.addAll(tempStack);
		
		//only count the elements that are in stack but not in visitP
		ArrayList<Integer> getUniq= new ArrayList<Integer>();
		for(Integer i: remDup)
		        if(!MuleTrajectory.visitP.contains(i))
		                getUniq.add(i);
		     
		this.Cserv[muleCount] = getUniq.size();// unique elements in stack
												// excluding GC
	}

	//comparator function to sort neighbours according to distance

	class myCompare implements Comparator<Integer> {

		@Override
		public int compare(Integer o1, Integer o2) {
			if (Graph.aMat[MuleTrajectory.cIDB][o1] > Graph.aMat[MuleTrajectory.cIDB][o2])
				return 1;
			else
				return -1;
		}
	}
}
