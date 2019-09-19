DATE=$(eval date -I)
GITB=$(git symbolic-ref --short HEAD)
# Instructions
cd /workspace/
rm -f dbdiff.tar
tar -cvf dbdiff.tar jython_scripts/ ${JAREXPORT}
python3 ${GITHUBRELEASE} -k ${GITHUBKEY} -r ${REPO} -c ${GITB} -m ${DATE} -a /workspace/dbdiff.tar