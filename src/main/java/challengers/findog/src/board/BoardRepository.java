package challengers.findog.src.board;

import challengers.findog.src.board.model.Board;
import challengers.findog.src.board.model.PatchBoardReq;
import challengers.findog.src.board.model.PostBoardReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;


@Repository
public class BoardRepository {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //게시글 작성
    public int createBoard(PostBoardReq postBoardReq) {
        String query = "insert into Post(userId, title, category, region, content) value (?, ?, ?, ?, ?)";
        Object[] postBoardParams = new Object[]{postBoardReq.getUserId(), postBoardReq.getTitle(), postBoardReq.getCategory(), postBoardReq.getRegion(), postBoardReq.getContent()};
        this.jdbcTemplate.update(query, postBoardParams);

        String lastInsertIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    //게시글 작성 - 게시글 사진 저장
    public int createBoardPhoto(int postId, String imgUrl) {
        String query = "insert into Image(postId, imgUrl) value (?, ?)";
        Object[] postBoardParams = new Object[]{postId, imgUrl};

        return this.jdbcTemplate.update(query, postBoardParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
    }

    //게시글 수정
    public int updateBoard(int postId, PatchBoardReq patchBoardReq) {
        String query = "update Post set title = ?, category = ?, region = ?, content = ? where postId = ?";
        Object[] postBoardParams = new Object[]{patchBoardReq.getTitle(), patchBoardReq.getCategory(), patchBoardReq.getRegion(), patchBoardReq.getContent(), postId};
        this.jdbcTemplate.update(query, postBoardParams);

        return this.jdbcTemplate.update(query, postBoardParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
    }

    //게시글 수정 - 게시글 사진 유무 확인
    public List<String> checkImg(int postId) {
        String query = "select imgUrl FROM Image WHERE postId = ?";
        return this.jdbcTemplate.queryForList(query, String.class, postId);
    }

    //게시글 수정/삭제 - 게시글 사진 삭제
    public int deleteImg(int postId) {
        String query = "delete FROM Image WHERE postId = ?";
        return this.jdbcTemplate.update(query, postId);
    }

    //게시글 삭제
    public int deleteBoard(int postId) {
        String query = "delete FROM Post WHERE postId = ?";
        return this.jdbcTemplate.update(query, postId);
    }

    //해당 게시글 조회
    public Board getBoard(int postId) {
        String query = "select P.postId, P.userId, nickname, profileUrl, title, category, region, thumbnail, P.content, postCreateAt, likeCount, commentCount, hits " +
                "from Post P left join User U on P.userId = U.userId " +
                "left join (SELECT postId, Count(commentId) as commentCount FROM Comment GROUP BY postId) C on C.postId = P.postId " +
                "left join (SELECT postId, imgUrl as thumbnail FROM Image GROUP BY postId) I on I.postId = P.postId " +
                "left join (SELECT postId, Count(likeId) as likeCount FROM `Like` GROUP BY postId) L on L.postId = P.postId " +
                "where P.postId =?";
        return jdbcTemplate.queryForObject(query,
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
                )), postId);
    }

    //해당 게시글 사진 리스트 조회
    public List<String> getBoardImage(int postId) {
        String query = "select imgUrl from Image inner join Post P on Image.postId = P.postId where P.postId = ?";
        return this.jdbcTemplate.queryForList(query, String.class, postId);
    }

    //게시글 조회수+1
    public int viewCount(int postId) {
        String query = "update Post set hits = hits + 1 where postId=?";
        return this.jdbcTemplate.update(query, postId);
    }

    //게시글 좋아요 여부
    public int userLiked(int postId, int userId) {
        String query = "select exists(select likeId from `Like` where postId = ? and userId = ?)";
        return this.jdbcTemplate.queryForObject(query, int.class, postId, userId);
    }

    //게시글 리스트 조회
    public List<Board> getBoardList(int page, int size) {
        String query = "select P.postId, P.userId, nickname, profileUrl, title, category, region, thumbnail, P.content, postCreateAt, likeCount, commentCount, hits " +
                "from Post P left join User U on P.userId = U.userId " +
                "left join (SELECT postId, Count(commentId) as commentCount FROM Comment GROUP BY postId) C on C.postId = P.postId " +
                "left join (SELECT postId, imgUrl as thumbnail FROM Image GROUP BY postId) I on I.postId = P.postId " +
                "left join (SELECT postId, Count(likeId) as likeCount FROM `Like` GROUP BY postId) L on L.postId = P.postId " +
                "order by postId desc, postCreateAt desc " +
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
                )), size, (page-1)*size);
    }

    //게시글 좋아요
    public void likeBoard(int postId, int userId) {
        String query = "insert into `Like` (postId, userId) VALUES (?,?)";
        this.jdbcTemplate.update(query, postId, userId);
    }

    //게시글 좋아요 취소
    public void likeCancelBoard(int postId , int userId) {
        String query = "delete from `Like` where postId =? and userId =?";
        this.jdbcTemplate.update(query, postId, userId);
    }

    public int checkAuth(int postId) {
        String query = "select userId from Post where postId = ?";
        return this.jdbcTemplate.queryForObject(query, int.class, postId);
    }

    /** 게시글 검색 **/
    //지역x, 카테고리x
    public List<Board> searchBoard(String keyword, String start_period, String end_period, int sort, int page, int size) {
        if(sort == 1) { //최신순 조회
            String query = "select P.postId, P.userId, nickname, profileUrl, title, category, region, thumbnail, P.content, postCreateAt, if(likeCount is null, 0, likeCount) as likeCount, if(commentCount is null, 0, commentCount) as commentCount, hits\n" +
                    "from Post P left join User U on P.userId = U.userId\n" +
                    "    left join (SELECT postId, Count(commentId) as commentCount FROM Comment GROUP BY postId) C on C.postId = P.postId\n" +
                    "    left join (SELECT postId, imgUrl as thumbnail FROM Image GROUP BY postId) I on I.postId = P.postId\n" +
                    "    left join (SELECT postId, Count(likeId) as likeCount FROM `Like` GROUP BY postId) L on L.postId = P.postId\n" +
                    "where ((title like concat ('%', ?, '%')) or (P.content like concat ('%', ?, '%')))\n" +
                    "  and postCreateAt between STR_TO_DATE(?, '%Y%m%d') and STR_TO_DATE(concat(?, 235959), '%Y%m%d%H%i%s')\n" +
                    "order by postId desc, postCreateAt desc\n" +
                    "limit ? offset ?";
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Board.class), keyword, keyword, start_period, end_period, size, (page - 1) * size);
        }
        else if(sort == 2) { //조회수순 조회
            String query = "select P.postId, P.userId, nickname, profileUrl, title, category, region, thumbnail, P.content, postCreateAt, if(likeCount is null, 0, likeCount) as likeCount, if(commentCount is null, 0, commentCount) as commentCount, hits\n" +
                    "from Post P left join User U on P.userId = U.userId\n" +
                    "    left join (SELECT postId, Count(commentId) as commentCount FROM Comment GROUP BY postId) C on C.postId = P.postId\n" +
                    "    left join (SELECT postId, imgUrl as thumbnail FROM Image GROUP BY postId) I on I.postId = P.postId\n" +
                    "    left join (SELECT postId, Count(likeId) as likeCount FROM `Like` GROUP BY postId) L on L.postId = P.postId\n" +
                    "where ((title like concat ('%', ?, '%')) or (P.content like concat ('%', ?, '%')))\n" +
                    "  and postCreateAt between STR_TO_DATE(?, '%Y%m%d') and STR_TO_DATE(concat(?, 235959), '%Y%m%d%H%i%s')\n" +
                    "order by hits desc, postCreateAt desc\n" +
                    "limit ? offset ?";
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Board.class), keyword, keyword, start_period, end_period, size, (page - 1) * size);
        }
        else { //좋아요순 조회
            String query = "select P.postId, P.userId, nickname, profileUrl, title, category, region, thumbnail, P.content, postCreateAt, if(likeCount is null, 0, likeCount) as likeCount, if(commentCount is null, 0, commentCount) as commentCount, hits\n" +
                    "from Post P left join User U on P.userId = U.userId\n" +
                    "    left join (SELECT postId, Count(commentId) as commentCount FROM Comment GROUP BY postId) C on C.postId = P.postId\n" +
                    "    left join (SELECT postId, imgUrl as thumbnail FROM Image GROUP BY postId) I on I.postId = P.postId\n" +
                    "    left join (SELECT postId, Count(likeId) as likeCount FROM `Like` GROUP BY postId) L on L.postId = P.postId\n" +
                    "where ((title like concat ('%', ?, '%')) or (P.content like concat ('%', ?, '%')))\n" +
                    "  and postCreateAt >= ? and postCreateAt <= ?\n" +
                    "order by likeCount desc, postCreateAt desc\n" +
                    "limit ? offset ?";
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Board.class), keyword, keyword, start_period, end_period, size, (page - 1) * size);
        }
    }

    //지역o, 카테고리x
    public List<Board> searchBoardRegion(String keyword, int region, String start_period, String end_period, int sort, int page, int size) {
        if(sort == 1) { //최신순 조회
            String query = "select P.postId, P.userId, nickname, profileUrl, title, category, region, thumbnail, P.content, postCreateAt, if(likeCount is null, 0, likeCount) as likeCount, if(commentCount is null, 0, commentCount) as commentCount, hits\n" +
                    "from Post P left join User U on P.userId = U.userId\n" +
                    "    left join (SELECT postId, Count(commentId) as commentCount FROM Comment GROUP BY postId) C on C.postId = P.postId\n" +
                    "    left join (SELECT postId, imgUrl as thumbnail FROM Image GROUP BY postId) I on I.postId = P.postId\n" +
                    "    left join (SELECT postId, Count(likeId) as likeCount FROM `Like` GROUP BY postId) L on L.postId = P.postId\n" +
                    "where ((title like concat ('%', ?, '%')) or (P.content like concat ('%', ?, '%')))\n" +
                    "  and region = ?\n" +
                    "  and postCreateAt between STR_TO_DATE(?, '%Y%m%d') and STR_TO_DATE(concat(?, 235959), '%Y%m%d%H%i%s')\n" +
                    "order by postId desc, postCreateAt desc\n" +
                    "limit ? offset ?";
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Board.class), keyword, keyword, region, start_period, end_period, size, (page - 1) * size);
        }
        else if(sort == 2) { //조회수순 조회
            String query = "select P.postId, P.userId, nickname, profileUrl, title, category, region, thumbnail, P.content, postCreateAt, if(likeCount is null, 0, likeCount) as likeCount, if(commentCount is null, 0, commentCount) as commentCount, hits\n" +
                    "from Post P left join User U on P.userId = U.userId\n" +
                    "    left join (SELECT postId, Count(commentId) as commentCount FROM Comment GROUP BY postId) C on C.postId = P.postId\n" +
                    "    left join (SELECT postId, imgUrl as thumbnail FROM Image GROUP BY postId) I on I.postId = P.postId\n" +
                    "    left join (SELECT postId, Count(likeId) as likeCount FROM `Like` GROUP BY postId) L on L.postId = P.postId\n" +
                    "where ((title like concat ('%', ?, '%')) or (P.content like concat ('%', ?, '%')))\n" +
                    "  and region = ?\n" +
                    "  and postCreateAt between STR_TO_DATE(?, '%Y%m%d') and STR_TO_DATE(concat(?, 235959), '%Y%m%d%H%i%s')\n" +
                    "order by hits desc, postCreateAt desc\n" +
                    "limit ? offset ?";
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Board.class), keyword, keyword, region, start_period, end_period, size, (page - 1) * size);
        }
        else { //좋아요순 조회
            String query = "select P.postId, P.userId, nickname, profileUrl, title, category, region, thumbnail, P.content, postCreateAt, if(likeCount is null, 0, likeCount) as likeCount, if(commentCount is null, 0, commentCount) as commentCount, hits\n" +
                    "from Post P left join User U on P.userId = U.userId\n" +
                    "    left join (SELECT postId, Count(commentId) as commentCount FROM Comment GROUP BY postId) C on C.postId = P.postId\n" +
                    "    left join (SELECT postId, imgUrl as thumbnail FROM Image GROUP BY postId) I on I.postId = P.postId\n" +
                    "    left join (SELECT postId, Count(likeId) as likeCount FROM `Like` GROUP BY postId) L on L.postId = P.postId\n" +
                    "where ((title like concat ('%', ?, '%')) or (P.content like concat ('%', ?, '%')))\n" +
                    "  and region = ?\n" +
                    "  and postCreateAt >= ? and postCreateAt <= ?\n" +
                    "order by likeCount desc, postCreateAt desc\n" +
                    "limit ? offset ?";
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Board.class), keyword, keyword, region, start_period, end_period, size, (page - 1) * size);
        }
    }

    //지역x, 카테고리o
    public List<Board> searchBoardCategory(String keyword, int category, String start_period, String end_period, int sort, int page, int size) {
        if(sort == 1) { //최신순 조회
            String query = "select P.postId, P.userId, nickname, profileUrl, title, category, region, thumbnail, P.content, postCreateAt, if(likeCount is null, 0, likeCount) as likeCount, if(commentCount is null, 0, commentCount) as commentCount, hits\n" +
                    "from Post P left join User U on P.userId = U.userId\n" +
                    "    left join (SELECT postId, Count(commentId) as commentCount FROM Comment GROUP BY postId) C on C.postId = P.postId\n" +
                    "    left join (SELECT postId, imgUrl as thumbnail FROM Image GROUP BY postId) I on I.postId = P.postId\n" +
                    "    left join (SELECT postId, Count(likeId) as likeCount FROM `Like` GROUP BY postId) L on L.postId = P.postId\n" +
                    "where ((title like concat ('%', ?, '%')) or (P.content like concat ('%', ?, '%')))\n" +
                    "  and category = ?\n" +
                    "  and postCreateAt between STR_TO_DATE(?, '%Y%m%d') and STR_TO_DATE(concat(?, 235959), '%Y%m%d%H%i%s')\n" +
                    "order by postId desc, postCreateAt desc\n" +
                    "limit ? offset ?";
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Board.class), keyword, keyword, category, start_period, end_period, size, (page - 1) * size);
        }
        else if(sort == 2) { //조회수순 조회
            String query = "select P.postId, P.userId, nickname, profileUrl, title, category, region, thumbnail, P.content, postCreateAt, if(likeCount is null, 0, likeCount) as likeCount, if(commentCount is null, 0, commentCount) as commentCount, hits\n" +
                    "from Post P left join User U on P.userId = U.userId\n" +
                    "    left join (SELECT postId, Count(commentId) as commentCount FROM Comment GROUP BY postId) C on C.postId = P.postId\n" +
                    "    left join (SELECT postId, imgUrl as thumbnail FROM Image GROUP BY postId) I on I.postId = P.postId\n" +
                    "    left join (SELECT postId, Count(likeId) as likeCount FROM `Like` GROUP BY postId) L on L.postId = P.postId\n" +
                    "where ((title like concat ('%', ?, '%')) or (P.content like concat ('%', ?, '%')))\n" +
                    "  and category = ?\n" +
                    "  and postCreateAt between STR_TO_DATE(?, '%Y%m%d') and STR_TO_DATE(concat(?, 235959), '%Y%m%d%H%i%s')\n" +
                    "order by hits desc, postCreateAt desc\n" +
                    "limit ? offset ?";
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Board.class), keyword, keyword, category, start_period, end_period, size, (page - 1) * size);
        }
        else { //좋아요순 조회
            String query = "select P.postId, P.userId, nickname, profileUrl, title, category, region, thumbnail, P.content, postCreateAt, if(likeCount is null, 0, likeCount) as likeCount, if(commentCount is null, 0, commentCount) as commentCount, hits\n" +
                    "from Post P left join User U on P.userId = U.userId\n" +
                    "    left join (SELECT postId, Count(commentId) as commentCount FROM Comment GROUP BY postId) C on C.postId = P.postId\n" +
                    "    left join (SELECT postId, imgUrl as thumbnail FROM Image GROUP BY postId) I on I.postId = P.postId\n" +
                    "    left join (SELECT postId, Count(likeId) as likeCount FROM `Like` GROUP BY postId) L on L.postId = P.postId\n" +
                    "where ((title like concat ('%', ?, '%')) or (P.content like concat ('%', ?, '%')))\n" +
                    "  and category = ?\n" +
                    "  and postCreateAt >= ? and postCreateAt <= ?\n" +
                    "order by likeCount desc, postCreateAt desc\n" +
                    "limit ? offset ?";
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Board.class), keyword, keyword, category, start_period, end_period, size, (page - 1) * size);
        }
    }

    //지역o, 카테고리o
    public List<Board> searchBoardFull(String keyword, int region, int category, String start_period, String end_period, int sort, int page, int size) {
        if(sort == 1) { //최신순 조회
            String query = "select P.postId, P.userId, nickname, profileUrl, title, category, region, thumbnail, P.content, postCreateAt, if(likeCount is null, 0, likeCount) as likeCount, if(commentCount is null, 0, commentCount) as commentCount, hits\n" +
                    "from Post P left join User U on P.userId = U.userId\n" +
                    "    left join (SELECT postId, Count(commentId) as commentCount FROM Comment GROUP BY postId) C on C.postId = P.postId\n" +
                    "    left join (SELECT postId, imgUrl as thumbnail FROM Image GROUP BY postId) I on I.postId = P.postId\n" +
                    "    left join (SELECT postId, Count(likeId) as likeCount FROM `Like` GROUP BY postId) L on L.postId = P.postId\n" +
                    "where ((title like concat ('%', ?, '%')) or (P.content like concat ('%', ?, '%')))\n" +
                    "  and region = ?\n" +
                    "  and category = ?\n" +
                    "  and postCreateAt between STR_TO_DATE(?, '%Y%m%d') and STR_TO_DATE(concat(?, 235959), '%Y%m%d%H%i%s')\n" +
                    "order by postId desc, postCreateAt desc\n" +
                    "limit ? offset ?";
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Board.class), keyword, keyword, region, category, start_period, end_period, size, (page - 1) * size);
        }
        else if(sort == 2) { //조회수순 조회
            String query = "select P.postId, P.userId, nickname, profileUrl, title, category, region, thumbnail, P.content, postCreateAt, if(likeCount is null, 0, likeCount) as likeCount, if(commentCount is null, 0, commentCount) as commentCount, hits\n" +
                    "from Post P left join User U on P.userId = U.userId\n" +
                    "    left join (SELECT postId, Count(commentId) as commentCount FROM Comment GROUP BY postId) C on C.postId = P.postId\n" +
                    "    left join (SELECT postId, imgUrl as thumbnail FROM Image GROUP BY postId) I on I.postId = P.postId\n" +
                    "    left join (SELECT postId, Count(likeId) as likeCount FROM `Like` GROUP BY postId) L on L.postId = P.postId\n" +
                    "where ((title like concat ('%', ?, '%')) or (P.content like concat ('%', ?, '%')))\n" +
                    "  and region = ?\n" +
                    "  and category = ?\n" +
                    "  and postCreateAt between STR_TO_DATE(?, '%Y%m%d') and STR_TO_DATE(concat(?, 235959), '%Y%m%d%H%i%s')\n" +
                    "order by hits desc, postCreateAt desc\n" +
                    "limit ? offset ?";
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Board.class), keyword, keyword, region, category, start_period, end_period, size, (page - 1) * size);
        }
        else { //좋아요순 조회
            String query = "select P.postId, P.userId, nickname, profileUrl, title, category, region, thumbnail, P.content, postCreateAt, if(likeCount is null, 0, likeCount) as likeCount, if(commentCount is null, 0, commentCount) as commentCount, hits\n" +
                    "from Post P left join User U on P.userId = U.userId\n" +
                    "    left join (SELECT postId, Count(commentId) as commentCount FROM Comment GROUP BY postId) C on C.postId = P.postId\n" +
                    "    left join (SELECT postId, imgUrl as thumbnail FROM Image GROUP BY postId) I on I.postId = P.postId\n" +
                    "    left join (SELECT postId, Count(likeId) as likeCount FROM `Like` GROUP BY postId) L on L.postId = P.postId\n" +
                    "where ((title like concat ('%', ?, '%')) or (P.content like concat ('%', ?, '%')))\n" +
                    "  and region = ?\n" +
                    "  and category = ?\n" +
                    "  and postCreateAt >= ? and postCreateAt <= ?\n" +
                    "order by likeCount desc, postCreateAt desc\n" +
                    "limit ? offset ?";
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Board.class), keyword, keyword, region, category, start_period, end_period, size, (page - 1) * size);
        }
    }

    /** 검색 게시글 수 조회 **/
    //지역x, 카테고리x
    public Integer getBoardCount(String keyword, String start_period, String end_period) {
        String query = "select count(postId)\n" +
                "from Post\n" +
                "where ((title like concat ('%', ?, '%')) or (content like concat ('%', ?, '%')))\n" +
                "  and postCreateAt between STR_TO_DATE(?, '%Y%m%d') and STR_TO_DATE(concat(?, 235959), '%Y%m%d%H%i%s')";
        return jdbcTemplate.queryForObject(query, int.class, keyword, keyword, start_period, end_period);
    }

    //지역o, 카테고리x
    public Integer getBoardCountRegion(String keyword, int region, String start_period, String end_period) {
        String query = "select count(postId)\n" +
                "from Post\n" +
                "where ((title like concat ('%', ?, '%')) or (content like concat ('%', ?, '%')))\n" +
                "  and region = ?\n" +
                "  and postCreateAt between STR_TO_DATE(?, '%Y%m%d') and STR_TO_DATE(concat(?, 235959), '%Y%m%d%H%i%s')";
        return jdbcTemplate.queryForObject(query, int.class, keyword, keyword, region, start_period, end_period);
    }

    //지역x, 카테고리o
    public Integer getBoardCountCategory(String keyword, int category, String start_period, String end_period) {
        String query = "select count(postId)\n" +
                "from Post\n" +
                "where ((title like concat ('%', ?, '%')) or (content like concat ('%', ?, '%')))\n" +
                "  and category = ?\n" +
                "  and postCreateAt between STR_TO_DATE(?, '%Y%m%d') and STR_TO_DATE(concat(?, 235959), '%Y%m%d%H%i%s')";
        return jdbcTemplate.queryForObject(query, int.class, keyword, keyword, category, start_period, end_period);
    }

    //지역o, 카테고리o
    public Integer getBoardCountFull(String keyword, int region, int category, String start_period, String end_period) {
        String query = "select count(postId)\n" +
                "from Post\n" +
                "where ((title like concat ('%', ?, '%')) or (content like concat ('%', ?, '%')))\n" +
                "  and region = ?\n" +
                "  and category = ?\n" +
                "  and postCreateAt between STR_TO_DATE(?, '%Y%m%d') and STR_TO_DATE(concat(?, 235959), '%Y%m%d%H%i%s')";
        return jdbcTemplate.queryForObject(query, int.class, keyword, keyword, region, category, start_period, end_period);
    }
}
