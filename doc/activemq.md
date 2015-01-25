ActiveMQ Study Note
====
ActiveMQ[^1] is a popular Message Server.
Get Started
----
* Install activemq
* Play with activemq
* How to download source code from activemq
* How to configure Eclipse to read source code
* How to debug activemq in Eclipse

Code Read
---
* Core Concepts
* Begin from main
* Code Structure

# Get Started

## Install
* ```brew install apache-activemq```
* ```activemq setup /Users/hankjohn/.activemqrc```
* ```activemq start```

## Play around
Open <http://localhost:8161/admin> to access the ActiveMQ Console
* username: admin
* password: admin

## Source Code
* Download ```git clone https://git-wip-us.apache.org/repos/asf/activemq.git``` 
* Install Maven ```brew install maven```
* Generate Eclipse config ```mvn eclipse:eclipse```

## Configure Eclipse
* Install Scalar
* Install M2Eclipse
* Import Projects

## Debug
* Enable remote debug 
* ```vi /usr/local/Cellar/activemq/5.10.0/libexec/bin/activemq```
* Uncomment this line, and set suspend=y
* ```#ACTIVEMQ_DEBUG_OPTS="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:trans    port=dt_socket,server=y,suspend=y,address=5005"```
* restart activemq
* ```activemq stop; activemq start```
* Open Eclipse -> Run -> Debug Configuration
* Double click Remote Java Application
* Set Port 5005, and the click Debug

# Code Read
## Structure
## Main
### Start
* activemq-console: org.apache.activemq.console.Main.main
* get tmpdir from -Djava.io.tmpdir from script
* parseExtensions [^2]

#### Get activemq config
* get activemq.conf file path from -Dactivemq.conf
* get activemq.base from -Dactivemq.base
* get activemq.home from -Dactivemq.home
* get activemq.data from -Dactivemq.data

#### Build classpath
* activemq.base/lib, camel, optional, web, extra
* activemq.classpath from -Dactivemq.classpath

#### enter runTaskClass
* print java version, jvm args, memory status
* sort jars to make sure the conflict class name are loaded the same version for each run.
* add jars to classLoader with 
```clsLoader = new ClassLoader(Urls[], clsLoader)```
* using reflection to run ```org.apache.activemq.console.command.ShellCommand```

#### ShellCommand
* create commands set with ServiceLoader.load(Command.class)[^3]
* loop commands to find the right commands to run

#### StartCommand.parseOption
* configURI = TODO

#### StartCommand.runTask
* configURI = xbean:activemq.xml(default)

##### BrokerService.createBroker
* create BrokerFactoryHandler accord to configURI.schema
* META-INF/services/org/apache/activemq/broker/xbean
* classLoader.getResourceAsStream
* Properties.load(streamReader)
* properties.getProperty("class")
* classLoader.loadClass(clsName)
* org.apache.activemq.xbean.XBeanBrokerFactory.createBroker

##### XBeanBrokerFactory
* ClassPathResource
* ApplicationContext.fromXML.getBean("broker")
* getBeanNamesForType(BrokerService)
* broker.setSprintBrokerContext

##### broker.start

##### broker.stop register
* Runtime.addShutdownHook

## Core Concept
### Broker
![Broker](http://hankjin.github.io/image/ActiveMQBroker.png)
[^1]: http://activemq.apache.org/
[^2]: Extension will not be discussed here, and there is no extension by default
[^3]: ServiceLoader using META-INF/service/xxx to register a list of implementation which can be loaded
