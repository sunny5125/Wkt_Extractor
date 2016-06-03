/*
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details.
 */
package routing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.*;
import java.io.*;

import core.Connection;
import core.DTNHost;
import core.Message;
import core.MessageListener;
import core.Settings;
import core.SimClock;
import core.Tuple;

/**
 * Superclass of active routers. Contains convenience methods (e.g.
 * {@link #getOldestMessage(boolean)}) and watching of sending connections (see
 * {@link #update()}).
 */
public abstract class ActiveRouter extends MessageRouter
{
    /** Delete delivered messages -setting id ({@value}). Boolean valued.
     * If set to true and final recipient of a message rejects it because it
     * already has it, the message is deleted from buffer. Default=false. */
    public static final String DELETE_DELIVERED_S = "deleteDelivered";
    public static final String MCS_MSG = "mcs_message";
    public static ArrayList<Integer> clusterWifi;
    // Restricted routing specific inputs
    public static boolean wr,tr;
    public static Graph<DTNHost> wifigraph=new Graph<DTNHost>();
    public static Graph<DTNHost> wifitree=new Graph<DTNHost>();
    public static String mcsAdd= "mcs_address";
    public static int mcsId;
    /** Node number of the first node of each group
    */
    public static final String BT = "firstBT";
    public static final String CD = "firstCD";
    public static final String ADB = "firstADB";
    public static final String SAT = "firstSAT";
    public static final String WIFI = "firstWIFI";
    public static final String cWifi="clusterWifi";
    public static final String tDM="DMS";  // string to read total data mules from setting file
    /** Tag for the node type
    */
    public static final String BT_TAG = "bt_tag";
    public static final String CD_TAG = "cd_tag";
    public static final String ADB_TAG = "adb_tag";
    public static final String SAT_TAG = "sat_tag";
    public static final String WIFI_TAG = "wifi_tag";

    /** Number of nodes in each cluster
    */
    public static final String NPC = "nodespc"; // string to read total nodes per cluster

    /** Stage 1 end time
    */
    public static final String STG1 = "stage1";

    /** Warmup time to analyse the network in each stage
    */
    public static final String WARMUP = "stage_warmups";

    /** List of groupcenters
    */
    public static final String GRP_CENTER = "group_centers";


    /** Restricted routing variables
    */
    private int firstBT;
    private int firstCD;
    private int firstADB;
    private int firstSAT;
    private int firstWIFI;
    private int warmups[] = new int[5];
    private int totDM; // total data mules

    private ArrayList<Integer> SP; // stores the node connected to DataMule
    private int nodes_per_cluster;
    private int first_stage;
    private ArrayList<ArrayList<Integer>> cd_cluster;  // stores the non GCs visited by each data mules

    // the node tags
    private String bt_tag;
    private String cd_tag;
    private String adb_tag;
    private String sat_tag;
    private String wifi_tag;
    private ArrayList<String> msg_type;

    /** should messages that final recipient marks as delivered be deleted
     * from message buffer */
    protected boolean deleteDelivered;
    private String mcs_msg;

    /** prefix of all response message IDs */
    public static final String RESPONSE_PREFIX = "R_";
    /** how often TTL check (discarding old messages) is performed */
    public static int TTL_CHECK_INTERVAL = 60;
    /** connection(s) that are currently used for sending */
    protected ArrayList<Connection> sendingConnections;
    /** sim time when the last TTL check was done */
    private double lastTtlCheck;

    /**
     * Constructor. Creates a new message router based on the settings in
     * the given Settings object.
     * @param s The settings object
     */
    public ActiveRouter(Settings s) {
        super(s);

        if (s.contains(DELETE_DELIVERED_S))
                {
                    this.deleteDelivered = s.getBoolean(DELETE_DELIVERED_S);
        }
        else
                {
                    this.deleteDelivered = false;
        }

        this.mcsId=Integer.parseInt(s.getSetting(mcsAdd));
        this.mcs_msg = s.getSetting(MCS_MSG);
        this.firstBT = s.getInt(BT);
        this.firstCD = s.getInt(CD);
        this.firstADB = s.getInt(ADB);
       // this.firstSAT = s.getInt(SAT);
        this.firstWIFI = s.getInt(WIFI);
        this.nodes_per_cluster = s.getInt(NPC);
        this.first_stage = s.getInt(STG1);
        this.warmups = s.getCsvInts(WARMUP);
        this.bt_tag = s.getSetting(BT_TAG);
        this.cd_tag = s.getSetting(CD_TAG);
        this.adb_tag = s.getSetting(ADB_TAG);
       // this.sat_tag = s.getSetting(SAT_TAG);
        this.wifi_tag = s.getSetting(WIFI_TAG);



               // System.out.println(msg_type);
        this.clusterWifi = s.getCsvArrayList(cWifi);
        this.SP = s.getCsvArrayList(GRP_CENTER);  // cluster # of GCs
        this.totDM = s.getInt(tDM); // total data mules
        cd_cluster= new ArrayList<ArrayList<Integer>>();
        for(int i=0;i<totDM;i++)
        {
            cd_cluster.add(s.getCsvArrayList("CD"+Integer.toString(i+1))); // reading the non GCs visited by each data mule
        }
    }

    /**
     * Copy constructor.
     * @param r The router prototype where setting values are copied from
     */
    protected ActiveRouter(ActiveRouter r) {
        super(r);
        this.mcsId=r.mcsId;
        this.deleteDelivered = r.deleteDelivered;
        this.mcs_msg = r.mcs_msg;
        this.firstBT = r.firstBT;
        this.firstCD = r.firstCD;
        this.firstADB = r.firstADB;
       // this.firstSAT = r.firstSAT;
        this.firstWIFI = r.firstWIFI;
        this.nodes_per_cluster = r.nodes_per_cluster;
        this.first_stage = r.first_stage;
        this.warmups = r.warmups;
        this.bt_tag = r.bt_tag;
        this.cd_tag = r.cd_tag;
        this.adb_tag = r.adb_tag;
       // this.sat_tag = r.sat_tag;
        this.wifi_tag = r.wifi_tag;
        this.SP = r.SP;
        this.totDM=r.totDM;
//      this.interactions = new ArrayList<String>();
    //  this.reachedadb = new ArrayList<String>();
        this.cd_cluster=r.cd_cluster;
    }

    @Override
    public void init(DTNHost host, List<MessageListener> mListeners)
    {
        super.init(host, mListeners);
        this.sendingConnections = new ArrayList<Connection>(1);
        this.lastTtlCheck = 0;
    }

    /**
     * Called when a connection's state changes. This version doesn't do
     * anything but subclasses may want to override this.Somir
     */
    @Override
    public void changedConnection(Connection con)
    {
        DTNHost x,y;
        x=getHost();  // extracting host node
        y=con.getOtherNode(x); // extracting otehr node in the connecton
        if(x.toString().startsWith(wifi_tag) && y.toString().startsWith(wifi_tag))   // if both the nodes in the connection are LWCs
        {
            //System.out.println("vertex x= "+x+" vertex y= "+y);
            wifigraph.insertEdge(x,y); // insert the edge in the wifi graph
        }

    }

    @Override
    public boolean requestDeliverableMessages(Connection con)
    {
        if (isTransferring()) {
            return false;
        }

        DTNHost other = con.getOtherNode(getHost());
        /* do a copy to avoid concurrent modification exceptions
         * (startTransfer may remove messages) */
        ArrayList<Message> temp = new ArrayList<Message>(this.getMessageCollection());
        for (Message m : temp)
        {
            if (other == m.getTo())
            {
                if (startTransfer(m, con) == RCV_OK)
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean createNewMessage(Message m)
    {
        makeRoomForNewMessage(m.getSize());
        return super.createNewMessage(m);
    }

    @Override
    public int receiveMessage(Message m, DTNHost from) {
        if(m!= null)
        {
            int recvCheck = checkReceiving(m);
            if (recvCheck != RCV_OK)
            {
                return recvCheck;
            }

            // Added to find the load of messages being received

            //this.getHost().WriteIncomingLoad(m,SimClock.getIntTime(),from);


            // seems OK, start receiving the message
               return super.receiveMessage(m, from);
        }
        else
            return -1;
    }

    @Override
    public Message messageTransferred(String id, DTNHost from)
    {
        Message m = super.messageTransferred(id, from);

        /**
         *  N.B. With application support the following if-block
         *  becomes obsolete, and the response size should be configure
         *  to zero.
         */
        // check if msg was for this host and a response was requested
        if (m.getTo() == getHost() && m.getResponseSize() > 0)
        {
            // generate a response message
            Message res = new Message(this.getHost(),m.getFrom(),
                    RESPONSE_PREFIX+m.getId(), m.getResponseSize());
            this.createNewMessage(res);
            this.getMessage(RESPONSE_PREFIX+m.getId()).setRequest(m);
        }

        return m;
    }

    /**
     * Returns a list of connections this host currently has with other hosts.
     * @return a list of connections this host currently has with other hosts
     */
    protected List<Connection> getConnections() {
        return getHost().getConnections();
    }

    /**
     * Tries to start a transfer of message using a connection. Is starting
     * succeeds, the connection is added to the watch list of active connections
     * @param m The message to transfer
     * @param con The connection to use
     * @return the value returned by
  * {@link Connection#startTransfer(DTNHost, Message)}
     */
    protected int startTransfer(Message m, Connection con) {
        int retVal;

        boolean shouldsend = true;
        DTNHost to = con.getOtherNode(getHost());
        shouldsend = shouldSendMessage(m,to,con);
        //System.out.println("--NULL--");

        //  System.out.println(" x "+getHost()+" y "+to+" m.getFrom() "+m.getFrom()+" m.getTo() "+m.getTo());

        if(shouldsend == false)
            return -1;



        if (!con.isReadyForTransfer())
        {
            return TRY_LATER_BUSY;
        }
        retVal = con.startTransfer(getHost(), m);
        if (retVal == RCV_OK)
        {
            addToSendingConnections(con);

        }
        else if (deleteDelivered && retVal == DENIED_OLD && m.getTo() == con.getOtherNode(this.getHost()))
        {
            /* final recipient has already received the msg -> delete it */
            this.deleteMessage(m.getId(), false);
        }

        /*  If message is being transferred,
            then update the list of contacted nodes
            if required
        */

        return retVal;
    }



    /**
    * Check if the message should be sent or not
        * Somir
    */

    public boolean shouldSendMessage(Message m, DTNHost to,Connection con)
    {
      // System.out.println("\nGraph:\n");
        DTNHost x,y;
        boolean shouldsend=false;
       
        x=getHost();   // host node

        y=con.getOtherNode(getHost());   // extracting the other node

        //System.out.println("x "+x+" y "+y);

        if(x.toString().startsWith(wifi_tag))  // host is a wifi device
        {
                    if(y.toString().startsWith(wifi_tag))  // other node is a wifi device
                    {
                        DTNHost mcs=null;

                        if( !wr) // indicating graph status being written into file or not
                        {
                            wr=true;
                            if(wifigraph.vertex.size()!=0)
                            {
                                // getting the reference of the MCS node
                                for(int ni=0;ni<wifigraph.vertex.size();ni++)
                                {
                                     //System.out.println("wifigraph vertex"+wifigraph.vertex.get(ni).getAddress());
                                    if(wifigraph.vertex.get(ni).getAddress()==mcsId)
                                        mcs=wifigraph.vertex.get(ni);
                                }

                                int pos=wifigraph.vertex.indexOf(mcs); // index of node MCS in wifi graph
                                Collections.swap(wifigraph.vertex,0,pos);
                                Collections.swap(wifigraph.edge,0,pos);
                                // making the MCS as the 1st node of the wifi graph


                                 // System.out.println("\nGraph:\n");
                               //   wifigraph.display();
                                 System.out.println("\nTree:\n");
                                 wifitree=wifigraph.getTree();    // extracting the tree structure of LWCs from the current graph
						          wifitree.display();
						         wifitree.look_up_table();
                                 wifitree.look_upDisplay();
                                   // extracting the tree structure of LWCs from the current graph
                                                            }

                            /*if(!tr)
                            {
                                System.out.println("\nGraph:\n");
                                wifigraph.display();
                                System.out.println("\nTree:\n");
                                wifitree.display();
                                wifitree.write();
                                wifitree.writenext(); // writing status of tree to file
                                tr=true;
                                /*System.out.println("\nCD Cluster-->\n");

                                /*for(int i=0;i<totDM;i++)
                                {
                                    System.out.print("Data Mule:"+SP.get(i)+"  : ");
                                    for(int j=0;j< cd_cluster.get(i).size();j++)
                                        System.out.print("DropBox:"+cd_cluster.get(i).get(j)+"  ");
                                    System.out.println();
                                }
                            }  */
                        }




                        //nsx=wifitree.findImmediateNode(x,m.getTo()); // Next Wifi node in delivering message from X to Destination of Message
                         DTNHost nsx,z;
                         //nsx=wifitree.nextNode(x,getClusterWifi(m.getTo()));
                        z=getClusterWifi(m.getTo());
			         	nsx=wifitree.findImmediateNode(x,z);
                      //  nsx=wifitree.look_upDisplay(x,z);

                     //  System.out.println("nsx="+nsx+" "+"m.getTo="+m.getTo());

                        if(y==nsx )  // if y is the next wifi device the message should be sent to
                        {
                            //  System.out.println(" entered wifi "+x+" "+y+" "+nsx);
                              shouldsend=true;

                        }
                    }

        //the othernode is a dropbox
                    else if(y.toString().startsWith("ADB"))  // other node is a Dropbox
                    {
                        if(x==getClusterWifi(m.getTo())){ // if the message is destined at that cluster
                        //        System.out.println(" wifi to adb");
                                shouldsend=true;
                        }
                    }
        }
        //Device scheduling code

        if(x.toString().startsWith("ADB")) // if the host is a Dropbox
        {
                    if(y.toString().startsWith(wifi_tag)) // if the other node is a Wifi
                    {
                        if(getClusterWifi(m.getTo())!=y)   // if the message is not destined in the current cluster then upload to wifi
                        {
                            System.out.println(" adb to wifi "+x+" "+y);
                            shouldsend=true;
                        }
                    }
                    else if(y.toString().startsWith(cd_tag))  // if the other node is a data mule
                    {


                        int cdno=y.getAddress()-firstCD;// get the number of the data mule
                        int adbno=x.getAddress();    // address of Dropbox
                        if(m.toString().startsWith("N"))  // N type message
                        {   // System.out.println("from adb to cd for N"+x+" "+y+" "+m);
                            if(SP.contains(adbno) && cd_cluster.get(cdno).contains(findCluster(m.getTo())-1)&& (findCluster(m.getFrom())!=findCluster(m.getTo())))
                            {
                                 System.out.println("from adb to cd for N"+x+" "+y+" "+m);
                                // if current Dropbox is a GC and the data mule visits the cluster where the message is destined
                                shouldsend=true;
                            }
                        }
                        else if(m.toString().startsWith("M")) // M type message
                        {
                            if(!SP.contains(adbno)){   // if Dropbox is not a GC
                                 System.out.println("from adb to cd for M "+x+" "+y+" "+m);
                               shouldsend=true;
                              }
                        }
                        //System.out.println(m.getId()+"-->"+y.toString()+":"+adbno);//Delete

                    }
                    else if(y.toString().startsWith("dtn"))  // if other node is a DTN NODE
                    {
                       


                      

                        int adbno=x.getAddress(); // address of the Dropbox

                        if(findCluster(m.getTo())==(adbno-(firstADB-1)) && m.toString().startsWith("N"))  //if N type messsage and destined at the current cluster
                        {
                      //     
                            shouldsend=true;
                        }

                        try
                        {
                            if(!shouldsend)
                                    return false;
                            List<Connection> s=x.getConnections(); // list of all connections for the Dropbox
                            ArrayList<DTNHost> conn=new ArrayList<DTNHost>();
                            for (Connection c:s)
                                conn.add(c.getOtherNode(x));   // adding all the nodes connected to the current Dropbox
                            DTNHost dm=null;

                            for( DTNHost h:conn)
                            {
                                if(h.toString().startsWith(cd_tag)) // if the connected device is a datamule then give DM higher priority
                                {
                                    dm=h;
                                    shouldsend=false;
                                }
                            }

                            if(!shouldsend)
                                return false;
                       // System.out.println("from adb to bt "+x+" "+y+" "+m);
                        }
                        catch(Exception e)
                        {
                            System.out.println("Active Router:"+e);
                        }
                    }
        }
        if(x.toString().startsWith(cd_tag)) // if host is a data mule
        {
            
            if(y.toString().startsWith("ADB"))  // if the other node is a Dropbox
            {

                if(m.toString().startsWith("N")) // n type message
                {
                    int adbno=y.getAddress(); // id of Dropbox
                    if(findCluster(m.getTo())==adbno-(firstADB-1))  // if the message is destind for the Dropbox
                                        {

                        //System.out.println("from cd to adb for N"+x+" "+y);
                        shouldsend=true;
                                        }
                }
                else if(m.toString().startsWith("M")) // m type message
                {
                    if(SP.indexOf(y.getAddress())!=-1){ // if the Dropbox is not a GC
                        System.out.println("from cd to adb for M "+x+" "+y+m);
                        shouldsend=true;
                       }
                }

            }
        }


        if(x.toString().startsWith("dtn"))   // host is a DTN Node
        {
            if(y.toString().startsWith("ADB"))  // other node is a Dropbox
            {


                if(m.toString().startsWith("M")) // m type message
                {
                    shouldsend=true;
                   // System.out.println("from bt to adb for M "+x+" "+y);
                   // if(x.toString().startsWith("dtn3") && y.toString().startsWith("ADB5"))
                      //   System.out.print("from bt to adb for M "+x+" "+y);


                    try{
                    if(!shouldsend)
                        return false;
                    List<Connection> s=y.getConnections();   // list of all connections for the Dropbox
                    ArrayList<DTNHost> conn=new ArrayList<DTNHost>();
                    for (Connection c:s)
                        conn.add(c.getOtherNode(y)); // adding all devices connected to the Dropbox
                    DTNHost dm=null;

                    for( DTNHost h:conn)
                    {   //System.out.print(h.toString()+" ");

                        if(h.toString().startsWith(cd_tag)) // if any device connected to the dropbox is a datamule then give it priority
                        {       //return false;
                                dm=h;
                                shouldsend=false;
                                //System.in.read();
                        }
                    }
                    //System.out.println(dm.toString()+"  "+dm.getNrofMessages());
                    //System.in.read();
                

                    if(!shouldsend)
                        return false;
                    //System.out.println();

                    }
                    catch(Exception e)
                    {}

                }
            }
            if(x.toString().startsWith("dtn") && y.toString().startsWith("dtn"))
                   // System.out.println("checking bt to bt "+x+" "+y);

            if(y.toString().startsWith("dtn")) // if the other node is a DTN NODE
            {  //System.out.println(" clx "+findCluster(x)+" cly "+findCluster(y));
                if(findCluster(x)==findCluster(y)){ // if both host and the other node is in the same cluster
                    //System.out.println("from bt to bt M "+x+" "+y);
                    shouldsend=true;
                   }
            }

        }
        return shouldsend;
    }

    /*
    * Check if there is any other node except "to" whom the
    * message can be relayed for faster delivery in hosts contact list
    * Check for increasing number of hops for all the nodes
    * that have been contacted.
    */
  /*  public boolean getWifi(Message m, DTNHost to){
        int to_len, host_len;
        to_len = to.contacted.size();
        host_len = getHost().contacted.size();
        for(int i = 0; i < host_len; i++){
            if(getHost().contacted.get(i).contacted.contains(m.getTo())){
                return true;
            }
        }
        for(int j = 0; j < to_len; j++){
            if(to.contacted.get(j).contacted.contains(m.getTo())){
                return true;
            }
        }
        return false;
    }*/
    /**
     * Makes rudimentary checks (that we have at least one message and one
     * connection) about can this router start transfer.
     * @return True if router can start transfer, false if not
     */
    protected boolean canStartTransfer() {
        if (this.getNrofMessages() == 0) {
            return false;
        }
        if (this.getConnections().size() == 0) {
            return false;
        }

        return true;
    }

    /**
     * Checks if router "wants" to start receiving message (i.e. router
     * isn't transferring, doesn't have the message and has room for it).
     * @param m The message to check
     * @return A return code similar to
     * {@link MessageRouter#receiveMessage(Message, DTNHost)}, i.e.
     * {@link MessageRouter#RCV_OK} if receiving seems to be OK,
     * TRY_LATER_BUSY if router is transferring, DENIED_OLD if the router
     * is already carrying the message or it has been delivered to
     * this router (as final recipient), or DENIED_NO_SPACE if the message
     * does not fit into buffer
     */
    protected int checkReceiving(Message m) {
        if (isTransferring()) {
            return TRY_LATER_BUSY; // only one connection at a time
        }

        if ( hasMessage(m.getId()) || isDeliveredMessage(m) ){
            return DENIED_OLD; // already seen this message -> reject it
        }

        if (m.getTtl() <= 0 && m.getTo() != getHost()) {
            /* TTL has expired and this host is not the final recipient */
            return DENIED_TTL;
        }

        /* remove oldest messages but not the ones being sent */
        if (!makeRoomForMessage(m.getSize())) {
            return DENIED_NO_SPACE; // couldn't fit into buffer -> reject
        }

        return RCV_OK;
    }

    /**
     * Removes messages from the buffer (oldest first) until
     * there's enough space for the new message.
     * @param size Size of the new message
     * transferred, the transfer is aborted before message is removed
     * @return True if enough space could be freed, false if not
     */
    protected boolean makeRoomForMessage(int size){
        if (size > this.getBufferSize()) {
            return false; // message too big for the buffer
        }

        int freeBuffer = this.getFreeBufferSize();
        /* delete messages from the buffer until there's enough space */
        while (freeBuffer < size) {
            Message m = getOldestMessage(true); // don't remove msgs being sent

            if (m == null) {
                return false; // couldn't remove any more messages
            }

            /* delete message from the buffer as "drop" */
            deleteMessage(m.getId(), true);
            freeBuffer += m.getSize();
        }

        return true;
    }

    /**
     * Drops messages whose TTL is less than zero.
     */
    protected void dropExpiredMessages() {
        Message[] messages = getMessageCollection().toArray(new Message[0]);
        for (int i=0; i<messages.length; i++) {
            int ttl = messages[i].getTtl();
            if (ttl <= 0) {
                deleteMessage(messages[i].getId(), true);
            }
        }
    }

    /**
     * Tries to make room for a new message. Current implementation simply
     * calls {@link #makeRoomForMessage(int)} and ignores the return value.
     * Therefore, if the message can't fit into buffer, the buffer is only
     * cleared from messages that are not being sent.
     * @param size Size of the new message
     */
    protected void makeRoomForNewMessage(int size) {
        makeRoomForMessage(size);
    }


    /**
     * Returns the oldest (by receive time) message in the message buffer
     * (that is not being sent if excludeMsgBeingSent is true).
     * @param excludeMsgBeingSent If true, excludes message(s) that are
     * being sent from the oldest message check (i.e. if oldest message is
     * being sent, the second oldest message is returned)
     * @return The oldest message or null if no message could be returned
     * (no messages in buffer or all messages in buffer are being sent and
     * exludeMsgBeingSent is true)
     */
    protected Message getOldestMessage(boolean excludeMsgBeingSent) {
        Collection<Message> messages = this.getMessageCollection();
        Message oldest = null;
        for (Message m : messages) {

            if (excludeMsgBeingSent && isSending(m.getId())) {
                continue; // skip the message(s) that router is sending
            }

            if (oldest == null ) {
                oldest = m;
            }
            else if (oldest.getReceiveTime() > m.getReceiveTime()) {
                oldest = m;
            }
        }

        return oldest;
    }

    /**
     * Returns a list of message-connections tuples of the messages whose
     * recipient is some host that we're connected to at the moment.
     * @return a list of message-connections tuples
     */
    protected List<Tuple<Message, Connection>> getMessagesForConnected() {
        if (getNrofMessages() == 0 || getConnections().size() == 0) {
            /* no messages -> empty list */
            return new ArrayList<Tuple<Message, Connection>>(0);
        }

        List<Tuple<Message, Connection>> forTuples =
            new ArrayList<Tuple<Message, Connection>>();
        for (Message m : getMessageCollection()) {
            for (Connection con : getConnections()) {
                DTNHost to = con.getOtherNode(getHost());
                if (m.getTo() == to) {
                    forTuples.add(new Tuple<Message, Connection>(m,con));
                }
            }
        }

        return forTuples;
    }

    /**
     * Tries to send messages for the connections that are mentioned
     * in the Tuples in the order they are in the list until one of
     * the connections starts transferring or all tuples have been tried.
     * @param tuples The tuples to try
     * @return The tuple whose connection accepted the message or null if
     * none of the connections accepted the message that was meant for them.
     */
    protected Tuple<Message, Connection> tryMessagesForConnected(
            List<Tuple<Message, Connection>> tuples) {
        if (tuples.size() == 0) {
            return null;
        }

        for (Tuple<Message, Connection> t : tuples) {
            Message m = t.getKey();
            Connection con = t.getValue();
            if (startTransfer(m, con) == RCV_OK) {
                return t;
            }
        }

        return null;
    }

     /**
      * Goes trough the messages until the other node accepts one
      * for receiving (or doesn't accept any). If a transfer is started, the
      * connection is included in the list of sending connections.
      * @param con Connection trough which the messages are sent
      * @param messages A list of messages to try
      * @return The message whose transfer was started or null if no
      * transfer was started.
      */
    protected Message tryAllMessages(Connection con, List<Message> messages) {
        for (Message m : messages) {
            int retVal = startTransfer(m, con);
            if (retVal == RCV_OK) {
                return m;   // accepted a message, don't try others
            }
            else if (retVal > 0) {
                return null; // should try later -> don't bother trying others
            }
        }

        return null; // no message was accepted
    }

    /**
     * Tries to send all given messages to all given connections. Connections
     * are first iterated in the order they are in the list and for every
     * connection, the messages are tried in the order they are in the list.
     * Once an accepting connection is found, no other connections or messages
     * are tried.
     * @param messages The list of Messages to try
     * @param connections The list of Connections to try
     * @return The connections that started a transfer or null if no connection
     * accepted a message.
     */
    protected Connection tryMessagesToConnections(List<Message> messages,
            List<Connection> connections) {
        for (int i=0, n=connections.size(); i<n; i++) {
            Connection con = connections.get(i);
            Message started = tryAllMessages(con, messages);
            if (started != null) {
                return con;
            }
        }

        return null;
    }

    /**
     * Tries to send all messages that this router is carrying to all
     * connections this node has. Messages are ordered using the
     * {@link MessageRouter#sortByQueueMode(List)}. See
     * {@link #tryMessagesToConnections(List, List)} for sending details.
     * @return The connections that started a transfer or null if no connection
     * accepted a message.
     */
    protected Connection tryAllMessagesToAllConnections(){
        List<Connection> connections = getConnections();
        if (connections.size() == 0 || this.getNrofMessages() == 0) {
            return null;
        }

        List<Message> messages =
            new ArrayList<Message>(this.getMessageCollection());
        this.sortByQueueMode(messages);

        return tryMessagesToConnections(messages, connections);
    }

    /**
     * Exchanges deliverable (to final recipient) messages between this host
     * and all hosts this host is currently connected to. First all messages
     * from this host are checked and then all other hosts are asked for
     * messages to this host. If a transfer is started, the search ends.
     * @return A connection that started a transfer or null if no transfer
     * was started
     */
    protected Connection exchangeDeliverableMessages() {
        List<Connection> connections = getConnections();

        if (connections.size() == 0) {
            return null;
        }

        @SuppressWarnings(value = "unchecked")
        Tuple<Message, Connection> t =
            tryMessagesForConnected(sortByQueueMode(getMessagesForConnected()));

        if (t != null) {
            return t.getValue(); // started transfer
        }

        // didn't start transfer to any node -> ask messages from connected
        for (Connection con : connections) {
            if (con.getOtherNode(getHost()).requestDeliverableMessages(con)) {
                return con;
            }
        }

        return null;
    }



    /**
     * Shuffles a messages list so the messages are in random order.
     * @param messages The list to sort and shuffle
     */
    protected void shuffleMessages(List<Message> messages) {
        if (messages.size() <= 1) {
            return; // nothing to shuffle
        }

        Random rng = new Random(SimClock.getIntTime());
        Collections.shuffle(messages, rng);
    }

    /**
     * Adds a connections to sending connections which are monitored in
     * the update.
     * @see #update()
     * @param con The connection to add
     */
    protected void addToSendingConnections(Connection con) {
        this.sendingConnections.add(con);
    }

    /**
     * Returns true if this router is transferring something at the moment or
     * some transfer has not been finalized.
     * @return true if this router is transferring something
     */
    public boolean isTransferring() {
        if (this.sendingConnections.size() > 0) {
            return true; // sending something
        }

        if (this.getHost().getConnections().size() == 0) {
            return false; // not connected
        }

        List<Connection> connections = getConnections();
        for (int i=0, n=connections.size(); i<n; i++) {
            Connection con = connections.get(i);
            if (!con.isReadyForTransfer()) {
                return true;    // a connection isn't ready for new transfer
            }
        }

        return false;
    }

    /**
     * Returns true if this router is currently sending a message with
     * <CODE>msgId</CODE>.
     * @param msgId The ID of the message
     * @return True if the message is being sent false if not
     */
    public boolean isSending(String msgId) {
        for (Connection con : this.sendingConnections) {
            if (con.getMessage() == null) {
                continue; // transmission is finalized
            }
            if (con.getMessage().getId().equals(msgId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks out all sending connections to finalize the ready ones
     * and abort those whose connection went down. Also drops messages
     * whose TTL <= 0 (checking every one simulated minute).
     * @see #addToSendingConnections(Connection)
     */
    @Override
    public void update() {

        super.update();

        /* in theory we can have multiple sending connections even though
          currently all routers allow only one concurrent sending connection */
        for (int i=0; i<this.sendingConnections.size(); ) {
            boolean removeCurrent = false;
            Connection con = sendingConnections.get(i);

            /* finalize ready transfers */
            if (con.isMessageTransferred()) {
                if (con.getMessage() != null) {
                    transferDone(con);
                    con.finalizeTransfer();
                } /* else: some other entity aborted transfer */
                removeCurrent = true;
            }
            /* remove connections that have gone down */
            else if (!con.isUp()) {
                if (con.getMessage() != null) {
                    transferAborted(con);
                    con.abortTransfer();
                }
                removeCurrent = true;
            }

            if (removeCurrent) {
                // if the message being sent was holding excess buffer, free it
                if (this.getFreeBufferSize() < 0) {
                    this.makeRoomForMessage(0);
                }
                sendingConnections.remove(i);
            }
            else {
                /* index increase needed only if nothing was removed */
                i++;
            }
        }

        /* time to do a TTL check and drop old messages? Only if not sending */
        if (SimClock.getTime() - lastTtlCheck >= TTL_CHECK_INTERVAL &&
                sendingConnections.size() == 0) {
            dropExpiredMessages();
            lastTtlCheck = SimClock.getTime();
        }
    }

    /**
     * Method is called just before a transfer is aborted at {@link #update()}
     * due connection going down. This happens on the sending host.
     * Subclasses that are interested of the event may want to override this.
     * @param con The connection whose transfer was aborted
     */
    protected void transferAborted(Connection con) { }

    /**
     * Method is called just before a transfer is finalized
     * at {@link #update()}.
     * Subclasses that are interested of the event may want to override this.
     * @param con The connection whose transfer was finalized
     */
    protected void transferDone(Connection con) { }

    public DTNHost getClusterWifi(DTNHost node)  // Somir returns the wifi for the cluster to which node belongs
    {
        if(node.toString().startsWith(wifi_tag)) // wifi device
            return node;

        int c= findCluster(node); // cluster id for the node

        try
        {
            int sa=clusterWifi.get(c-1); // get the node id for the required wifi device
            for(DTNHost i:wifitree.vertex) // search in the wifi tree for the required wifi device
                        {
                            if( (i.getAddress()) == sa)
                {
                                    //System.out.println(i.toString());
                    return i;
                }

                        }
        }catch(Exception e)
        {
            //System.out.println(i.toString()+"  "+node.toString()+ "  ");
        }

        return node;
    }

    /**
    * Find the cluster of node
        * Somir
    */
    public int findCluster(DTNHost node)
    {
       // System.out.println(node.toString()+ "  "+nodes_per_cluster+" "+node.getAddress()+" "+firstBT);
        if(node.toString().startsWith("dtn"))
        {
            return ((node.getAddress() - firstBT)/nodes_per_cluster + 1);
        }
        return ((node.getAddress() - firstADB) + 1);
    }

}
