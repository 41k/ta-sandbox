package root.domain.strategy.sma;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.trading.rules.BooleanIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;
import root.domain.indicator.trend.UpTrendIndicator;

//    Given 3 SMA (short, medium, long), e.g.:
//    * SMA(7) - shortSma,
//    * SMA(25) - mediumSma,
//    * SMA(100) - longSma
//
//    Buy rule:
//        (shortSma < mediumSma < longSma)
//        AND
//        (closePrice crosses up shortSma)
//        AND
//        (closePrice < mediumSma)
//        AND
//        (closePrice < longSma)
//        AND
//        (longSma is in upTrend(70, 0.4))
//
//    Sell rule:
//        (closePrice crosses up mediumSma)

public class SmaStrategy1BFactory extends AbstractSmaStrategyFactory
{
    public SmaStrategy1BFactory(String strategyId, BarSeries series, int shortSmaLength, int mediumSmaLength, int longSmaLength)
    {
        super(strategyId, series, shortSmaLength, mediumSmaLength, longSmaLength);
    }

    @Override
    public Strategy create()
    {
        UpTrendIndicator longSmaUpTrendIndicator = new UpTrendIndicator(longSmaIndicator, 70, 0.4);

        Rule entryRule = // Buy rule:
                // (shortSma < mediumSma < longSma)
                new UnderIndicatorRule(shortSmaIndicator, mediumSmaIndicator)
                .and(new UnderIndicatorRule(mediumSmaIndicator, longSmaIndicator))
                // AND
                // (closePrice crosses up shortSma)
                .and(new CrossedUpIndicatorRule(closePriceIndicator, shortSmaIndicator))
                // AND
                // (closePrice < mediumSma)
                .and(new UnderIndicatorRule(closePriceIndicator, mediumSmaIndicator))
                // AND
                // (closePrice < longSma)
                .and(new UnderIndicatorRule(closePriceIndicator, longSmaIndicator))
                // AND
                // (longSma is in upTrend(70, 0.4))
                .and(new BooleanIndicatorRule(longSmaUpTrendIndicator));

        Rule exitRule = // Sell rule:
                // (closePrice crosses up mediumSma)
                new CrossedUpIndicatorRule(closePriceIndicator, mediumSmaIndicator);

        return new BaseStrategy(strategyId, entryRule, exitRule);
    }
}

//    Series-1 [ohlcvt-1m-1.csv] results:
//        Total profit: 0
//        N trades: 0
//        N profitable trades (UP): 0
//        N unprofitable trades (DOWN): 0
//        Risk/Reward ratio: NaN
//
//    Series-2 [ohlcvt-1m-2.csv] results:
//        Total profit: 60.73
//        N trades: 6
//        N profitable trades (UP): 5
//        N unprofitable trades (DOWN): 1
//        Risk/Reward ratio: 0.2
//
//    Series-3 [ohlcvt-1m-3.csv] results:
//        Total profit: 120.14
//        N trades: 15
//        N profitable trades (UP): 14
//        N unprofitable trades (DOWN): 1
//        Risk/Reward ratio: 0.07142857142857142
