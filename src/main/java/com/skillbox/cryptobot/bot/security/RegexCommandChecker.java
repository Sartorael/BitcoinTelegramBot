package com.skillbox.cryptobot.bot.security;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Service
@Slf4j
public class RegexCommandChecker {

    public boolean subscribeMethodChecker(String chechkableString){
        Pattern pattern = Pattern.compile("/subscribe"+ " "+ "\\d+(\\.\\d+)?");
        Matcher matcher = pattern.matcher(chechkableString);
        return matcher.matches();
    }
}
