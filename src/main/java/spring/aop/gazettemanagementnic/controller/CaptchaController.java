package spring.aop.gazettemanagementnic.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

@Controller
public class CaptchaController {

    @Autowired
    private DefaultKaptcha captchaProducer;

    // Endpoint to generate and serve the CAPTCHA image
    @GetMapping("/captcha-image")
    public void getCaptchaImage(HttpSession session, HttpServletResponse response) throws Exception {
        response.setContentType("image/png");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        String captchaText = captchaProducer.createText();
        session.setAttribute("captcha", captchaText);

        BufferedImage captchaImage = captchaProducer.createImage(captchaText);

        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(captchaImage, "png", out);
        try {
            out.flush();
        } finally {
            out.close();
        }
    }
}
