package challengers.findog.src.mypage;

import challengers.findog.src.board.model.Board;
import challengers.findog.src.mypage.model.PatchNicknameReq;
import challengers.findog.src.mypage.model.PatchPasswordReq;
import challengers.findog.src.mypage.model.PatchPhoneNumReq;
import challengers.findog.src.mypage.model.PatchProfileImgReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class MypageRepository {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //닉네임 수정
    public int modifyNickname(PatchNicknameReq patchNicknameReq, int userId){
        String query = "update User set nickname = ? where userId = ?";
        Object[] params = new Object[]{patchNicknameReq.getNickname(), userId};
        return jdbcTemplate.update(query, params);
    }

    //핸드폰 번호 수정
    public int modifyPhoneNum(PatchPhoneNumReq patchPhoneNumReq, int userId){
        String query = "update User set phoneNum = ? where userId = ?";
        Object[] params = new Object[]{patchPhoneNumReq.getPhoneNum(), userId};
        return jdbcTemplate.update(query, params);
    }

    //비밀번호 수정
    public int modifyPassword(PatchPasswordReq patchPasswordReq, int userId){
        String query = "update User set password = ? where userId = ?";
        Object[] params = new Object[]{patchPasswordReq.getNewPassword(), userId};
        return jdbcTemplate.update(query, params);
    }

    //프로필 이미지 삭제
    public int deleteProfileImg(int userId){
        String query = "update User set profileUrl = null where userId = ?";
        return jdbcTemplate.update(query, userId);
    }

    //프로필 이미지 수정
    public int modifyProfileImg(String profileImgUrl, int userId){
        String query = "update User set profileUrl = ? where userId = ?";
        Object[] params = new Object[]{profileImgUrl, userId};
        return jdbcTemplate.update(query, params);
    }

    //내가 작성한 글 조회
    public List<Board> getMyWriteBoardList(int userId, int page, int size) {
        String query = "select P.postId, P.userId, nickname, U.profileUrl, title, category, region, thumbnail, P.content, postCreateAt, likeCount, commentCount, hits\n" +
                "from Post P left join User U on P.userId = U.userId\n" +
                "            left join (SELECT postId, Count(commentId) as commentCount FROM Comment GROUP BY postId) C on C.postId = P.postId\n" +
                "            left join (SELECT postId, imgUrl as thumbnail FROM Image GROUP BY postId) I on I.postId = P.postId\n" +
                "            left join (SELECT postId, Count(likeId) as likeCount FROM `Like` GROUP BY postId) L on L.postId = P.postId\n" +
                "where P.userId = ?\n" +
                "order by postId desc, postCreateAt desc\n" +
                "limit ? offset ?";
        return jdbcTemplate.query(query,
                ((rs, rowNum) -> new Board(
                        rs.getInt("postId"),
                        rs.getInt("userId"),
                        rs.getString("nickname"),
                        rs.getString("profileUrl"),
                        rs.getString("title"),
                        rs.getInt("category"),
                        rs.getInt("region"),
                        rs.getString("thumbnail"),
                        rs.getString("content"),
                        rs.getString("postCreateAt"),
                        rs.getInt("likeCount"),
                        rs.getInt("commentCount"),
                        rs.getInt("hits")
                )), userId, size, (page-1)*size);
    }

    //내가 좋아요한 글 조회
    public List<Board> getMyLikeBoardList(int userId, int page, int size) {
        String query = "select P.postId, P.userId, nickname, profileUrl, title, category, region, thumbnail, P.content, postCreateAt, likeCount, commentCount, hits\n" +
                "from (select * from `Like` where userId = ?) L\n" +
                "         join Post P on P.postId = L.postId\n" +
                "         join User U on P.userId = U.userId\n" +
                "         left join (select userId, postId, count(likeId) as likeCount from `Like` group by postId) LC on P.postId = LC.postId\n" +
                "         left join (SELECT postId, Count(commentId) as commentCount FROM Comment GROUP BY postId) C on C.postId = P.postId\n" +
                "         left join (SELECT postId, imgUrl as thumbnail FROM Image GROUP BY postId) I on I.postId = P.postId\n" +
                "order by P.postId desc\n" +
                "limit ? offset ?";
        return jdbcTemplate.query(query,
                ((rs, rowNum) -> new Board(
                        rs.getInt("postId"),
                        rs.getInt("userId"),
                        rs.getString("nickname"),
                        rs.getString("profileUrl"),
                        rs.getString("title"),
                        rs.getInt("category"),
                        rs.getInt("region"),
                        rs.getString("thumbnail"),
                        rs.getString("content"),
                        rs.getString("postCreateAt"),
                        rs.getInt("likeCount"),
                        rs.getInt("commentCount"),
                        rs.getInt("hits")
                )), userId, size, (page-1)*size);
    }

    //내가 작성한 글 총 게시글 수 조회
    public Integer getMyWriteBoardCount(int userId) {
        String query = "select COUNT(postId) from Post where userId = ?";
        return jdbcTemplate.queryForObject(query, int.class, userId);
    }

    //내가 좋아요한 글 총 게시글 수 조회
    public Integer getMyLikeBoardCount(int userId) {
        String query = "select COUNT(P.postId)\n" +
                "from Post P\n" +
                "    left join `Like` L on L.postId = P.postId\n" +
                "where L.userId = ? and L.animalId is null";
        return jdbcTemplate.queryForObject(query, int.class, userId);
    }
}
