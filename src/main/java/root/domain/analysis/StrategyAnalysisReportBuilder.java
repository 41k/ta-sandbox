package root.domain.analysis;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Trade;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.PrecisionNum;
import root.domain.strategy.StrategyFactory;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class StrategyAnalysisReportBuilder
{
    public StrategyAnalysisReport build(List<Trade> trades, BarSeries series, StrategyFactory strategyFactory)
    {
        var totalProfit = calculateTotalProfit(trades);
        var nProfitableTrades = calculateNumberOfProfitableTrades(trades);
        var nUnprofitableTrades = calculateNumberOfUnprofitableTrades(trades);
        var riskRewardRatio = nUnprofitableTrades / (double) nProfitableTrades;
        var tradeHistoryItems = buildTradeHistoryItems(trades, series, strategyFactory);
        return StrategyAnalysisReport.builder()
                .strategyId(strategyFactory.getStrategyId())
                .totalProfit(totalProfit)
                .nProfitableTrades(nProfitableTrades)
                .nUnprofitableTrades(nUnprofitableTrades)
                .riskRewardRatio(riskRewardRatio)
                .trades(tradeHistoryItems)
                .build();
    }

    private List<TradeHistoryItem> buildTradeHistoryItems(List<Trade> trades, BarSeries series, StrategyFactory strategyFactory)
    {
        var tradeHistoryItemBuilder = new TradeHistoryItemBuilder();
        return trades.stream()
                .map(trade -> tradeHistoryItemBuilder.build(trade, series, strategyFactory))
                .collect(toList());
    }

    private Double calculateTotalProfit(List<Trade> trades)
    {
        return trades.stream()
                .map(Trade::getProfit)
                .reduce(PrecisionNum.valueOf(0), Num::plus)
                .doubleValue();
    }

    private long calculateNumberOfProfitableTrades(List<Trade> trades)
    {
        return trades.stream()
                .filter(trade -> trade.getProfit().isPositive())
                .count();
    }

    private long calculateNumberOfUnprofitableTrades(List<Trade> trades)
    {
        return trades.stream()
                .filter(trade -> trade.getProfit().isNegative())
                .count();
    }
}
