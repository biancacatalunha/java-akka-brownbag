package bankaccount.domain.siterep;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SiteRep {
    @JsonProperty("DV")
    private DV DV;
}
