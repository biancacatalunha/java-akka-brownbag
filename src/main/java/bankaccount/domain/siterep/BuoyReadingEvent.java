package bankaccount.domain.siterep;


import lombok.AllArgsConstructor;

public class BuoyReadingEvent extends BuoyReading {

    public BuoyReadingEvent(SiteRep siteRep) {
        super(siteRep);
    }
}
