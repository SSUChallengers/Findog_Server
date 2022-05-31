package challengers.findog.src.animal;

import challengers.findog.src.animal.model.Animal;
import challengers.findog.src.animal.model.AnimalSimpleDto;
import challengers.findog.src.animal.model.DeleteUnlikeAnimalReq;
import challengers.findog.src.animal.model.PageCriteriaDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class AnimalRepository {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //유기동물 공고 저장
    public int createAnimal(Animal animal){
        String query = "insert ignore into Animal(desertionNo, filename, happenDt, happenPlace, kindCd, colorCd, age, weight, noticeNo, noticeSdt, noticeEdt, popfile, processState, sexCd, neuterYn, specialMark, careNm, careTel, careAddr, orgNm) value(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        Object[] params = new Object[]{
                animal.getDesertionNo(),
                animal.getFilename(),
                animal.getHappenDt(),
                animal.getHappenPlace(),
                animal.getKindCd(),
                animal.getColorCd(),
                animal.getAge(),
                animal.getWeight(),
                animal.getNoticeNo(),
                animal.getNoticeSdt(),
                animal.getNoticeEdt(),
                animal.getPopfile(),
                animal.getProcessState(),
                animal.getSexCd(),
                animal.getNeuterYn(),
                animal.getSpecialMark(),
                animal.getCareNm(),
                animal.getCareTel(),
                animal.getCareAddr(),
                animal.getOrgNm()
        };
        return jdbcTemplate.update(query, params);
    }

    //로그인 안한 사람이 유기동물 공고 조회
    public List<AnimalSimpleDto> getAnimalPostList(int userId, int page, int size) {
        String query = "select * " +
                "from Animal left join (select animalId, if(userId > 0 , 1, 0) as likeFlag from `Like` where userId = ?) l on Animal.animalId = l.animalId " +
                "order by noticeSdt desc, desertionNo desc " +
                "limit ? offset ?";
        Object[] params = new Object[]{userId, size, (page - 1) * size};
        return jdbcTemplate.query(query,
                (rs, rowNum) -> new AnimalSimpleDto(
                        rs.getInt("animalId"),
                        rs.getString("processState"),
                        rs.getString("sexCd"),
                        rs.getString("neuterYn"),
                        rs.getString("kindCd"),
                        rs.getString("happenDt"),
                        rs.getString("careNm"),
                        rs.getString("happenPlace"),
                        rs.getString("popfile"),
                        rs.getInt("likeFlag")
                ), params);
    }

    //전체 유기동물 공고 개수
    public int getAnimalPostTotalCount(){
        String query = "select count(desertionNo) from Animal";
        return jdbcTemplate.queryForObject(query, int.class);
    }

    //유기동물 공고 상세 조회
    public Animal getAnimalPost(int animalId){
        String query = "select * from Animal where animalId = ?";
        return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(Animal.class), animalId);
    }

    //유기동물 공고 관심 등록
    public int likeAnimalPost(int animalId, int userId){
        String query = "insert into `Like` (userId, animalId) values (?, ?)";
        Object[] params = new Object[]{userId, animalId};
        return jdbcTemplate.update(query, params);
    }

    //유기동물 공고 관심 등록 중복 여부 확인
    public int checkLikeAnimal(int animalId, int userId){
        String query = "select exists (select animalId from `Like` where userId = ? and animalId = ?)";
        Object[] params = new Object[]{userId, animalId};
        return jdbcTemplate.queryForObject(query, int.class, params);
    }

    //유기동물 공고 관심 삭제
    public int unlikeAnimalPost(String animalIdList, int userId){
        String query = "delete from `Like` where userId = ? and animalId in" + "(" + animalIdList + ")";
        return jdbcTemplate.update(query, userId);
    }

    //관심 유기동물 공고 조회
    public List<AnimalSimpleDto> getLikeAnimalPostList(int userId, int page, int size) {
        String query = "select * from Animal natural join (select animalId, if(userId > 0 , 1, 0) as likeFlag, likeCreateAt from `Like` where userId = ?) T " +
                "order by likeCreateAt desc, desertionNo desc limit ? offset ?";
        Object[] params = new Object[]{userId, size, (page - 1) * size};
        return jdbcTemplate.query(query,
                (rs, rowNum) -> new AnimalSimpleDto(
                        rs.getInt("animalId"),
                        rs.getString("processState"),
                        rs.getString("sexCd"),
                        rs.getString("neuterYn"),
                        rs.getString("kindCd"),
                        rs.getString("happenDt"),
                        rs.getString("careNm"),
                        rs.getString("happenPlace"),
                        rs.getString("popfile"),
                        rs.getInt("likeFlag")
                ), params);
    }

    //관심 유기동물 공고 개수
    public int getLikeAnimalPostTotalCount(int userId){
        String query = "select count(desertionNo) from Animal natural join (select * from `Like` where userId = ?) T";
        return jdbcTemplate.queryForObject(query, int.class, userId);
    }

    //유기동물 공고 검색
    public List<AnimalSimpleDto> searchAnimals(int userId, int page, int size, String filter) {
        String query = "select * from (select * from Animal where " + filter +
        ") A left join (select animalId, if(userId > 0 , 1, 0) as likeFlag from `Like` where userId = ?) l on A.animalId = l.animalId " +
                "order by noticeSdt desc, desertionNo desc limit ? offset ?";

        Object[] params = new Object[]{userId, size, (page - 1) * size};
        return jdbcTemplate.query(query,
                (rs, rowNum) -> new AnimalSimpleDto(
                        rs.getInt("animalId"),
                        rs.getString("processState"),
                        rs.getString("sexCd"),
                        rs.getString("neuterYn"),
                        rs.getString("kindCd"),
                        rs.getString("happenDt"),
                        rs.getString("careNm"),
                        rs.getString("happenPlace"),
                        rs.getString("popfile"),
                        rs.getInt("likeFlag")
                ), params);
    }

    //검색한 유기동물 페이징
    public int getsearchedAnimalPostTotalCount(String filter) {
        String query = "select count(desertionNo) from Animal where " + filter;
        return jdbcTemplate.queryForObject(query, int.class);
    }

}
