package cn.wolfcode.shop;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.Resource;
import java.io.IOException;

@SpringBootApplication
@MapperScan("cn.wolfcode.shop.mapper")
@PropertySource({"redis.properties","zookeeper.properties"})
public class GoodsApp {
    public static void main(String[] args) throws IOException {
        SpringApplication.run(GoodsApp.class,args);
        System.in.read();
    }
}
