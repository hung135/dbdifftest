<a href="https://scan.coverity.com/projects/hung135-dbdifftest">
  <img alt="Coverity Scan Build Status"
       src="https://scan.coverity.com/projects/19285/badge.svg"/>
</a>

RunTime Installation:
- Download the jython 2.7 runtime:
  - http://search.maven.org/remotecontent?filepath=org/python/jython-installer/2.7.1/jython-installer-2.7.1.jar
  - Follow Instrution install the downloaded file:
    - https://www.jython.org/installation
  - Set your environment PATH variable to:
    - C:\xxxxx\yyyyy\jython\bin
  - Get latest Release of this Project and Unzip it:
    - https://github.com/hung135/dbdifftest/releases
  - create 2 batch files to make your life easier:
    - runtasks.bat
       - jython dbdiff.py -y ../conn.yaml -t ../tasks.yaml
    - runreports.bat 
      - jython dbdiff.py -y ../conn.yaml -t ../reports.yaml

What Can I put in tasks & reports.yaml:
  - Every Function in this file can be configured in your tasks.yaml & reports.yaml
    - https://github.com/hung135/dbdifftest/blob/master/jython_scripts/logic/migration.py
  - Same Connection yaml (conn.yaml) file is here :
    - https://github.com/hung135/dbdifftest/blob/master/jython_scripts/conn.yaml

Logging:
   - Setup custom logging by following [this](https://www.tutorialspoint.com/log4j/log4j_configuration.htm)

Note:
- You can call the yaml files and have as many as you. 
- Understand one has your connection info and one has your execution parameters
- This framework will look up the connection to run based on what parameters you configure in your task yaml file

Description:
- Generic Maven Project to Connect to 2 different Database via JDBC.
- Queryes Tables, Views, Fucntions and reports differences found
- This Project w/ VSCODE + Docker will Spin Up Local Instances:
  - Oracle 12.2.0c
  - Sybase ASE 15.7

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
- Added `deletereleases` alias
- Updated `package` and `releasepackage` to support zip files
- Added the ability to export the MD5 has in sybase
- Added `TableInformation`
- Added logging with `-v <debug, warn, all>`
