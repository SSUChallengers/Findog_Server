package challengers.findog.src.animal;

import challengers.findog.config.BaseException;
import challengers.findog.config.BaseResponse;
import challengers.findog.src.animal.model.*;
import challengers.findog.utils.JwtService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jose.shaded.json.parser.JSONParser;
import com.nimbusds.jose.shaded.json.parser.ParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/animals")
public class AnimalController {
    private final AnimalService animalService;
    private final JwtService jwtService;
    private static final RestTemplate REST_TEMPLATE;

    static {
        // RestTemplate 기본 설정을 위한 Factory 생성
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(3000);
        factory.setBufferRequestBody(false); // 파일 전송은 이 설정을 꼭 해주자.
        REST_TEMPLATE = new RestTemplate(factory);
    }

    /**
     * 유기동물 리스트 조회 API
     * @param page
     * @param size
     * @return
     */
    @Transactional(readOnly = true)
    @GetMapping("")
    public BaseResponse<GetAnimalListRes> getAnimalPostList(@RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value = "size", defaultValue = "6") int size){
        try{
            return new BaseResponse<>(animalService.getAnimalPostList(jwtService.getJwt(), page, size));
        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 유기동물 공고 상세 조회 API
     * @param animalId
     * @return
     */
    @GetMapping("/{animalId}")
    public BaseResponse<Animal> getAnimalPost(@PathVariable("animalId") int animalId){
        try{
            return new BaseResponse<>(animalService.getAnimalPost(animalId));
        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 유기동물 공고 관심 등록 API
     * @param animalId
     * @return
     */
    @PostMapping("/like")
    public BaseResponse<String> likeAnimalPost(@RequestParam("animalId") int animalId) {
        try{
            int userId = jwtService.getUserIdx();
            return new BaseResponse<>(animalService.likeAnimalPost(animalId, userId));
        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 유기동물 공고 관심 해제 API
     * @param deleteUnlikeAnimalReq
     * @return
     */
    @DeleteMapping("/unlike")
    public BaseResponse<String> unlikeAnimalPost(@RequestBody DeleteUnlikeAnimalReq deleteUnlikeAnimalReq) {
        try{
            int userId = jwtService.getUserIdx();
            return new BaseResponse<>(animalService.unlikeAnimalPost(deleteUnlikeAnimalReq, userId));
        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 관심있는 공고
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/mypage")
    public BaseResponse<GetAnimalListRes> getLikeAnimalList(@RequestParam(value = "page", defaultValue = "1") int page, @RequestParam(value = "size", defaultValue = "6") int size){
        try{
            int userId = jwtService.getUserIdx();
            return new BaseResponse<>(animalService.getLikeAnimalPostList(userId, page, size));
        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 유기동물 공고 조회
     * @param page
     * @param size
     * @param word
     * @param region
     * @param category
     * @param breed
     * @param status
     * @return
     */
    @GetMapping("/search")
    public BaseResponse<GetAnimalListRes> searchAnimals(@RequestParam(value = "page", defaultValue = "1") int page,
                                                        @RequestParam(value = "size", defaultValue = "6") int size,
                                                        @RequestParam(value = "word", required = false) String word,
                                                        @RequestParam(value = "region", required = false) String region,
                                                        @RequestParam(value = "category", required = false) String category,
                                                        @RequestParam(value = "breed", required = false) String breed,
                                                        @RequestParam(value = "status", required = false) String status) {
        try{
            int userId = 0;
            if(jwtService.getJwt() != null && jwtService.getJwt().length() != 0) {
                userId = jwtService.getUserIdx();
            }

            String[] condition = {word, region, category, breed, status};

            return new BaseResponse<>(animalService.searchAnimals(userId, page, size, condition));
        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }

    }

    /**
     * 유기동물 공고 사진 검색
     * @param getImageReq
     * @return
     */
    @PostMapping("/searchImage")
    public ResponseEntity<?> searchImage(@ModelAttribute GetImageReq getImageReq) throws IOException {
    //public BaseResponse<GetAnimalListRes> uploadImages(@ModelAttribute GetImageReq getImageReq) {

        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        JsonNode response;
        HttpStatus httpStatus = HttpStatus.CREATED;

        try {
            if (!getImageReq.getInput().isEmpty()) {
                map.add("input", getImageReq.getInput().getResource());
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity
                    = new HttpEntity<>(map, headers);
            response = REST_TEMPLATE.postForObject(getImageReq.getServerUrl(), requestEntity, JsonNode.class);

        } catch (HttpStatusCodeException e) {
            HttpStatus errorHttpStatus = HttpStatus.valueOf(e.getStatusCode().value());
            String errorResponse = e.getResponseBodyAsString();
            return new ResponseEntity<>(errorResponse, errorHttpStatus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
//        }  catch (BaseException e){
//            return new BaseResponse<>(e.getStatus());
//        }
        //return new ResponseEntity<>(response, httpStatus);
        //return new BaseResponse<>(searchAnimals("breed" = response.toString()));

        String classNm = null;
        try{
            JSONParser parser	= new JSONParser();
            JSONObject obj 		= (JSONObject)parser.parse(response.toString());
            classNm = obj.get("class_name").toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        BaseResponse<GetAnimalListRes> getAnimalListRes = searchAnimals(1, 6, null, null, null, classNm, null);

        return new ResponseEntity<>(getAnimalListRes, httpStatus);
    }
}
