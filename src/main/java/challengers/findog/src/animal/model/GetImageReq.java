package challengers.findog.src.animal.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class GetImageReq {
    private MultipartFile input;
    private String serverUrl;
}
