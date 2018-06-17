package cn.wolfcode.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;

@SpringBootApplication
@PropertySource({"redis.properties","zookeeper.properties","acivemq.properties"})
public class MobileApp {
    public static void main(String[] args) throws IOException {
        SpringApplication.run(MobileApp.class,args);
        //System.in.read();
    }
}
