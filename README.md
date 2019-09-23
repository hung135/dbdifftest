
RunTime Installation:
- Download the jython 2.7 runtime:
-- https://www.jython.org/jython-old-sites/downloads.html
-- create a batch file with:
-- java -jar jython.jar and make sure the batch file is in your path
-- clone this project


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
