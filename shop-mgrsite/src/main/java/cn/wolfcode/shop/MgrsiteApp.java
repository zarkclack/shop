package cn.wolfcode.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;

@SpringBootApplication
@PropertySource({"zookeeper.properties"})
public class MgrsiteApp {
    public static void main(String[] args) throws IOException {
        SpringApplication.run(MgrsiteApp.class,args);
        //System.in.read();
    }
}
