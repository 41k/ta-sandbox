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
import root.domain.rule.TakeProfitLevelRule;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;

import static root.domain.ChartType.MAIN;
import static root.domain.indicator.NumberIndicators.*;

public class NewStrategy2Factory extends AbstractStrategyFactory
{
    private final OpenPriceIndicator openPrice;
    private final HighPriceIndicator highPrice;
    private final LowPriceIndicator lowPrice;
    private final ClosePriceIndicator closePrice;
    private final List<NumberIndicator> numberIndicators;

    private final MainChartLevelProvider stopLossLevelProvider;
    private final MainChartLevelProvider takeProfitLevelProvider;
    private final List<MainChartLevelProvider> mainChartLevelProviders;

    public NewStrategy2Factory(String strategyId, BarSeries series)
    {
        super(strategyId, series);
        this.openPrice = new OpenPriceIndicator(series);
        this.highPrice = new HighPriceIndicator(series);
        this.lowPrice = new LowPriceIndicator(series);
        this.closePrice = new ClosePriceIndicator(series);
        this.numberIndicators = List.of();
        this.stopLossLevelProvider = new MainChartLevelProvider("SL", this::calculateStopLossLevel);
        this.takeProfitLevelProvider = new MainChartLevelProvider("SL", this::calculateTakeProfitLevel);
        this.mainChartLevelProviders = List.of(stopLossLevelProvider, takeProfitLevelProvider);
    }

    @Override
    public Strategy create()
    {
        var buyCondition1 = new UnderIndicatorRule(new PreviousValueIndicator(closePrice), new PreviousValueIndicator(openPrice))
                .and(new OverIndicatorRule(closePrice, openPrice));

        var buyCondition2 = new OverIndicatorRule(closePrice, new PreviousValueIndicator(openPrice));

        var buyCondition3 = new UnderIndicatorRule(new LowestValueIndicator(lowPrice, 3), new PreviousValueIndicator(new LowestValueIndicator(lowPrice, 50), 1))
                .or(new UnderIndicatorRule(new LowestValueIndicator(lowPrice, 3), new PreviousValueIndicator(new LowestValueIndicator(lowPrice, 50), 2)))
                .or(new UnderIndicatorRule(new LowestValueIndicator(lowPrice, 3), new PreviousValueIndicator(new LowestValueIndicator(lowPrice, 50), 3)));

        var entryRule = buyCondition1.and(buyCondition2).and(buyCondition3);

        var sellCondition1 = new OverIndicatorRule(new PreviousValueIndicator(closePrice), new PreviousValueIndicator(openPrice))
                .and(new UnderIndicatorRule(closePrice, openPrice));

        var sellCondition2 = new UnderIndicatorRule(closePrice, new PreviousValueIndicator(openPrice));

        var sellCondition3 = new OverIndicatorRule(new HighestValueIndicator(highPrice, 3), new PreviousValueIndicator(new HighestValueIndicator(highPrice, 50), 1))
                .or(new OverIndicatorRule(new HighestValueIndicator(highPrice, 3), new PreviousValueIndicator(new HighestValueIndicator(highPrice, 50), 2)))
                .or(new OverIndicatorRule(new HighestValueIndicator(highPrice, 3), new PreviousValueIndicator(new HighestValueIndicator(highPrice, 50), 3)));

        var exitRule = sellCondition1.and(sellCondition2).and(sellCondition3)
                .or(new StopLossLevelRule(closePrice, stopLossLevelProvider));

        return new BaseStrategy(strategyId, entryRule, exitRule);
    }

    @Override
    public List<NumberIndicator> getNumberIndicators()
    {
        return numberIndicators;
    }

    @Override
    public List<MainChartLevelProvider> getMainChartLevelProviders()
    {
        return mainChartLevelProviders;
    }

    private Double calculateStopLossLevel(Integer entryIndex)
    {
        return closePrice.getValue(entryIndex).doubleValue() - 40;
    }

    private Double calculateTakeProfitLevel(Integer entryIndex)
    {
        return closePrice.getValue(entryIndex).doubleValue() + 50;
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