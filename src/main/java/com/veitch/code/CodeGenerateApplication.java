package com.veitch.code;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CodeGenerateApplication implements CommandLineRunner {

    private final static Logger logger = LoggerFactory.getLogger(CodeGenerateApplication.class);

    public static void main(String[] args) throws Exception {
        logger.debug("CodeGenerateApplication is STARTing...");
        SpringApplication.run(CodeGenerateApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {

    }
}
