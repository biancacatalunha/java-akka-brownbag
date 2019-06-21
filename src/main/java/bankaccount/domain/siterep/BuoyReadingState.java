package bankaccount.domain.siterep;

import lombok.Data;

import java.util.List;

@Data
public class BuoyReadingState {

    private List<BuoyReading> buoyReadings;
    private String lastReadingAt;

    public BuoyReadingState(List<BuoyReading> buoyReadings, String lastReadingAt) {
        this.buoyReadings = buoyReadings;
        this.lastReadingAt = lastReadingAt;
    }

    public void apply(BuoyReading buoyReading) {
        buoyReadings.add(buoyReading);
        lastReadingAt = buoyReading.getSiteRep().getDV().getDataDate();
    }

    public int getSize(){
        return buoyReadings.size();
    }
}
