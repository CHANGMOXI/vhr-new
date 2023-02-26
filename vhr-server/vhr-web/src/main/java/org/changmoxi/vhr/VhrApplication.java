package org.changmoxi.vhr;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @author CZS
 * @create 2023-02-05 16:48
 **/
@SpringBootApplication
// 配置mapper扫描，包里的所有接口在编译之后都会生成相应的代理实现类，这样就不用在每个接口类加上@Mapper注解
@MapperScan(basePackages = "org.changmoxi.vhr.mapper")
// 开启基于注解的缓存
@EnableCaching
public class VhrApplication {
    public static void main(String[] args) {
        SpringApplication.run(VhrApplication.class, args);
    }
}
