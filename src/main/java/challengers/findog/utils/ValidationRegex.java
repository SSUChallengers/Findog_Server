package challengers.findog.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static challengers.findog.config.Constant.nicknameRegex;

public class ValidationRegex {
    // 이메일 형식 체크
   public static boolean isRegexEmail(String target) {
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    public static boolean isRegexImage(String target){ //이미지 확장자 검사
       String regex = "(.*?)\\.(jpg|jpeg|png|bmp)$";
       Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    public static boolean isRegexNickname(String target){ //닉네임 검사
        Pattern pattern = Pattern.compile(nicknameRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }
}
