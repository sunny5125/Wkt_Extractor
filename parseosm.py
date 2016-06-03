
import os
import sys

for arg in sys.argv:
    print arg
if len(sys.argv) != 3:
    print "argument error"
    quit()

inputosm=sys.argv[1]
outputosm=sys.argv[2]

#print "filename?"
#fn=raw_input().rstrip()

if not inputosm in os.listdir('.'):
    print inputosm+" doesnt exist"
else:
    import xml.etree.ElementTree as ET
    tree = ET.parse(inputosm)
    root = tree.getroot()
    noderefs={} #store nodes with same lat lon
    nodes=[]
    ways=[]
    for node in root.findall('node'):
        lat=node.get('lat')
        lon=node.get('lon')
        id1=node.get('id')
        key=lat+' '+lon
        if key in noderefs:
            noderefs[key].append(node)
        else:
            noderefs[key]=[node]
        nodes.append(node)
        root.remove(node)
    '''for key in noderefs:
        print noderefs[key]'''
    ndrefs={}
    for way in root.findall('way'):
        for nd in way:
            ref = nd.get('ref')
            ndrefs[ref]=nd
        ways.append(way)
        root.remove(way)
    
    for key in noderefs:
        rnode=noderefs[key][0]
        rid=rnode.get('id')
        for node in noderefs[key][1:]:
            id1=node.get('id')
            ndrefs[id1].set('ref',rid)
            print node.get('id')
            nodes.remove(node)

    for node in nodes:
        root.append(node)
    for way in ways:
        root.append(way)
    #tree.write('output.osm')
    tree.write(outputosm)
