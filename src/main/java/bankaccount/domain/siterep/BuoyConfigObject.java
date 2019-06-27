package bankaccount.domain.siterep;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BuoyConfigObject {
    private String key;
    private List<Location> location;
}