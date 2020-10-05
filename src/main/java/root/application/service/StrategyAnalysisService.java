package root.application.service;

import lombok.RequiredArgsConstructor;
import org.ta4j.core.BarSeriesManager;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.Trade;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.PrecisionNum;
import root.application.BarProvider;
import root.application.TradeVisualizationBuilder;
import root.application.model.StrategyAnalysisReport;
import root.domain.strategy.rsi.RsiStrategy1Factory;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class StrategyAnalysisService
{
    private final BarProvider barProvider;
    private final TradeVisualizationBuilder tradeVisualizationBuilder;
    private final int nSignificantTrades;

    public StrategyAnalysisReport analyse()
    {
        var bars = barProvider.getMinuteBars();
        var series = new BaseBarSeries(bars);
        //var strategyFactory = new SmaStrategy5AFactory("SMA", series, 7, 25, 100);
        var strategyFactory = new RsiStrategy1Factory("RSI", series);
        var strategy = strategyFactory.create();
        var seriesManager = new BarSeriesManager(series);
        var tradingRecord = seriesManager.run(strategy);
        var trades = tradingRecord.getTrades();

        var tradesVisualisation = tradeVisualizationBuilder.build(trades, series, strategyFactory);
        var totalProfit = calculateTotalProfit(trades);
        var nProfitableTrades = calculateNumberOfProfitableTrades(trades);
        var nUnprofitableTrades = calculateNumberOfUnprofitableTrades(trades);
        var riskRewardRatio = nUnprofitableTrades / (double) nProfitableTrades;
        var listOfNSignificantUps = getListOfNSignificantUps(trades);
        var listOfNSignificantDowns = getListOfNSignificantDowns(trades);
        return StrategyAnalysisReport.builder()
                .trades(tradesVisualisation)
                .totalProfit(totalProfit)
                .nProfitableTrades(nProfitableTrades)
                .nUnprofitableTrades(nUnprofitableTrades)
                .riskRewardRatio(riskRewardRatio)
                .listOfNSignificantUps(listOfNSignificantUps)
                .listOfNSignificantDowns(listOfNSignificantDowns)
                .build();
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

    private List<Double> getListOfNSignificantUps(List<Trade> trades)
    {
        return trades.stream()
                .map(Trade::getProfit)
                .filter(Num::isPositive)
                .mapToDouble(Num::doubleValue)
                .boxed()
                .sorted(Comparator.reverseOrder())
                .limit(nSignificantTrades)
                .collect(Collectors.toList());
    }

    private List<Double> getListOfNSignificantDowns(List<Trade> trades)
    {
        return trades.stream()
                .map(Trade::getProfit)
                .filter(Num::isNegative)
                .mapToDouble(Num::doubleValue)
                .sorted()
                .limit(nSignificantTrades)
                .boxed()
                .collect(Collectors.toList());
    }
}
