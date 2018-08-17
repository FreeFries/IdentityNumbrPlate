package xander.beans;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xander.engines.filehandling.FileEngine;


@Configuration
public class SpringContainerDI {

    @Bean
    public IFileEngine provideFileEngine() throws Exception {

        FileEngine fe = new FileEngine("./data" );

        return fe ;
    }

 }
