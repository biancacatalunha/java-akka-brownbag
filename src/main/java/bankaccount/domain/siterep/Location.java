package bankaccount.domain.siterep;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location implements Serializable {
    @JsonProperty("i")
    private Integer i;
    @JsonProperty("name")
    private String name;
    @JsonProperty("Period")
    private List<Period> Period;

   public Location copy() {
       return new Location(i, name, Period);
   }
}
