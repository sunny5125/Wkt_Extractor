import os
import sys
import math

def parsefile(wifi,mule,total):
    f2=open("gui_input.txt","r")
    lines=f2.readlines()
    total=int(lines[1])
    f2.close()
    f1=open(wifi,"r")
    f2=open("gui_input.txt","a")
    lines=f1.readlines()
    c=0
    for line in lines:
        print line
        c=c+1
    f1.close()
    f2.writelines(str(c)+"\n")
    gc=[]
    wifi=0
    w_loc=[0]*total
    w_no=(total*2)
    grpcen=[]
    for line in lines:
        if len(line.strip()) == 0 :
            continue;
        gc=line[(line.index("[")+1):-2].split(", ")
        wifi=line[:line.index(":")-1]
        grpcen.append(wifi)
        '''f2.writelines(wifi+"\n")'''
        for i in gc:
            if i=='':
                print i
                w_loc[int(wifi)-total-1]=w_no
                break
            else:
                print i
                w_loc[int(i)-total-1]=w_no
        w_no+=1
    w_loc=",".join(map(str,w_loc))
    print grpcen
    grpcen=map(int,grpcen)
    grpcen[:]=[x-1 for x in grpcen]
    l=len(grpcen)
    grpcen=",".join(map(str,grpcen))
    f2.writelines(w_loc+"\n")
    f2.writelines(str(l)+"\n")
    f2.writelines(grpcen+"\n")
    f1.close()
    f1=open(mule,"r")
    lines=f1.readlines()
    c=0
    traj=[]
    for line in lines:
        if "Mule ID" in line:
            c+=1
    f2.writelines(str(c)+"\n")
    for line in lines:
        if "[trajectory]" in line:
            c-=1
            tra=line[:line.index("[")-1].split(",")
            tra=map(int,tra)
            tra[:]=[x-1 for x in tra]
            tra=",".join(map(str,tra))
            f2.writelines(tra+"\n")
            
        if c==0:
            break
        
    

if len(sys.argv)!= 5:
    print "Invalid argument"
    quit()
wifi=sys.argv[1]
mule=sys.argv[2]
total=int(sys.argv[3])
#path ="./OUTPUT/120/"
path=sys.argv[4]
dir = os.listdir(path)
if wifi not in dir or mule not in dir:
    print "file not found"
    quit()
parsefile(path+wifi,path+mule,total)

    
