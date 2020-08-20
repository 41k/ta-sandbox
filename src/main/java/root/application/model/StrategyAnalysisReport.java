package root.application.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class StrategyAnalysisReport
{
    List<TradeVisualization> trades;
    Double totalProfit;
    Long nProfitableTrades;
    Long nUnprofitableTrades;
    Double riskRewardRation;
}
