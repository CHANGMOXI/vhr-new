package org.changmoxi.vhr.config;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * 配置验证码库Kaptcha的Bean
 *
 * @author CZS
 * @create 2023-02-17 11:48
 **/
@Configuration
public class KaptchaConfig {
    @Bean
    public DefaultKaptcha getDefaultKaptcha() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        // 是否有图片边框，默认为yes，合法值: yes、no
        properties.setProperty(Constants.KAPTCHA_BORDER, "yes");
        // 边框颜色，默认为black，合法值: r,g,b 或 white、black、blue等
        properties.setProperty(Constants.KAPTCHA_BORDER_COLOR, "black");
        // 验证码图片宽度，默认为200
        properties.setProperty(Constants.KAPTCHA_IMAGE_WIDTH, "150");
        // 验证码图片高度，默认为50
        properties.setProperty(Constants.KAPTCHA_IMAGE_HEIGHT, "50");
        // 验证码字符库
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_STRING, "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        // 验证码文本字符长度，默认为5
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_CHAR_LENGTH, "4");
        // 验证码文本字体样式，默认为Arial,Courier
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_NAMES, "宋体,楷体,隶书,微软雅黑");
        // 验证码文本字符颜色，默认为black，合法值: r,g,b 或 white、black、blue等
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_COLOR, "black");
        // 验证码文本字符大小，默认为40
        properties.setProperty(Constants.KAPTCHA_TEXTPRODUCER_FONT_SIZE, "40");
        // 图片样式，水纹com.google.code.kaptcha.impl.WaterRipple 鱼眼com.google.code.kaptcha.impl.FishEyeGimpy 阴影com.google.code.kaptcha.impl.ShadowGimpy
        properties.setProperty(Constants.KAPTCHA_OBSCURIFICATOR_IMPL, "com.google.code.kaptcha.impl.WaterRipple");
        // session key
        // 自动将生成的验证码文本存入到session中，并且key为自己设置的值，比如 kaptchaCode
        // 只配置这个并不会将验证码文本自动存入session，因为实际上Kaptcha工具自己提供了一个生成验证码图片的Servlet
        // 如果想这个配置生效，还需要使用它提供的Servlet，就要另外配置它提供的Servlet
        // 同时验证码内容比较丰富不仅仅是图片的话，直接配置它的Servlet不能满足需求，需要自己写验证码的接口，在里面把验证码信息存入session
        // 所以这里不开启这个配置，自己在验证码接口存入session
//        properties.setProperty(Constants.KAPTCHA_SESSION_CONFIG_KEY, "kaptchaCode");

        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}