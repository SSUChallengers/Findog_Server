package challengers.findog.src.animal;

import challengers.findog.config.BaseException;
import challengers.findog.config.BaseResponse;
import challengers.findog.src.animal.model.Animal;
import challengers.findog.src.animal.model.GetAnimalListRes;
import challengers.findog.src.animal.model.PageCriteriaDto;
import challengers.findog.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/animals")
public class AnimalController {
    private final AnimalService animalService;
    private final JwtService jwtService;

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
}
