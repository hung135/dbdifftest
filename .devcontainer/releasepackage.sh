DATE=$(eval date -I)
GITB=$(git symbolic-ref --short HEAD)
# Instructions
cd /workspace/
rm -f dbdiff.tar
cd /workspace/jython_scripts
tar -cvf /workspace/dbdiff.tar . 
cd /workspace/
tar -rvf dbdiff.tar ${JAREXPORT}
python3 ${GITHUBRELEASE} -v -k ${GITHUBKEY} -r ${REPO} -c ${GITB} -m ${DATE} -a /workspace/dbdiff.tar