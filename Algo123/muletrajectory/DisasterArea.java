package muletrajectory;

public class DisasterArea {

    float v,p,L;
    float g;
    int n;
    int size;
    float rswifi;
    float Fdtn;
    static int wifiRange=4000; //in metres

    DisasterArea(float L,int size)
    {
        this.v=1; //[speed of vehicle in m/min]
        this.p=8; //[size of data packet in MB]
        this.L=L; //[ Latency in minutes]
        this.n=10; //[Number of DTN nodes registered per IDB
        this.size= size;
        this.rswifi= 20; // 20 Mbps ie, 20/8 = ~3 MBPS
        this.g= 10;// 10 packets/hour/DTN
        this.Fdtn= 20; //in minutes
    }

}
