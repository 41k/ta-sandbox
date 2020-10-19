package root.domain.analysis;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Value
@Builder
public class StrategiesGroupAnalysisReport
{
    @NonNull
    List<StrategyAnalysisReport> strategiesReports;
    @NonNull
    Double totalProfit;
    @NonNull
    Long nTrades;
    @NonNull
    Long nProfitableTrades;
    @NonNull
    Long nUnprofitableTrades;
    @NonNull
    Double riskRewardRatio;
    @NonNull
    Map<String, Set<Long>> tradesIntersectionsReport;
    @NonNull
    Long beginTimestamp;
    @NonNull
    Long endTimestamp;
}
