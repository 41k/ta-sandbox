package root.domain.strategy.level;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.helpers.*;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;
import root.domain.indicator.pivot_points.FibonacciRetracementLevelIndicator;
import root.domain.level.MainChartLevelProvider;
import root.domain.rule.OverMainChartLevelRule;
import root.domain.rule.UnderMainChartLevelRule;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;

import static root.domain.indicator.TrendType.BULLISH;

public class FibonacciRetracementLevelsStrategyFactory extends AbstractStrategyFactory
{
    private static final int WINDOW_SIZE = 100;
    private static final int PRICE_DIFFERENCE_THRESHOLD = 60;

    private final ClosePriceIndicator closePrice;
    private final DifferenceIndicator priceDifferenceIndicator;

    // Warning: Do not use this FibonacciRetracementLevelIndicator with CrossedUpIndicatorRule/CrossedDownIndicatorRule
    private final FibonacciRetracementLevelIndicator fib_0;
    private final FibonacciRetracementLevelIndicator fib_0_382;
    private final FibonacciRetracementLevelIndicator fib_0_5;
    private final FibonacciRetracementLevelIndicator fib_0_618;
    private final FibonacciRetracementLevelIndicator fib_1;

    private final MainChartLevelProvider fib_0_Level;
    private final MainChartLevelProvider fib_0_382_Level;
    private final MainChartLevelProvider fib_0_5_Level;
    private final MainChartLevelProvider fib_0_618_Level;
    private final MainChartLevelProvider fib_1_Level;
    private final List<MainChartLevelProvider> mainChartLevelProviders;

    public FibonacciRetracementLevelsStrategyFactory(String strategyId, BarSeries series)
    {
        super(strategyId, series);
        this.closePrice = new ClosePriceIndicator(series);
        var highestPriceIndicator = new HighestValueIndicator(closePrice, WINDOW_SIZE);
        var lowestPriceIndicator = new LowestValueIndicator(closePrice, WINDOW_SIZE);
        this.priceDifferenceIndicator = new DifferenceIndicator(highestPriceIndicator, lowestPriceIndicator);

        this.fib_0 = new FibonacciRetracementLevelIndicator(0, BULLISH, closePrice, WINDOW_SIZE);
        this.fib_0_382 = new FibonacciRetracementLevelIndicator(0.382, BULLISH, closePrice, WINDOW_SIZE);
        this.fib_0_5 = new FibonacciRetracementLevelIndicator(0.5, BULLISH, closePrice, WINDOW_SIZE);
        this.fib_0_618 = new FibonacciRetracementLevelIndicator(0.618, BULLISH, closePrice, WINDOW_SIZE);
        this.fib_1 = new FibonacciRetracementLevelIndicator(1, BULLISH, closePrice, WINDOW_SIZE);

        this.fib_0_Level = new MainChartLevelProvider("[0] -- ", entryIndex -> fib_0.getValue(entryIndex).doubleValue());
        this.fib_0_382_Level = new MainChartLevelProvider("[0.382] -- ", entryIndex -> fib_0_382.getValue(entryIndex).doubleValue());
        this.fib_0_5_Level = new MainChartLevelProvider("[0.5] -- ", entryIndex -> fib_0_5.getValue(entryIndex).doubleValue());
        this.fib_0_618_Level = new MainChartLevelProvider("[0.618] -- ", entryIndex -> fib_0_618.getValue(entryIndex).doubleValue());
        this.fib_1_Level = new MainChartLevelProvider("[1] -- ", entryIndex -> fib_1.getValue(entryIndex).doubleValue());
        this.mainChartLevelProviders = List.of(fib_0_Level, fib_0_382_Level, fib_0_5_Level, fib_0_618_Level, fib_1_Level);
    }

    @Override
    public Strategy create()
    {
        var entryRule = new OverIndicatorRule(priceDifferenceIndicator, PRICE_DIFFERENCE_THRESHOLD)
                .and(new UnderIndicatorRule(new PreviousValueIndicator(closePrice), new PreviousValueIndicator(fib_0_5)))
                .and(new OverIndicatorRule(closePrice, fib_0_5));
        var exitRule = new OverMainChartLevelRule(closePrice, fib_0_Level)
                .or(new UnderMainChartLevelRule(closePrice, fib_1_Level));
        return new BaseStrategy(strategyId, entryRule, exitRule);
    }

    @Override
    public List<MainChartLevelProvider> getMainChartLevelProviders()
    {
        return mainChartLevelProviders;
    }
}