package bankaccount.domain.siterep;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DV {
    @JsonProperty("dataDate")
    private String dataDate;
    @JsonProperty("Location")
    private Location Location;
}
