package challengers.findog.src.animal;

import challengers.findog.config.BaseException;
import challengers.findog.config.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

@Component
@RestController
@RequiredArgsConstructor
@RequestMapping("/openApi")
public class OpenApiController {
    private final AnimalService animalService;
    private final OpenApiService openApiService;

    //유기동물 조회
    @Scheduled(cron = "0 0 12 * * ?")
    @GetMapping("animal")
    public BaseResponse<String> getAnimalOpenApi() throws IOException {
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1543061/abandonmentPublicSrvc/abandonmentPublic"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=6zpcReaX29KLMWkD%2BxKr2V8lGbNCJ5ZyoaTtsAGgRzHtcYApZ6acokfImEcgW20CjXD6toNEgd5Y2yygrKN4Ow%3D%3D"); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("100", "UTF-8")); /*한 페이지 결과 수(1,000 이하)*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지 번호*/
        urlBuilder.append("&" + URLEncoder.encode("_type", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*xml(기본값) 또는 json*/
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();

        try{
            return new BaseResponse<>(animalService.insertAnimalPost(sb));
        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    //유기동물 사진 리스트 전송
    @GetMapping("animal/img")
    public BaseResponse<List<String>> getAnimalImgOpenApi(@RequestParam("size") String size, @RequestParam("page") String page) throws IOException {
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1543061/abandonmentPublicSrvc/abandonmentPublic"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=6zpcReaX29KLMWkD%2BxKr2V8lGbNCJ5ZyoaTtsAGgRzHtcYApZ6acokfImEcgW20CjXD6toNEgd5Y2yygrKN4Ow%3D%3D"); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode(size, "UTF-8")); /*한 페이지 결과 수(1,000 이하)*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode(page, "UTF-8")); /*페이지 번호*/
        urlBuilder.append("&" + URLEncoder.encode("_type", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*xml(기본값) 또는 json*/
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();

        try{
            return new BaseResponse<>(openApiService.getAnimalImgList(sb));
        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }
}
