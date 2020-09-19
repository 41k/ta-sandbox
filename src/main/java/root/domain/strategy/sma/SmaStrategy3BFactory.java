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
//        (closePrice crosses up mediumSma)
//        AND
//        (closePrice < longSma)
//        AND
//        (longSma is in upTrend(70, 0.2))
//
//    Sell rule:
//        (closePrice crosses up longSma)

public class SmaStrategy3BFactory extends AbstractSmaStrategyFactory
{
    public SmaStrategy3BFactory(String strategyId, BarSeries series, int shortSmaLength, int mediumSmaLength, int longSmaLength)
    {
        super(strategyId, series, shortSmaLength, mediumSmaLength, longSmaLength);
    }

    @Override
    public Strategy create()
    {
        UpTrendIndicator longSmaUpTrendIndicator = new UpTrendIndicator(longSmaIndicator, 70, 0.2);

        Rule entryRule = // Buy rule:
                // (shortSma < mediumSma < longSma)
                new UnderIndicatorRule(shortSmaIndicator, mediumSmaIndicator)
                .and(new UnderIndicatorRule(mediumSmaIndicator, longSmaIndicator))
                // AND
                // (closePrice crosses up mediumSma)
                .and(new CrossedUpIndicatorRule(closePriceIndicator, mediumSmaIndicator))
                // AND
                // (closePrice < longSma)
                .and(new UnderIndicatorRule(closePriceIndicator, longSmaIndicator))
                // AND
                // (longSma is in upTrend(70, 0.2))
                .and(new BooleanIndicatorRule(longSmaUpTrendIndicator));

        Rule exitRule = // Sell rule:
                // (closePrice crosses up longSma)
                new CrossedUpIndicatorRule(closePriceIndicator, longSmaIndicator);

        return new BaseStrategy(strategyId, entryRule, exitRule);
    }
}

//    Series-1 [ohlcvt-1m-1.csv] results:
//        Total profit: 17.56
//        N trades: 5
//        N profitable trades (UP): 4
//        N unprofitable trades (DOWN): 1
//        Risk/Reward ratio: 0.25
//
//    Series-2 [ohlcvt-1m-2.csv] results:
//        Total profit: 122.32
//        N trades: 11
//        N profitable trades (UP): 11
//        N unprofitable trades (DOWN): 0
//        Risk/Reward ratio: 0
//
//    Series-3 [ohlcvt-1m-3.csv] results:
//        Total profit: 252.71
//        N trades: 15
//        N profitable trades (UP): 13
//        N unprofitable trades (DOWN): 2
//        Risk/Reward ratio: 0.15384615384615385
