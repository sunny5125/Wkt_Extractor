/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Somir Saikia
 */
package routing;

import java.io.*;
import java.util.*;

public class Graph<T> 
{
    public ArrayList<T> vertex;//Stores vertex
    
    public ArrayList<ArrayList<T>> edge;//Stores edge list of a particular vertex
    
    public ArrayList<ArrayList<T>> n;//next vertex List(Used to find Data Mule)

    public HashMap<T, ArrayList<ArrayList<T>>> lookUpTable = new HashMap<T, ArrayList<ArrayList<T>>>();
    
    //Initialisation of vertex and edge
    public Graph()
    {
        vertex = new ArrayList<T>();
        edge = new ArrayList<ArrayList<T>>();
    }
    //Insertion of Vertex
    public void insertVertex(T n)
    {
        vertex.add(n);
        edge.add(new ArrayList<T>());
    }
    //Insertion of Edge between two vertex x and y
    void insertEdge(T x,T y)
    {
        //if both the vertex are in the vertex List
        if(vertex.contains(x) && vertex.contains(y))
        {
            //Check if edge List contain the other vertex
            if(!edge.get(vertex.indexOf(x)).contains(y))
                edge.get(vertex.indexOf(x)).add(y);
            
            if(!edge.get(vertex.indexOf(y)).contains(x))
                edge.get(vertex.indexOf(y)).add(x);               
        }
        //The next two if statements are for the vertex: if one of them contain in the vertex List
        if(vertex.contains(x) && !vertex.contains(y))
        {
            vertex.add(y);
            edge.get(vertex.indexOf(x)).add(y);
            ArrayList<T> tmp=new ArrayList<T>();
            tmp.add(x);
            edge.add(tmp);//add edge list of y vertex
        }
        if(!vertex.contains(x) && vertex.contains(y))
        {
            vertex.add(x);
            edge.get(vertex.indexOf(y)).add(x);
            ArrayList<T> tmp=new ArrayList<T>();
            tmp.add(y);
            edge.add(tmp);//add edge list of x vertex
        }
        //If both vertex are not in the vertex List
        if(!vertex.contains(x) && !vertex.contains(y))
        {
            vertex.add(x);
            vertex.add(y);
            ArrayList<T> tmp=new ArrayList<T>();
            tmp.add(y);
            edge.add(tmp);
            tmp=new ArrayList<T>();
            tmp.add(x);            
            edge.add(tmp);
        }
    }
    // Function returns a minimum Spanning tree from a graph
    public Graph<T> getTree()
    {
        
         
        Graph<T> tree=new Graph<T>();
        if(vertex.size()==0)
            return tree;

        tree.insertVertex(vertex.get(0));

        for(int i=0;i<edge.get(0).size();i++)
            tree.insertEdge(vertex.get(0), edge.get(0).get(i));
	//MST using Prim's
        while(tree.vertex.size()<vertex.size())
        {
            for(int i=0;i<tree.vertex.size();i++)
            {
                for(int k=0;k<edge.get(vertex.indexOf(tree.vertex.get(i))).size();k++)
                {
                        if(!tree.vertex.contains(edge.get(vertex.indexOf(tree.vertex.get(i))).get(k)))
                        {
                              
                           tree.insertEdge(edge.get(vertex.indexOf(tree.vertex.get(i))).get(k),tree.vertex.get(i));
                                break;
                        }
                }

            }
        }
        /*
        
        while(tree.vertex.size()<vertex.size())
        {
            for(int i=0;i<tree.vertex.size();i++)
            {
                for(int j=0;j<edge.get(i).size();j++)
                {
                    if(!tree.vertex.contains(edge.get(i).get(j)))
                    {
                        tree.insertEdge(tree.vertex.get(i), edge.get(i).get(j));
                        break;
                    }
                }
            }
        }*/ 
    //    tree.nextV();
        return tree;
    }
    // Display the graph
  
 public void look_up_table(){
 //----generrating unique MST according to Host()-----------//  
	  
	for(int x=0;x<vertex.size();x++){		// x: Source Vertex...
		ArrayList<ArrayList<T>> edge1= new ArrayList<ArrayList<T>>();
		ArrayList<ArrayList<T>> con = new ArrayList<ArrayList<T>>();	// con: Temporary Look Up Table for node x...	
		ArrayList<T> tmpv=new ArrayList<T>();
		tmpv.add(this.vertex.get(x));
		int flag=0;;
		for(int y=0;y<vertex.size();y++){	// y: Destination Vertex...
			ArrayList<T> temp = new ArrayList<T>();
			int f=0;
			if(x!=y){
			    if(flag==0)
			      {
				int i=0;
				while(tmpv.size()<vertex.size()){
				        int f1=0;
					for (int j=0;j<vertex.size();j++){
						if(tmpv.get(i)==vertex.get(j)){
							ArrayList<T> tmp=new ArrayList<T>();
							for (int k=0;k<edge.get(j).size();k++){
								if(!tmpv.contains(edge.get(j).get(k))){
									if(f1==0)
									  {
									    tmp.add(tmpv.get(i));
									    f1=1;
									    }
									
									f=1;
									tmpv.add(edge.get(j).get(k)); 
									//tmp.add(tmpv.get(i));
									tmp.add(edge.get(j).get(k));
									//System.out.println(
				                                   }
							}
							if(f==1){
								edge1.add(tmp);
								f=0;
							}
							
						break;
						}
				    }
				    i++;
				}
			flag=1;
		   }		                       
				
			
		/*	System.out.println("unique MST according to ="+vertex.get(x));
			//-------------------------------------------------------- 
			   for(int i1=0;i1<edge1.size();i1++)
			       {
			             System.out.print(edge1.get(i1).get(0)+"  : ");
			            for(int j=1;j<edge1.get(i1).size();j++)
			                 System.out.print(edge1.get(i1).get(j)+"  ");
			            System.out.println();
			        } 
			
			//--------------------------------------------------------*/
			    
			   
	  			   T fn=this.vertex.get(y);
				   T fn1=this.vertex.get(y);
				   for(int i2=edge1.size()-1;i2>=0;i2--){
					   for(int j=1;j<edge1.get(i2).size();j++){
						   if(fn==edge1.get(i2).get(j)){
							   fn1=fn;
							   fn=edge1.get(i2).get(0);  
							   break;
						   }   
					   }
				   }
				   //return fn1;
				  // System.out.println("x:"+vertex.get(x)+" y "+vertex.get(y)+" fn1"+fn1);
				   temp.add(this.vertex.get(y));
				   temp.add(fn1);
			           con.add(temp);
			}
			
		} //System.out.println("first immediate"+fn1);
		lookUpTable.put(this.vertex.get(x),con);
	}
    

}  

public T findImmediateNode(T x,T y){
	
/*	Iterator<T> keySetIterator =lookUpTable.keySet().iterator();

while(keySetIterator.hasNext()){
  T key = keySetIterator.next();
  System.out.println("\nkey: " + key + "\n value: \n" + lookUpTable.get(key));
}*/
	
	
	//Iterator it = lookUpTable.entrySet().iterator();
	ArrayList<ArrayList<T>> temp = new ArrayList<ArrayList<T>>();
	temp = this.lookUpTable.get(x);
	for(int i=0;i<temp.size();i++){
		for(int j=0;j<temp.get(i).size();j++){
			if(temp.get(i).get(0)==y){
				//System.out.println("x "+x+" y "+y+" imm "+temp.get(i).get(1));
			    return (temp.get(i).get(1));
			}
		}

	}
	return x;
	}
	
	
	
	
public void look_upDisplay()
{
  	
  	Iterator<T> keySetIterator =lookUpTable.keySet().iterator();

while(keySetIterator.hasNext()){
  T key = keySetIterator.next();
         System.out.println("\nkey: " + key + "\n value: \n" + lookUpTable.get(key));
   }	
	
}	
	
	
	
	
 
 
 /* public T look_up_table(T x,T y)
 {
 //----generrating unique MST according to Host()-----------//  
     int cnt=0;
     
       ArrayList<ArrayList<T>> edge1= new ArrayList<ArrayList<T>>();  
       int sz=edge.size();
       int n=vertex.size();
       int i=0,f=0;
       ArrayList<T> tmpv=new ArrayList<T>();
       tmpv.add(x);
   
     while(tmpv.size()<vertex.size())   
     {
      for (int j=0;j<vertex.size();j++)
      {
        if(tmpv.get(i)==vertex.get(j))
         {
           ArrayList<T> tmp=new ArrayList<T>();
           for (int k=0;k<edge.get(j).size();k++)
            {
               if(!tmpv.contains(edge.get(j).get(k)))
                  {
                      f=1;
                      tmpv.add(edge.get(j).get(k)); 
                      //tmp.add(tmpv.get(i));
                      tmp.add(edge.get(j).get(k));
                  }
              }
              if(f==1) {
                  edge1.add(tmp);
                  f=0;
                 } 
           }
        }
     i+=1;
     }                       


System.out.println("unique MST according to ="+x);
//-------------------------------------------------------- 
   for(int i1=0;i1<edge1.size();i1++)
       {
             System.out.print(tmpv.get(i1)+"  : ");
            for(int j=0;j<edge1.get(i1).size();j++)
                 System.out.print(edge1.get(i1).get(j)+"  ");
            System.out.println();
        } 

//--------------------------------------------------------






  
     T fn=y;
     T fn1=y;
  
      for(int i2=edge1.size()-1;i2>=0;i2--)
       {
         for(int j=0;j<edge1.get(i2).size();j++)
           {
             if(fn==edge1.get(i2).get(j))
               {
                  fn1=fn;
                  fn=tmpv.get(i2);  
                  break;
               }
               
            }
        }
        
     //System.out.println("first immediate"+fn1);
     
      return(fn1);
   
    /* look_up[k][0][0]=getHost();
     look_up[k][0][1]=con.;
     look_up[k][0][2]=fn1;
     System.out.print(" | "+look_up[k][0][0]+" | "+look_up[k][0][1]+"| "+look_up[k][0][2]);
     System.out.println();
     }
  }

//

}*/  
   
   
    public void display()
    {
        for(int i=0;i<vertex.size();i++)
        {
            System.out.print(vertex.get(i)+"  : ");
            for(int j=0;j<edge.get(i).size();j++)
                System.out.print(edge.get(i).get(j)+"  ");
            System.out.println();
        }
    }
    // Write the Adjacency List
    public void write()
    {
        try
        {
            FileWriter fstream = new FileWriter("Adjacency.txt");
            BufferedWriter out = new BufferedWriter(fstream);
            out.write("Number of Wifis : "+vertex.size()+"\n");
            //out.write(cnt+"   "+cnta+"  "+cf+"\n");
            for(int i=0;i<vertex.size();i++)
            {
                    out.write(vertex.get(i).toString()+" :  ");
                    for(int j=0;j<edge.get(i).size();j++)
                    {
                            out.write(edge.get(i).get(j).toString()+"  ");
                    }
                    out.write("\n");
            }
            out.close();
            fstream.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }	
    }
  /* 
  
      //Get the next vertex in the graph
    public T nextNode(T x,T y)
    {
        return n.get(vertex.indexOf(x)).get(vertex.indexOf(y));
    }

  
  
  
   //If there is a path in between x and y
    public boolean isPath(T x,T y)
    {
        if(vertex.contains(x) && vertex.contains(y))
        {
            int cnt=0;
            int ind=0;
            ArrayList<T> vlist=new ArrayList<T>();
            vlist.add(x);
            do
            {
                cnt=0;
                for(int l=ind;l<=ind+cnt;l++)
                {
                    for(T i: edge.get(vertex.indexOf(vlist.get(l))))
                    {
                        if(!vlist.contains(i))
                        {
                            vlist.add(i);
                            cnt++;
                        }
                    }
                    if(vlist.contains(y)) return true;
                }
            }while(cnt>0);
            //if(vlist.contains(y)) return true;
            return false;                
        }
        return false;
    }
    // returns true is the y vertex is adjacent to x
    public boolean isAdjacent(T x,T y)
    {
            return edge.get(vertex.indexOf(x)).contains(y);
    }
    //Returns the next in path vertex
    public T nextInPath(T x,T y)
    {
        if(isPath(x,y))
        {            
            int cnt=0;
            int ind=0;
            ArrayList<T> vlist=new ArrayList<T>();
            ArrayList<Integer> vplist=new ArrayList<Integer>();
            vlist.add(x);
            vplist.add(-1);
            do
            {
                cnt=0;
                for(int l=ind;l<=ind+cnt;l++)
                {
                    for(T i: edge.get(vertex.indexOf(vlist.get(l))))
                    {
                        if(!vlist.contains(i))
                        {
                            vlist.add(i);
                            vplist.add(l);
                            cnt++;
                        }
                    }
                    if(vlist.contains(y)) break;
                }
                int py=vplist.get(vlist.indexOf(y));
                if(py==0) return y;
                while(vplist.get(py)!=0)
                    py=vplist.get(py);
                return vlist.get(py);
            }while(cnt>0);
            //if(vlist.contains(y)) return true;  
        }
        return x;
    }
    // add the next vertex to n List
    public void nextV()
    {
        n = new ArrayList<ArrayList<T>>();
        for(int i=0;i<vertex.size();i++)
        {
            ArrayList<T> temp= new ArrayList<T> ();
            for(int j=0;j<vertex.size();j++)
            {
                if(i==j) 
                    temp.add(vertex.get(i));
                else
                    temp.add(nextInPath(vertex.get(i),vertex.get(j)));
            }
            n.add(temp);
        }   
    }*/
    // writes next in path vertexs
    public void writenext()
    {
        try
        {

                FileWriter fstream = new FileWriter("Next.txt");
                BufferedWriter out = new BufferedWriter(fstream);
                for(int i=0;i<n.size();i++)
                {
                        out.write(vertex.get(i).toString()+" :  ");
                        for(int j=0;j<n.get(i).size();j++)
                        {
                                out.write(n.get(i).get(j).toString()+"  ");
                        }
                        out.write("\n");
                }
                out.close();
                fstream.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }	
    }
}
