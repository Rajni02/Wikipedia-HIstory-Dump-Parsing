# This is the Master Code 
#This code reads the pages and makes a ordered dictionary and computes the links sequence


import re
import xml.etree.ElementTree as etree 
import time
import pickle
from collections import defaultdict
import subprocess
import time
from collections import OrderedDict
pid = 1263
pg = OrderedDict()
count = 0
pat1 = re.compile(ur'\[\[\w+[|\s\w]*\]\]')  # All Links
pat2 = re.compile(r"\w+[\w\s]*")    # All links with links withou pipe
pat3 = re.compile(r"\[\[(\w+[\w\s]*)\]\] | \[\[(\w+[\w\s]*)[|\w\s]*\]\]")
def hms_string(sec_elapsed):
    h = int(sec_elapsed / (60 * 60))
    m = int((sec_elapsed % (60 * 60)) / 60)
    s = sec_elapsed % 60
    return "{}:{:>02}:{:>05.2f}".format(h, m, s)


def convert_time(tm):
                  t = str(tm.strip("Z").split("T")[0])+" "+str(tm.strip("Z").split("T")[1]) # Convert to preferable format
                  t = time.mktime(time.strptime(t,"%Y-%m-%d %H:%M:%S")) #Convert to Epoch Time
                  return float(t)

f = open("mylog2.txt","w")
st  =time.time()
for i in range(1263,1567,1):
   print i
   try: 
	tree = etree.parse('Dump2/pages'+str(i))
	root = tree.getroot()
	ns = root.find('ns').text
	tit= root.find('title').text
	if int(ns)!=0:
		continue
	else:
		curr = 0
		prev = 0
		revc = 0
	        linc = 0
		start  = time.time()
		for rev in root.findall('revision'):
			revc = revc + 1
			lin_sq = defaultdict(dict)	
			id = int(rev.find('id').text)
			pg[id]={}
			if rev.find('parentid') != None:
				prid = int(rev.find('parentid').text)
			else:
				prid=-1
			txt = rev.find('text').text
			curr = convert_time(rev.find('timestamp').text)
			if txt == None:
				continue
			else:
				txt = txt.encode('ascii','ignore')
				txt = "".join(c for c in txt if c not in ('.',';','\'','*',',','\"'))
				txt = txt.split("\n\n")
			#print root.find('title').text,id,prid,len(txt),curr 
			if prev<curr:
				if prid>0:
					pg[id]['parentid']=prid
					pg[id]['time']=curr
					pg[id]['text']=txt
					for tx1 in range(len(txt)):
						l = re.findall(pat3,txt[tx1])
						linc = linc+len(l)
						for x in range(len(l)):
							lin_sq[tx1][x] = l[x][0]+l[x][1]						
					pg[id]['link-seq']=lin_sq
						
				prev = curr
			else:
				count=count+1

		pickle.dump(pg,open("pages_"+str(pid)+".p","wb"))
		pid = pid+1
		pg.clear()			
		elap = time.time()-start
		#print hms_string(elap),revc,linc
		f.write(tit.encode('ascii','ignore')+"	"+str(hms_string(elap))+"	"+str(revc)+"	"+str(linc)+"\n")
   except:
	pass


elaps = time.time()-st
f.write(str(hms_string(elaps)))
f.close()	      	
