package bankaccount.domain.siterep;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SiteRep implements Serializable {
    @JsonProperty("DV")
    private DV DV;

    public SiteRep copy() {
        return new SiteRep(DV);
    }
}
