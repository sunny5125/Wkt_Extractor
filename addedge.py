import os
import sys
import math

class Point(object):
    x=0.0
    y=0.0
    def __init__(self,x,y):
        self.x=x
        self.y=y
    def dist(self,p):
        d=math.sqrt(math.pow(self.x-p.x,2)+math.pow(self.y-p.y,2))
        return d

maxy=0
def readpoints(f):
    global maxy

    points=[]
    f1=open(f,"r")
    lines=f1.readlines()

    for line in lines:
        st=line.rstrip("\n")
        st=st[12:-1].split(",")
        for p in st:
            p=p.strip().split(" ")
            point=Point(float(p[0]),float(p[1]))
            points.append(point)
            if point.y>maxy:
                maxy=point.y
    f1.close()
    return points
    
def addline(mapf,db):
    global maxy
    mappoints=readpoints(mapf)
    dbpoints=readpoints(db)
    f1=open(mapf,"r")
    f2=open("data.osm.wkt","w")
    lines=f1.readlines()
    print lines
    for line in lines:
        f2.writelines(line)
    f1.close()
    f2.close()
    f1=open("data.osm.wkt","a")
    for p in dbpoints[1:]:
        minp=mappoints[0]
        mind=p.dist(minp)
        for mp in mappoints[1:]:
            d=p.dist(mp)
            if d<mind:
                minp=mp
                mind=d
        print mind,minp.x,minp.y
        if mind!=0:
            f1.writelines("LINESTRING ("+str(p.x)+" "+str(p.y)+", "+str(minp.x)+" "+str(minp.y)+")\n")
    f1.close()
    f3=open("Algo123/muletrajectory/Input/distance.txt","w")
    f2=open("gui_input.txt","w")
    f2.writelines(str(maxy)+"\n")
    line=str(len(dbpoints)-1)+"\n"
    f2.writelines(line)
    f3.writelines(line)
    for p in dbpoints[1:]:
        line=str(p.x)+" "+str(p.y)+"\n"
        f2.writelines(line)
        f3.writelines(line)
    f2.close()
    f3.close()
    

if len(sys.argv)!= 3:
    print "Invalid argument"
    quit()
mapf=sys.argv[1]
db=sys.argv[2]
dir = os.listdir(".")
if not mapf in dir or db not in dir:
    print "file not found"
    quit()
addline(mapf,db)
    
    
    
