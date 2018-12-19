package com.refinitiv.ejv.mort2cdf;

import com.sun.tools.javac.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.lang.invoke.MethodHandles;

/**
 *
 * Maven config:
 *
 * <dependencies>
 *         <dependency>
 *             <groupId>org.slf4j</groupId>
 *             <artifactId>slf4j-api</artifactId>
 *             <version>1.7.25</version>
 *         </dependency>
 *         <dependency>
 *             <groupId>ch.qos.logback</groupId>
 *             <artifactId>logback-classic</artifactId>
 *             <version>1.2.3</version>
 *         </dependency>
 * </dependencies>
 *
 * resources/logback.xml
 *
 * <?xml version="1.0" encoding="UTF-8"?>
 * <configuration>
 *
 *     <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
 *         <layout class="ch.qos.logback.classic.PatternLayout">
 *             <Pattern>%d{YYYY-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</Pattern>
 *         </layout>
 *     </appender>
 *     <root level="debug">
 *         <appender-ref ref="STDOUT"/>
 *     </root>
 *
 *     <logger name="com.refinitiv" level="ERROR"/>
 *
 * </configuration>
 *
 *
 */

public class LoggerTestMain {
    final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String... arg) {
        logger.info("Hello World!");
        logger.debug("Hello World!");
        logger.debug("Set \\{} differs from {}", "3");
        logger.debug("Set { 1, 2 } differs from {}", "3");

        List<String> entry = List.of("A", "B");
        //The following two lines will yield the exact same output.
        //However, the second form will outperform the first form by a factor of at least 30, in case of a disabled logging statement.
        logger.debug("The new entry is {}. Do this. ", entry);
        logger.debug("The new entry is " + entry + ". Do not do this.");


        String s = "Hello world";
        try {
            Integer i = Integer.valueOf(s);
        } catch (NumberFormatException e) {
            logger.error("Failed to format {}", s, e);
        }

        Marker fatal = MarkerFactory.getMarker("FATAL");

        try {
            Integer i = Integer.valueOf(s);
        } catch (NumberFormatException e) {
            logger.error(fatal, "Failed to forma {}", s, e);
        }

    }

}
