package challengers.findog.src.board.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

@Data
public class Board {
    @ApiModelProperty(value = "postId", example = "1", required = true)
    private int postId;
    @ApiModelProperty(value = "userId", example = "1", required = true)
    private int userId;
    @ApiModelProperty(value = "유저 닉네임", example = "abcde", required = true)
    private String nickname;
    @ApiModelProperty(value = "유저 프로필 이미지", required = true)
    private String userImgUrl;
    @ApiModelProperty(value = "게시글 제목", example = "제목입니다.", required = true)
    private String title;
    @ApiModelProperty(value = "게시글 카테고리", example = "1", required = true)
    private Integer category;
    @ApiModelProperty(value = "지역", example = "1", required = true)
    private Integer region;
    @ApiModelProperty(value = "게시글 썸네일", example = "첫번째 이미지")
    private String thumbnail;
    @ApiModelProperty(value = "게시글 내용", example = "내용입니다.", required = true)
    private String content;
    @ApiModelProperty(hidden = true)
    private String postCreateAt;
    private Integer likeCount;
    private Integer commentCount;
    private int hits;

    public Board(){}

    public Board(int postId, int userId, String nickname, String userImgUrl, String title, int category, int region, String thumbnail, String content,
                 String postCreateAt, int likeCount, int commentCount, int hits){
        this.postId = postId;
        this.userId = userId;
        this.nickname = nickname;
        this.userImgUrl = userImgUrl;
        this.title = title;
        this.category = category;
        this.region = region;
        this.thumbnail = thumbnail;
        this.content = content;
        this.postCreateAt = postCreateAt;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.hits = hits;
    }
}
