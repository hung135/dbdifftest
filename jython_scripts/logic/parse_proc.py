import sys
import re
#paternns

querypatterns = [" from (\w+.*?) where"
,"from (\w+)\)?"
,"exec (\w+) "
 ]
updatepatterns =[
    "update (\w+).*? set"
,"insert (\w+\.\.\w+) "
]
def remove_comments(dataString):
        #this replace HAS TO IN THESE ORDER
        x = dataString
        #single line comments
        x = re.sub( "--.*","",x)
        x = re.sub("/\*.*\*/","",x)
        x = re.sub("/\*.*\n.*\*/","",x)
         
        x = x.lower()
        x = re.sub("\n", " ", x)
        x = re.sub("/\*.*\*/","",x)
        x = re.sub("\t", " ", x)

        return x
 
def get_querys(dataString):
        x=dataString
        #for debugging
        product=[]
        total=[]
        for reg in querypatterns:
            #x = re.sub(reg," MATCH ",x)
            m = re.search(reg, x)
            #replace what we extracted so we don't do again
             
            if m: 
                total.append(  m.group(1))
            x = re.sub(reg, " ", x)
         
        return ','.join(total)
def get_updates(dataString):
        x=dataString
        #for debugging
        product=[]
        
        total=[]
        for reg in updatepatterns:
            #x = re.sub(reg," MATCH ",x)
            m = re.search(reg, x)
            if m:
                total.append(  m.group(1))

         
        product.append(["Update",total])
        return ','.join(total)
        #x = re.sub("\/\*.*?\*\/", " ", x)
 
 

      