Logback more appenders
==================================================
You can logging to Fluentd with the logback appender.

## Appenders
- [fluentd](http://fluentd.org/),modified the [logback-more-appenders](https://github.com/sndyuk/logback-more-appenders)
    - depend on [fluent-logger for Java](https://github.com/fluent/fluent-logger-java).
     - Install fluentd before running logger.

## Installing

### Install jars from Maven2 repository
```
		<dependency>
			<groupId>ch.qos.logback</groupId>
			<artifactId>logback-classic</artifactId>
			<version>1.1.7</version>
		</dependency>
		<dependency>
			<groupId>ch.qos.logback</groupId>
			<artifactId>logback-core</artifactId>
			<version>1.1.7</version>
		</dependency>
		<dependency>
			<groupId>com.firewarm</groupId>
			<artifactId>fluentd-logback-appender</artifactId>
			<version>0.0.1-SNAPSHOT</version>
		</dependency>
		<dependency>
			<groupId>org.fluentd</groupId>
			<artifactId>fluent-logger</artifactId>
			<version>${fluentd.logger.version}</version>
			<optional>true</optional>
		</dependency>
```

### Configure your logback.xml
You can find configuration files example here:

- [logback-appenders.xml](https://github.com/lightingLYG/fluentd-logback-appender/blob/master/src/main/resources/logback-appenders.xml)
- [logback.xml](https://github.com/lightingLYG/fluentd-logback-appender/blob/master/src/main/resources/logback.xml)

### License
[Apache License, Version 2.0](LICENSE)