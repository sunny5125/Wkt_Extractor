wf= open("dist.txt","w")

v= input()

wf.write(str(v)+"\n")

for i in xrange(v):
	x,y= [float(num) for num in raw_input().split()]
	wf.write(str(int(x*1000))+" "+str(int(y*1000))+"\n")

wf.close()


