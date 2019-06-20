package bankaccount.domain.siterep;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Rep {
    @JsonProperty("Wh")
    private String Wh;
    @JsonProperty("Wp")
    private String Wp;
    @JsonProperty("$")
    private String $;
}
