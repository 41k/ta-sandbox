package root.domain.strategy.sma;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.trading.rules.CrossedDownIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;

//    Given 3 SMA (short, medium, long), e.g.:
//    * SMA(7) - shortSma,
//    * SMA(25) - mediumSma,
//    * SMA(100) - longSma
//
//    Buy rule:
//        (shortSma crosses down mediumSma)
//
//    Sell rule:
//        (closePrise crosses up mediumSma)

public class SmaStrategy5Factory extends AbstractSmaStrategyFactory
{
    public SmaStrategy5Factory(String strategyId, BarSeries series, int shortSmaLength, int mediumSmaLength, int longSmaLength)
    {
        super(strategyId, series, shortSmaLength, mediumSmaLength, longSmaLength);
    }

    @Override
    public Strategy create()
    {
        Rule entryRule = // Buy rule:
                // (shortSma crosses down mediumSma)
                new CrossedDownIndicatorRule(shortSmaIndicator, mediumSmaIndicator);

        Rule exitRule = // Sell rule:
                // (closePrise crosses up mediumSma)
                new CrossedUpIndicatorRule(closePriceIndicator, mediumSmaIndicator);

        return new BaseStrategy(strategyId, entryRule, exitRule);
    }
}

//    Series-1 [ohlcvt-1m-1.csv] results:
//        Total profit: 276.65
//        N trades: 48
//        N profitable trades (UP): 39
//        N unprofitable trades (DOWN): 9
//        Risk/Reward ratio: 0.23076923076923078
//
//    Series-2 [ohlcvt-1m-2.csv] results:
//        Total profit: 268.14
//        N trades: 201
//        N profitable trades (UP): 146
//        N unprofitable trades (DOWN): 55
//        Risk/Reward ratio: 0.3767123287671233
//
//    Series-3 [ohlcvt-1m-3.csv] results:
//        Total profit: 424.77
//        N trades: 175
//        N profitable trades (UP): 123
//        N unprofitable trades (DOWN): 52
//        Risk/Reward ratio: 0.42276422764227645

