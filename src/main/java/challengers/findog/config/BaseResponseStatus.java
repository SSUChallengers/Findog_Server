package challengers.findog.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false, 2003, "권한이 없는 유저의 접근입니다."),

    INVALID_IMAGEURL(false, 2004, "올바르지 않은 이미지 형식입니다."),
    INVALID_PAGE(false, 2005, "존재하지 않는 페이지 입니다."),

    // users/sign-up
    EMPTY_EMAIL(false, 2100, "이메일을 입력해주세요."),
    INVALID_EMAIL(false, 2101, "올바르지 않은 이메일 형식입니다."),
    EMPTY_NICKNAME(false, 2102, "닉네임을 입력해주세요."),
    INVALID_NICKNAME(false, 2103, "올바르지 않은 닉네임 형식입니다."),
    EMPTY_PASSWORD(false, 2104, "비밀번호를 입력해주세요."),
    INVALID_PASSWORD(false, 2105, "올바르지 않은 비밀번호 형식입니다."),
    EMPTY_PHONENUMBER(false, 2106, "핸드폰 번호를 입력해주세요."),
    INVALID_PHONENUMBER(false, 2107, "올바르지 않은 핸드폰 번호 형식입니다."),
    INVALID_IMAGEFILEEXTENTION(false, 2108, "올바르지 않은 이미지 파일 형식입니다."),

    //users/leave
    EMPTY_USERID(false, 2110, "userId를 입력해주세요."),
    DIFFERENT_PASSWORD(false, 2109, "틀린 비밀번호입니다."),

    //comment
    INVALID_POSTID(false, 2110, "올바르지 않은 postId입니다."),
    EMPTY_COMMENT(false, 2111, "댓글 내용을 입력해주세요."),


    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),
    NOT_EXISTS_WRITING(false, 3001, "존재하지 않는 게시물입니다."),

    // users/sign-up
    DUPLICATED_EMAIL(false, 3100, "중복된 이메일입니다."),
    DUPLICATED_NICKNAME(false, 3101, "중복된 닉네임입니다."),

    //users/leave
    FAIL_LEAVEUSER(false, 3102, "회원탈퇴에 실패하였습니다."),

    //user/log-in
    NOT_EXISTS_USER(false, 3103, "일치하는 회원정보가 없습니다."),

    //comment
    FAIL_MODIFY_COMMENT(false, 3104, "댓글 수정에 실패하였습니다."),
    FAIL_DELETE_COMMENT(false, 3105, "댓글 삭제에 실패하였습니다."),

    //mypage/myInfo/nickname
    FAIL_MODIFY_NICKNAME(false, 3106, "닉네임 수정에 실패하였습니다."),

    //mypage/myInfo/phoneNum
    FAIL_MODIFY_PHONENUM(false, 3107, "연락처 수정에 실패하였습니다."),

    //mypage/myInfo/password
    FAIL_MODIFY_PASSWORD(false, 3108, "비밀번호 수정에 실패하였습니다."),

    //mypage/myInfo/password
    FAIL_DELETE_PROFILEIMG(false, 3109, "프로필 사진 삭제에 실패하였습니다."),
    FAIL_MODIFY_PROFILEIMG(false, 3110, "프로필 사진 수정에 실패하였습니다."),

    //users/:userId
    INVALID_USERID(false, 3111, "유저 정보가 올바르지 않습니다."),

    //animal
    FAIL_UPLOAD_LIKEANIMAL(false, 3112, "유기동물 공고 관심 등록에 실패하였습니다."),
    DUPLICATED_LIKEANUMAL(false, 3113, "이미 관심 등록된 유기동물 공고입니다."),
    FAIL_DELETE_LIKEANIMAL(false, 3114, "유기동물 공고 관심 삭제를 실패하였습니다."),

    //mypage/{userIdx}
    FAIL_UPDATE_USER_INFO(false, 3200, "회원정보 수정에 실패하였습니다."),

    //boards
    FAIL_UPLOAD_IMAGES(false, 3201, "사진 업로드에 실패하였습니다."),
    FAIL_UPDATE_BOARD(false, 3202, "게시글 수정에 실패하였습니다."),
    FAIL_DELETE_IMAGES(false, 3203, "이미지 삭제에 실패하였습니다."),
    FAIL_DELETE_BOARD(false, 3204, "존재하지 않는 게시글입니다."),
    FAIL_GET_BOARD_IMAGE(false, 3205, "해당 게시글 사진 조회에 실패하였습니다."),
    FAIL_GET_BOARD_COMMENTLIST(false, 3206, "해당 게시글 댓글 조회에 실패하였습니다."),
    EMPTY_TITLE(false, 3207, "제목을 입력해주세요."),
    EMPTY_CATEGORY(false, 3208, "카테고리를 선택해주세요."),
    EMPTY_CONTENT(false, 3209, "내용을 입력해주세요."),
    FAIL_DELETE_COMMENTS(false, 3210, "해당 게시글의 댓글들 삭제에 실패하였습니다."),
    EMPTY_REGION(false, 3211, "지역을 입력해주세요."),


    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다.");




    private final boolean isSuccess;
    private final int code;
    private final String message;

    BaseResponseStatus(boolean isSuccess, int code, String message) { //BaseResponseStatus 에서 각 해당하는 코드를 생성자로 맵핑
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }

    public static BaseResponseStatus of(final String errorName){
        return BaseResponseStatus.valueOf(errorName);
    }
}
