package bankaccount.domain.siterep;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DV implements Serializable {
    @JsonProperty("dataDate")
    private String dataDate;
    @JsonProperty("Location")
    private Location Location;

    public DV copy() {
        return new DV(dataDate, Location);
    }
}
