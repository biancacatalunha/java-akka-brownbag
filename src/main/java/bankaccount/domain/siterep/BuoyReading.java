package bankaccount.domain.siterep;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BuoyReading {
    @JsonProperty("SiteRep")
    private SiteRep SiteRep;
}
