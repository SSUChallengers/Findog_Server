package challengers.findog.src.animal;

import challengers.findog.src.animal.model.Animal;
import challengers.findog.src.animal.model.AnimalSimpleDto;
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
        String query = "insert into Animal(desertionNo, filename, happenDt, happenPlace, kindCd, colorCd, age, weight, noticeNo, noticeSdt, noticeEdt, popfile, processState, sexCd, neuterYn, specialMark, careNm, careTel, careAddr, orgNm) value(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, ?)";
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
                        rs.getString("orgNm"),
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
}
