package org.changmoxi.vhr.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.changmoxi.vhr.common.RespBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author CZS
 * @create 2023-01-02 18:06
 **/
@RestController
public class LoginController {
    @Resource
    private DefaultKaptcha defaultKaptcha;

    @GetMapping("/login")
    public RespBean login() {
        return RespBean.error("尚未登录，请登录!");
    }

    /**
     * 生成验证码图片
     *
     * @param session
     * @param response
     * @throws IOException
     */
    @GetMapping("/verification_code")
    public void verificationCode(HttpSession session, HttpServletResponse response) throws IOException {
        // 生成验证码文本和相应的验证码图片
        String text = defaultKaptcha.createText();
        BufferedImage image = defaultKaptcha.createImage(text);
        // 验证码文本存到session
        session.setAttribute("verification_code", text);
        // 把图片写出去显示，使用try-with-resources可以自动关闭流
        try (ServletOutputStream out = response.getOutputStream()) {
            ImageIO.write(image, "jpg", out);
        }
    }
}