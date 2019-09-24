
RunTime Installation:
- Download the jython 2.7 runtime:
- - http://search.maven.org/remotecontent?filepath=org/python/jython-installer/2.7.1/jython-installer-2.7.1.jar
- - Follow Instrution install the downloaded file:
- - - https://www.jython.org/installation
- - Set your environment PATH variable to:
- - - C:\xxxxx\yyyyy\jython\bin
- - Get latest Release of this Project and Unzip it:
- - - https://github.com/hung135/dbdifftest/releases
- - create 2 batch files to make your life easier:
- - - runtasks.bat
- - - - jython dbdiff.py -y ../conn.yaml -t ../tasks.yaml
- - - runreports.bat 
- - - - jython dbdiff.py -y ../conn.yaml -t ../reports.yaml

Description:
- Generic Maven Project to Connect to 2 different Database via JDBC.
- Queryes Tables, Views, Fucntions and reports differences found
- This Project w/ VSCODE + Docker will Spin Up Local Instances:
- - Oracle 12.2.0c
- - Sybase ASE 15.7

Requirements:
- Ensure your github developer key is in your `~/.gitkey` file as it will be mounted for managing releases
- VSCODE with VSCODE remote development extension

Features:
- `releasepackage` alias that will build, package, and publish the `tar` file to it.
- Reads `YAML` config and creates DB connections based on the configuration of the YAML file

Changelog (no order):
- Initial Java functions and project structure added
- Added Jython
- Added `releasepackage` alias
- Added YAML reader Jython interaction with JAVA's snakeyaml
- Added a directory YAML reader
- Updated dbdiff to hold mulitple connections
- Added `-y` arg for execution
- Added `-t` arg for execution
- Added reflection to find the correct `logic` class instance
- Updated `task` object to reflect the new changes
- Added a `CSV` out
