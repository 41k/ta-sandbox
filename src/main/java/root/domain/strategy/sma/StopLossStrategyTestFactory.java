package root.domain.strategy.sma;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.trading.rules.CrossedDownIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import root.domain.level.StopLossLevelProvider;
import root.domain.rule.StopLossNumberRule;

import java.util.Optional;

//    Given 3 SMA (short, medium, long), e.g.:
//    * SMA(7) - shortSma,
//    * SMA(25) - mediumSma,
//    * SMA(100) - longSma

public class StopLossStrategyTestFactory extends AbstractSmaStrategyFactory
{
    private static final double LOSS_VALUE = 50d;

    private final StopLossLevelProvider stopLossLevelProvider;

    public StopLossStrategyTestFactory(String strategyId, BarSeries series, int shortSmaLength, int mediumSmaLength, int longSmaLength)
    {
        super(strategyId, series, shortSmaLength, mediumSmaLength, longSmaLength);
        this.stopLossLevelProvider = new StopLossLevelProvider(series, LOSS_VALUE);
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
//                .or(new StopLossNumberRule(closePriceIndicator, LOSS_VALUE));

        return new BaseStrategy(strategyId, entryRule, exitRule);
    }

    @Override
    public Optional<StopLossLevelProvider> getStopLossLevelProvider()
    {
        return Optional.of(stopLossLevelProvider);
    }
}

//    Series-1 [ohlcvt-1m-1.csv] results:
//        Total profit: 262.05
//        N trades: 50
//        N profitable trades (UP): 38
//        N unprofitable trades (DOWN): 12
//        Risk/Reward ratio: 0.3157894736842105
//
//    Series-2 [ohlcvt-1m-2.csv] results:
//        Total profit: 358.88
//        N trades: 206
//        N profitable trades (UP): 138
//        N unprofitable trades (DOWN): 68
//        Risk/Reward ratio: 0.4927536231884058
//
//    Series-3 [ohlcvt-1m-3.csv] results:
//        Total profit: 513.25
//        N trades: 182
//        N profitable trades (UP): 120
//        N unprofitable trades (DOWN): 62
//        Risk/Reward ratio: 0.5166666666666667
