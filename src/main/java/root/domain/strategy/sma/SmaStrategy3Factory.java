package root.domain.strategy.sma;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;

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
//
//    Sell rule:
//        (closePrice crosses up longSma)

public class SmaStrategy3Factory extends AbstractSmaStrategyFactory
{
    public SmaStrategy3Factory(String strategyId, BarSeries series, int shortSmaLength, int mediumSmaLength, int longSmaLength)
    {
        super(strategyId, series, shortSmaLength, mediumSmaLength, longSmaLength);
    }

    @Override
    public Strategy create()
    {
        Rule entryRule = // Buy rule:
                // (shortSma < mediumSma < longSma)
                new UnderIndicatorRule(shortSmaIndicator, mediumSmaIndicator)
                .and(new UnderIndicatorRule(mediumSmaIndicator, longSmaIndicator))
                // AND
                // (closePrice crosses up mediumSma)
                .and(new CrossedUpIndicatorRule(closePriceIndicator, mediumSmaIndicator))
                // AND
                // (closePrice < longSma)
                .and(new UnderIndicatorRule(closePriceIndicator, longSmaIndicator));

        Rule exitRule = // Sell rule:
                // (closePrice crosses up longSma)
                new CrossedUpIndicatorRule(closePriceIndicator, longSmaIndicator);

        return new BaseStrategy(strategyId, entryRule, exitRule);
    }
}

//    Series-1 [ohlcvt-1m-1.csv] results:
//        Total profit: 61.09
//        N trades: 15
//        N profitable trades (UP): 13
//        N unprofitable trades (DOWN): 2
//        Risk/Reward ratio: 0.15384615384615385
//
//    Series-2 [ohlcvt-1m-2.csv] results:
//        Total profit: 37.84
//        N trades: 68
//        N profitable trades (UP): 53
//        N unprofitable trades (DOWN): 15
//        Risk/Reward ratio: 0.2830188679245283
//
//    Series-3 [ohlcvt-1m-3.csv] results:
//        Total profit: 459.9
//        N trades: 58
//        N profitable trades (UP): 47
//        N unprofitable trades (DOWN): 11
//        Risk/Reward ratio: 0.23404255319148937
