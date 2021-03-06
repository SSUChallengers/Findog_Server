package challengers.findog.src.user;

import challengers.findog.config.BaseException;
import challengers.findog.config.BaseResponse;
import challengers.findog.config.BaseResponseStatus;
import challengers.findog.config.secret.Secret;
import challengers.findog.src.user.model.*;
import challengers.findog.utils.AES128;
import challengers.findog.utils.JwtService;
import challengers.findog.utils.s3Component.FileControlService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static challengers.findog.config.BaseResponseStatus.*;
import static challengers.findog.utils.ValidationRegex.isRegexImage;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final FileControlService fileControlService;
    private final JwtService jwtService;

    //회원가입
    @Transactional(rollbackFor = Exception.class)
    public PostSignUpRes createUser(PostSignUpReq postSignUpReq) throws BaseException {
        if(userRepository.checkEmail(postSignUpReq.getEmail()) == 1){
            throw new BaseException(DUPLICATED_EMAIL);
        }

        if(userRepository.checkNickname(postSignUpReq.getNickname()) == 1){
            throw new BaseException(DUPLICATED_NICKNAME);
        }

        User user;
        try{
            String pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postSignUpReq.getPassword());
            user = new User(postSignUpReq.getEmail(), postSignUpReq.getNickname(), pwd, postSignUpReq.getPhoneNum(), null);
        } catch (Exception e){
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }

        if(postSignUpReq.getProfileImg() != null && !postSignUpReq.getProfileImg().isEmpty()){
            if(!isRegexImage(postSignUpReq.getProfileImg().getOriginalFilename())){
                throw new BaseException(INVALID_IMAGEFILEEXTENTION);
            }
            String imgUrl = fileControlService.uploadImage(postSignUpReq.getProfileImg()); //return값이 url
            user.setProfileUrl(imgUrl);
        }

        try{
            int userId = userRepository.createUser(user);
            String userJwt = jwtService.createJwt(userId);
            return new PostSignUpRes(userId, userJwt);
        } catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //닉네임 중복 검사
    public int checkNickname(String nickname) throws BaseException{
        try{
             return userRepository.checkNickname(nickname);
        } catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //이메일 중복 검사
    public int checkEmail(String email) throws BaseException{
        try{
            return userRepository.checkEmail(email);
        } catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //회원탈퇴
    public String leaveUser(int userid, PatchLeaveReq patchLeaveReq) throws BaseException{
        User user;
        String pwd;
        try{
            user = userRepository.getUser(userid);
        } catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }

        try{
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(user.getPassword());
        } catch (Exception e){
            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
        }

        if(!pwd.equals(patchLeaveReq.getPassword())){
            throw new BaseException(DIFFERENT_PASSWORD);
        }

        try{
            if(userRepository.leaveUser(userid) == 0){
                throw new BaseException(FAIL_LEAVEUSER);
            }
        } catch (Exception e){
            throw new BaseException(FAIL_LEAVEUSER);
        }

        return "회원 탈퇴가 성공적으로 완료되었습니다.";
    }

    //로그인
    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException {
        //이메일 확인
        User user;
        String pwd;
        if(userRepository.checkEmail(postLoginReq.getEmail()) == 0){
            throw new BaseException(NOT_EXISTS_USER);
        }

        try {
            user = userRepository.getUserByEmail(postLoginReq.getEmail());
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

        try{
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(user.getPassword());
        } catch (Exception e){
            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
        }

        if(!pwd.equals(postLoginReq.getPassword())){
            throw new BaseException(NOT_EXISTS_USER);
        }

        return new PostLoginRes(user.getUserId(), jwtService.createJwt(user.getUserId()), user.getProfileUrl());
    }

    //자동 로그인
    public PostLoginRes autoLogIn(int userId, String jwt) throws BaseException{
        try{
            User user = userRepository.getUser(userId);
            return new PostLoginRes(user.getUserId(), jwt, user.getProfileUrl());
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //유저 정보 조회
    public GetUserRes getUserInfo(int userId) throws BaseException{
        try{
            User user = userRepository.getUser(userId);
            return GetUserRes.from(user);
        } catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
