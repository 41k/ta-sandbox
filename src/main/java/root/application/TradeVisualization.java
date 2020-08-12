package root.application;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class TradeVisualization
{
    List<Tick> ticks;
    Double profit;
}
