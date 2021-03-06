package root.domain.report;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class StrategyAnalysisReport
{
    @NonNull
    String strategyId;
    @NonNull
    Double totalProfit;
    @NonNull
    Double averageProfitPerTrade;
    @NonNull
    Long nProfitableTrades;
    @NonNull
    Long nUnprofitableTrades;
    @NonNull
    Double riskRewardRatio;
    @NonNull
    List<TradeHistoryItem> trades;
}
