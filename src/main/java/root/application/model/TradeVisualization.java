package root.application.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class TradeVisualization
{
    List<Tick> ticks;
    String strategyId;
    Integer entryIndex;
    Integer exitIndex;
    Double profit;
}
