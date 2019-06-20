package bankaccount.domain.siterep;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Location {
    @JsonProperty("i")
    private Integer i;
    @JsonProperty("name")
    private String name;
    @JsonProperty("Period")
    private List<Period> Period;
}
