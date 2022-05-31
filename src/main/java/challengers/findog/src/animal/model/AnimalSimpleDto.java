package challengers.findog.src.animal.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnimalSimpleDto {
    private int animalId;
    private String processState;
    private String sexCd;
    private String neuterYn;
    private String kindCd;
    private String happenDt;
    private String careNm;
    private String happenPlace;
    private String popfile;

    private int likeFlag;
}
