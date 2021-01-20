package root.infrastructure.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.PrecisionNum;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.function.Function;

@Entity
@Table(name = "bar")
@Data
@Builder
@Access(AccessType.FIELD)
@NoArgsConstructor
@AllArgsConstructor
public class BarDbEntry
{
    private static final double AMOUNT = 0d;
    private static final int TRADES = 0;
    private static final Function<Number, Num> NUM_FUNCTION = PrecisionNum::valueOf;

    @Id
    private Long id;
    @NotNull
    private String exchangeGateway;
    @NotNull
    private String symbol;
    @NotNull
    @Column(name = "time_interval")
    private String interval;
    @NotNull
    private Long duration;
    @NotNull
    private Long timestamp;
    @NotNull
    private Double open;
    @NotNull
    private Double high;
    @NotNull
    private Double low;
    @NotNull
    private Double close;
    @NotNull
    private Double volume;

    public Bar toDomainObject()
    {
        var barDuration = Duration.ofMillis(duration);
        var barTime = Instant.ofEpochMilli(timestamp);
        var zonedBarTime = ZonedDateTime.ofInstant(barTime, ZoneId.systemDefault());
        return new BaseBar(barDuration, zonedBarTime, open, high, low, close, volume, AMOUNT, TRADES, NUM_FUNCTION);
    }
}
