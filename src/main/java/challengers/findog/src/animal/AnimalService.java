package challengers.findog.src.animal;

import challengers.findog.config.BaseException;
import challengers.findog.src.animal.model.*;
import challengers.findog.utils.JwtService;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jose.shaded.json.parser.JSONParser;
import com.nimbusds.jose.shaded.json.parser.ParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static challengers.findog.config.BaseResponseStatus.*;

@RequiredArgsConstructor
@Service
public class AnimalService {
    private final AnimalRepository animalRepository;
    private final JwtService jwtService;

    //유기 동물 공고 디비 저장
    public String insertAnimalPost(StringBuilder sb) throws BaseException{

        try {
            JSONParser parser	= new JSONParser();
            JSONObject obj 		= (JSONObject)parser.parse(sb.toString());
            JSONObject response = (JSONObject)obj.get("response");
            JSONObject body 	= (JSONObject)response.get("body");
            JSONObject items 	= (JSONObject)body.get("items");
            JSONArray item 	= (JSONArray) items.get("item");

            for (int i=0; i<item.size(); i++) {
                JSONObject data = (JSONObject) item.get(i);

                String desertionNo  = (String) data.get("desertionNo").toString();
                String filename     = (String) data.get("filename").toString();
                String happenDt 	= changeDateForm(data.get("happenDt").toString());
                String happenPlace	= (String) data.get("happenPlace").toString();
                String kindCd	    = (String) data.get("kindCd").toString();
                String colorCd 		= (String) data.get("colorCd").toString();
                String age		    = (String) data.get("age").toString();
                String weight 		= (String) data.get("weight").toString();
                String noticeNo	    = (String) data.get("noticeNo").toString();
                String noticeSdt	= changeDateForm(data.get("noticeSdt").toString());
                String noticeEdt	= changeDateForm(data.get("noticeEdt").toString());
                String popfile		= (String) data.get("popfile").toString();
                String processState	= (String) data.get("processState").toString();
                String sexCd	    = (String) data.get("sexCd").toString();
                String neuterYn		= (String) data.get("neuterYn").toString();
                String specialMark	= (String) data.get("specialMark").toString();
                String careNm		= (String) data.get("careNm").toString();
                String careTel		= (String) data.get("careTel").toString();
                String careAddr		= (String) data.get("careAddr").toString();
                String orgNm        = (String) data.get("orgNm").toString();

                Animal animal = new Animal(0, desertionNo, filename, happenDt, happenPlace, kindCd, colorCd, age, weight, noticeNo, noticeSdt, noticeEdt, popfile, processState, sexCd, neuterYn, specialMark, careNm, careTel, careAddr, orgNm);
                animalRepository.createAnimal(animal);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "불러온 유기동물 공고를 성공적으로 저장하였습니다.";
    }

    //날짜 형식 변경
    private String changeDateForm(String date){
        StringBuffer sb = new StringBuffer();

        sb.append(date);
        sb.insert(4, "-");
        sb.insert(7, "-");
        return sb.toString();
    }

    //유기동물 공고 리스트 조회
    public GetAnimalListRes getAnimalPostList(String jwt, int page, int size) throws BaseException{
        try{
            int userId = 0;
            if(jwt != null && jwt.length() != 0) {
                userId = jwtService.getUserIdx();
            }

            List<AnimalSimpleDto> animalList = animalRepository.getAnimalPostList(userId, page, size);
            int totalCount = animalRepository.getAnimalPostTotalCount();
            int totalPage = (totalCount % size != 0) ? totalCount / size + 1 : totalCount / size;

            PageCriteriaDto pageCriteriaDto = new PageCriteriaDto(totalCount, totalPage, page, size);
            return new GetAnimalListRes(pageCriteriaDto, animalList);
        } catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //유기동물 공고 상세 조회
    public Animal getAnimalPost(int animalId) throws BaseException{
        try{
            return animalRepository.getAnimalPost(animalId);
        } catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //유기동물 공고 관심 등록
    public String likeAnimalPost(int animalId, int userId) throws BaseException {
        int result;
        if(animalRepository.checkLikeAnimal(animalId, userId) == 1){
            throw new BaseException(DUPLICATED_LIKEANUMAL);
        }

        try{
            result = animalRepository.likeAnimalPost(animalId, userId);
        } catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }

        if(result == 0){
            throw new BaseException(FAIL_UPLOAD_LIKEANIMAL);
        }
        return "해당 유기동물 공고가 관심 등록되었습니다.";
    }

    //유기동물 공고 관심 해제
    public String unlikeAnimalPost(DeleteUnlikeAnimalReq deleteUnlikeAnimalReq, int userId) throws BaseException {
        int result;
        try{
            String animalIdList = deleteUnlikeAnimalReq.getAnimalIdList().toString();
            animalIdList = animalIdList.substring(1, animalIdList.length()-1);
            result = animalRepository.unlikeAnimalPost(animalIdList, userId);
        } catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
        if(result == 0){
            throw new BaseException(FAIL_DELETE_LIKEANIMAL);
        }
        return "해당 유기동물 공고 관심 내역이 삭제되었습니다.";
    }

    //관심 유기동물 공고 조회
    public GetAnimalListRes getLikeAnimalPostList(int userId, int page, int size) throws BaseException{
        try{
            List<AnimalSimpleDto> animalList = animalRepository.getLikeAnimalPostList(userId, page, size);
            int totalCount = animalRepository.getLikeAnimalPostTotalCount(userId);
            int totalPage = (totalCount % size != 0) ? totalCount / size + 1 : totalCount / size;

            PageCriteriaDto pageCriteriaDto = new PageCriteriaDto(totalCount, totalPage, page, size);
            return new GetAnimalListRes(pageCriteriaDto, animalList);
        } catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //유기동물 검색
    public GetAnimalListRes searchAnimals(int userId, int page, int size, String[] condition) throws BaseException {
        try{
            String filter = makeFilter(condition);
            List<AnimalSimpleDto> animalList = animalRepository.searchAnimals(userId, page, size, filter);
            int totalCount = animalRepository.getsearchedAnimalPostTotalCount(filter);
            int totalPage = (totalCount % size != 0) ? totalCount / size + 1 : totalCount / size;

            PageCriteriaDto pageCriteriaDto = new PageCriteriaDto(totalCount, totalPage, page, size);
            return new GetAnimalListRes(pageCriteriaDto, animalList);
        } catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //필터 조건 만드는 함수
    public String makeFilter(String[] condition) {
        StringBuilder sb = new StringBuilder();
        int andFlag = 0;

        sb.append("(");

        for(int i = 0; i < condition.length; i++){
            if(condition[i] != null && condition[i].length() != 0){
                if(andFlag == 1) {
                    sb.append(" and ");
                    andFlag = 0;
                }

                switch (i) {
                    case 0:
                        sb.append("(colorCd like '%").append(condition[i]).append("%' or ");
                        sb.append("kindCd like '%").append(condition[i]).append("%' or ");
                        sb.append("specialMark like '%").append(condition[i]).append("%')");
                        break;
                    case 1:
                        sb.append("orgNm like '%").append(condition[i]).append("%'");
                        break;
                    case 2:
                    case 3:
                        sb.append("kindCd like '%").append(condition[i]).append("%'");
                        break;
                    case 4:
                        sb.append("processState like '%").append(condition[i]).append("%'");
                        break;
                }
                andFlag = 1;
            }
        }

        sb.append(")");

        return sb.toString();
    }
}
