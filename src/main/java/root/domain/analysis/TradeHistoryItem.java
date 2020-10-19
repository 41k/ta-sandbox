package root.domain.analysis;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class TradeHistoryItem
{
    @NonNull
    String strategyId;
    @NonNull
    Long entryTimestamp;
    @NonNull
    Long exitTimestamp;
    @NonNull
    Double profit;
    List<Tick> ticks;
}
