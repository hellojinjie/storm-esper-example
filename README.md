## storm-esper-example

A simple demo to illustrate the integration of Storm and Esper.

### How To Run

#### 10 second log spout
This spout will continuous loop output 10 second log.   
The output of this spout is not accurate.  
Main class of this simulate is com.neulion.stream.TenSecondOnlineVisitorsMain  
Run this main class, you will get the output continuously  

#### one hour log spout
This spout will output one hour log.  
It is more accurate and real than 10 second log spout.  
Main class of this simulate is com.neulion.stream.OneHourOnlineVisitorsMain  
unzip the data file in data directory: drcSimulator.1.2013-01-07 00.7z  
rename config.properties-template to config.properties, make sure parameter onehourlogfile has the right value  
if you are a developer, do not forget to add config.properties to svn/git ignore list  
Run this main class, you will get the output continuously  