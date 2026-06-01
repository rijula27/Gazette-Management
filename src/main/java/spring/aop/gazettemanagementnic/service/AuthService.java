package spring.aop.gazettemanagementnic.service;

import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public String generateCaptcha() {

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        StringBuilder captcha = new StringBuilder();

        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            captcha.append(
                    chars.charAt(
                            random.nextInt(chars.length())));
        }

        return captcha.toString();
    }


    
}
