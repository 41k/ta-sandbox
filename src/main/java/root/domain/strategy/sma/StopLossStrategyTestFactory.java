package root.domain.strategy.sma;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.trading.rules.CrossedDownIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import root.domain.level.StopLossLevelProvider;

import java.util.Optional;

//    Given 3 SMA (short, medium, long), e.g.:
//    * SMA(7) - shortSma,
//    * SMA(25) - mediumSma,
//    * SMA(100) - longSma

public class StopLossStrategyTestFactory extends AbstractSmaStrategyFactory
{
    private static final double ACCEPTED_LOSS_VALUE = 10d;

    private final StopLossLevelProvider stopLossLevelProvider;

    public StopLossStrategyTestFactory(String strategyId, BarSeries series, int shortSmaLength, int mediumSmaLength, int longSmaLength)
    {
        super(strategyId, series, shortSmaLength, mediumSmaLength, longSmaLength);
        this.stopLossLevelProvider = new StopLossLevelProvider(series, ACCEPTED_LOSS_VALUE);
    }

    @Override
    public Strategy create()
    {
        Rule entryRule = // Buy rule:
                // (shortSma crosses down mediumSma)
                new CrossedDownIndicatorRule(shortSmaIndicator, mediumSmaIndicator);

        Rule exitRule = // Sell rule:
                // (shortSma crosses up mediumSma)
                new CrossedUpIndicatorRule(shortSmaIndicator, mediumSmaIndicator);

        return new BaseStrategy(strategyId, entryRule, exitRule);
    }

    @Override
    public Optional<StopLossLevelProvider> getStopLossLevelProvider()
    {
        return Optional.of(stopLossLevelProvider);
    }
}

//    Series-1 [ohlcvt-1m-1.csv] results:

//
//    Series-2 [ohlcvt-1m-2.csv] results:

//
//    Series-3 [ohlcvt-1m-3.csv] results:
