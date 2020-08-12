package root.domain.indicator.sri;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.ta4j.core.Bar;
import org.ta4j.core.num.Num;

import java.util.List;

@Value
@Builder
@NonNull
public class SRZoneSettings
{
    List<Bar> series;
    Integer calculationWindowSize;
    Integer segmentSize;
    Num zoneHeight;
    Integer minStrength;
}
