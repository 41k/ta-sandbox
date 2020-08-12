package root.domain.indicator.sri;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
@NonNull
public class SRISettings
{
    private static final Integer DEFAULT_MIN_ZONE_STRENGTH = 2;

    Integer calculationWindowSize;
    Integer segmentSize;
    Integer zoneHeight;
    @Builder.Default
    Integer minZoneStrength = DEFAULT_MIN_ZONE_STRENGTH;
}
