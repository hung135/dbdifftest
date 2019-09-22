import sys
import re
#paternns

querypatterns = ["from (\w+.*?) where"
 
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
            if m:
                total.append(  m.group(1))
        
        
        return total
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
        return total
        #x = re.sub("\/\*.*?\*\/", " ", x)
def filter_junk(dataString):
    filter_list=[
        ' insert into',
        ' insert in'
    ]
    total = []
    for a in dataString:
        if a not in filter_list:
            total.append(a)
    return total
def get_sybase_insert(dataString):
    total=[]
    x = remove_comments(dataString).lower()
    tmp = re.findall(" insert \w+", x)
    if len(tmp)>0: 
        total.append(tmp)
    #   tmp = re.findall(" insert into \w+", x)
    #   if len(tmp)>0: 
    #     total.append(tmp)
    return total

      