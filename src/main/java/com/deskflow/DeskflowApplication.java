package com.deskflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application entry point. @SpringBootApplication triggers component scanning
 * over com.deskflow.* and boots the embedded Tomcat server.
 */
@SpringBootApplication
public class DeskflowApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeskflowApplication.class, args);
    }
}
