DATE=$(eval date -I)
GITB=$(git symbolic-ref --short HEAD)
# Instructions
cd /workspace/
rm -f dbdiff.zip
cd /workspace/jython_scripts
zip -r /workspace/dbdiff.zip .
cd /workspace/
zip -ur dbdiff.zip ${JAREXPORT}
python3 ${GITHUBRELEASE} -v -k ${GITHUBKEY} -r ${REPO} -c ${GITB} -m ${DATE} -a /workspace/dbdiff.zip