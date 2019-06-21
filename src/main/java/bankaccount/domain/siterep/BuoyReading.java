package bankaccount.domain.siterep;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class BuoyReading implements Serializable {
    @JsonProperty("SiteRep")
    private SiteRep SiteRep;

    BuoyReading(SiteRep siteRep) {
        this.SiteRep = siteRep;
    }

   public BuoyReadingEvent toEvent() {
       return new BuoyReadingEvent(SiteRep);
    }
}


