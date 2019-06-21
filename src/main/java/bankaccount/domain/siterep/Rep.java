package bankaccount.domain.siterep;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rep implements Serializable {
    @JsonProperty("Wh")
    private String Wh;
    @JsonProperty("Wp")
    private String Wp;
    @JsonProperty("$")
    private String a;

    public Rep copy() {
        return new Rep(Wh, Wp, a);
    }
}
