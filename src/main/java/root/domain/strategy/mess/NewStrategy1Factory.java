package root.domain.strategy.mess;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.helpers.*;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;
import root.domain.indicator.NumberIndicator;
import root.domain.level.MainChartLevelProvider;
import root.domain.rule.StopLossLevelRule;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;

import static root.domain.ChartType.MAIN;
import static root.domain.indicator.NumberIndicators.*;

public class NewStrategy1Factory extends AbstractStrategyFactory
{
    private final HighPriceIndicator highPrice;
    private final LowPriceIndicator lowPrice;
    private final ClosePriceIndicator closePrice;
    private final NumberIndicator trendLine;
    private final NumberIndicator ema;
    private final NumberIndicator ema30;
    private final NumberIndicator ema60;
    private final NumberIndicator ema100;
    private final NumberIndicator sma;
    private final NumberIndicator adx;
    private final NumberIndicator adxLevel;
    private final NumberIndicator wr;
    private final NumberIndicator wrLevelMinus10;
    private final NumberIndicator wrLevelMinus90;
    private final NumberIndicator highest;
    private final NumberIndicator lowest;
    private final NumberIndicator diff;
    private final List<NumberIndicator> numberIndicators;

    private final MainChartLevelProvider stopLossLevelProvider;
    private final List<MainChartLevelProvider> mainChartLevelProviders;

    public NewStrategy1Factory(String strategyId, BarSeries series)
    {
        super(strategyId, series);
        this.highPrice = new HighPriceIndicator(series);
        this.lowPrice = new LowPriceIndicator(series);
        this.closePrice = new ClosePriceIndicator(series);
        this.trendLine = trendLine(series, true);
        this.ema30 = ema(closePrice, 30);
        this.ema60 = ema(closePrice, 60);
        this.ema100 = ema(closePrice, 100);
        this.ema = ema(closePrice, 150);
        this.sma = sma(closePrice, 150);
        this.adx = adx(series, 20, 20);
        this.adxLevel = adxLevel(25, series);
        this.wr = williamsR(14, series);
        this.wrLevelMinus10 = williamsRLevel(-10, series);
        this.wrLevelMinus90 = williamsRLevel(-90, series);
        this.highest = NumberIndicator.builder().name("H").chartType(MAIN).indicator(new HighestValueIndicator(highPrice, 50)).build();
        this.lowest = NumberIndicator.builder().name("L").chartType(MAIN).indicator(new LowestValueIndicator(lowPrice, 50)).build();
        this.diff = NumberIndicator.builder().name("HL-diff").chartType(MAIN).indicator(new DifferenceIndicator(highest, lowest)).build();
        this.numberIndicators = List.of(trendLine);
        this.stopLossLevelProvider = new MainChartLevelProvider("SL", this::calculateStopLossLevel);
        this.mainChartLevelProviders = List.of(stopLossLevelProvider);
    }

    @Override
    public Strategy create()
    {
        var entryRule = new OverIndicatorRule(trendLine, new PreviousValueIndicator(trendLine));
        var exitRule = new UnderIndicatorRule(trendLine, new PreviousValueIndicator(trendLine));
//        var exitRule = new UnderIndicatorRule(trendLine, new PreviousValueIndicator(trendLine))
//                .or(new StopLossLevelRule(lowPrice, stopLossLevelProvider));
//        var entryRule = new OverIndicatorRule(trendLine, new PreviousValueIndicator(trendLine))
//                .and(new UnderIndicatorRule(closePrice, ema));
//        var exitRule = new OverIndicatorRule(closePrice, ema);
        return new BaseStrategy(strategyId, entryRule, exitRule);
    }

    @Override
    public List<NumberIndicator> getNumberIndicators()
    {
        return numberIndicators;
    }

//    @Override
//    public List<MainChartLevelProvider> getMainChartLevelProviders()
//    {
//        return mainChartLevelProviders;
//    }

    private Double calculateStopLossLevel(Integer entryIndex)
    {
        return lowPrice.getValue(entryIndex).doubleValue();
    }
}

// TF:5m A:1 ETH/USD
//
//    data-set-1:
//
//    Total profit:	338.1499999999997
//    Average profit per trade:	24.153571428571407
//    N trades:	14
//    N profitable trades (UP):	12
//    N unprofitable trades (DOWN):	2
//    Risk/Reward ratio:	0.17
//
//    data-set-2
//
//    Total profit:	389.2
//    Average profit per trade:	43.24444444444444
//    N trades:	9
//    N profitable trades (UP):	8
//    N unprofitable trades (DOWN):	1
//    Risk/Reward ratio:	0.13