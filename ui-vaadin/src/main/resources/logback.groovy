import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.FileAppender
import ch.qos.logback.core.ConsoleAppender

import static ch.qos.logback.classic.Level.ALL
import static ch.qos.logback.classic.Level.DEBUG
import static ch.qos.logback.classic.Level.ERROR
import static ch.qos.logback.classic.Level.WARN
import static ch.qos.logback.classic.Level.INFO

appender("CONSOLE", ConsoleAppender) {
	encoder(PatternLayoutEncoder) { pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n" }
}

String path = null
if (System.getProperty("catalina.base") != null) {
	// tomcat
	path = System.getProperty("catalina.base") + "/logs";
} else if (System.getProperty("jetty.logs") != null) {
	// jetty
	path = System.getProperty("jetty.logs");
} else {
	// default
	path = System.getProperty("java.io.tmpdir");
}
if (path != null) {
	appender("FILE", FileAppender) {
		file = path + "/altes-leipzig.log"
		append = true
		encoder(PatternLayoutEncoder) { pattern = "%date %level [%thread] %logger{10} [%file:%line] %msg%n" }
	}
}


logger("org.springframework", WARN, ["CONSOLE", "FILE"])
logger("org.springframework.transaction", WARN, ["CONSOLE", "FILE"])
logger("org.springframework.security", WARN, ["CONSOLE", "FILE"])
logger("org.springframework.data.neo4j", WARN, ["CONSOLE", "FILE"])
logger("org.neo4j", WARN, ["CONSOLE", "FILE"])
logger("com.mymita.al", INFO, ["CONSOLE", "FILE"])
logger("com.mymita.al.importer", INFO, ["CONSOLE", "FILE"])
logger("com.mymita.al.ui.admin.AdminUI", WARN, ["CONSOLE", "FILE"])
logger("com.mymita.al.ui.search.MainUI", WARN, ["CONSOLE", "FILE"])

root(ERROR, ["CONSOLE", "FILE"])

scan("5 seconds")