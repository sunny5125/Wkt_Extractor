package muletrajectory;

public class DisasterArea {

    float v,p,L;
    float g;
    int n;
    int size;
    float rswifi;
    float Fdtn;
    static int wifiRange=9;

    DisasterArea(float L,int size)
    {
        this.v=1; //[speed of vehicle in km]
        this.p=1; //[size of data packet]
        this.L=L; //[ Latency in minutes]
        this.n=10; //[Number of DTN nodes registered per IDB
        this.size= size;
        this.rswifi= 60;
        this.g= 10;
        this.Fdtn= 0;
      }

}
