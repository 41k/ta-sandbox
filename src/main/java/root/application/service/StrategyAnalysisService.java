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
import root.domain.strategy.doji.DojiStrategy1Factory;
import root.domain.strategy.rsi_sma.RsiSmaStrategy1Factory;
import root.domain.strategy.rsi_sma.RsiSmaStrategy2Factory;
import root.domain.strategy.sma.SmaStrategy4Factory;
import root.domain.strategy.sma.SmaStrategy5AFactory;
import root.domain.strategy.sma.SmaStrategy5Factory;
import root.domain.strategy.sma.StopLossStrategyTestFactory;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class StrategyAnalysisService
{
    private final BarProvider barProvider;
    private final TradeVisualizationBuilder tradeVisualizationBuilder;

    public StrategyAnalysisReport analyse()
    {
        var bars = barProvider.getMinuteBars();
        var series = new BaseBarSeries(bars);
        var strategyFactory = new StopLossStrategyTestFactory("SMA", series, 7, 25, 100);
        //var strategyFactory = new DojiStrategy1Factory("DOJI", series);
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
                .limit(5)
                .collect(Collectors.toList());
    }

    private List<Double> getListOfNSignificantDowns(List<Trade> trades)
    {
        return trades.stream()
                .map(Trade::getProfit)
                .filter(Num::isNegative)
                .mapToDouble(Num::doubleValue)
                .sorted()
                .limit(5)
                .boxed()
                .collect(Collectors.toList());
    }
}
