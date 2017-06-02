import fileinput
import re
import pickle


j = 1
pg = []
p = re.compile(r'<page>[\s\S]*</id>')
p1 = re.compile(r'\d+')
text = ''
flag = 0
for line in fileinput.input('enwiki-4'):
		ln = line.strip(" \r\n")
		if ln=='<page>':
                        flag = 1
			f = open("Dump4/pages"+str(j),"w")
		if ln=='</page>':
			flag = 0
			f.write(line)
			f.close()
			print j
			j=j+1
		if flag !=1:
			continue
		else:
			f.write(ln)
			f.write("\n")
		
