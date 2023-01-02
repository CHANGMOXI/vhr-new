package org.changmoxi.vhr;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//配置mapper扫描，包里的所有接口在编译之后都会生成相应的代理实现类，这样就不用在每个接口类加上@Mapper注解
@MapperScan(basePackages = "ogr.changmoxi.vhr.mapper")
public class VhrNewApplication {

    public static void main(String[] args) {
        SpringApplication.run(VhrNewApplication.class, args);
    }

}
