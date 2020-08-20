package root.application.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.ta4j.core.Order;

import java.util.Map;

@Data
@Builder(toBuilder = true)
public class Tick
{
    @NonNull
    Double open;
    @NonNull
    Double high;
    @NonNull
    Double low;
    @NonNull
    Double close;
    @NonNull
    Double volume;
    @NonNull
    Long timestamp;
    Order.OrderType signal;
    Map<String, Double> indicators;
    Map<String, Double> metadata;
}