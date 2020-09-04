package root.domain.strategy.sma;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.trading.rules.BooleanIndicatorRule;
import org.ta4j.core.trading.rules.CrossedDownIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;

//    Given 3 SMA (short, medium, long), e.g.:
//    * SMA(7) - shortSma,
//    * SMA(25) - mediumSma,
//    * SMA(100) - longSma
//
//    Buy rule:
//        (mediumSma < longSma)
//        AND
//        (closePrice crosses up longSma)
//
//    Sell rule:
//        (mediumSma crosses up longSma)

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
                // (mediumSma < longSma)
                new UnderIndicatorRule(mediumSmaIndicator, longSmaIndicator)
                // AND
                // (closePrice crosses up longSma)
                .and(new CrossedUpIndicatorRule(closePriceIndicator, longSmaIndicator));

        Rule exitRule = // Sell rule:
                // (mediumSma crosses up longSma)
                new CrossedUpIndicatorRule(mediumSmaIndicator, longSmaIndicator);

        return new BaseStrategy(strategyId, entryRule, exitRule);
    }
}

//    Series-1 [ohlcvt-1m-1.csv] results:
//        Total profit: 114.24
//        N trades: 11
//        N profitable trades (UP): 7
//        N unprofitable trades (DOWN): 4
//        Risk/Reward ratio: 0.5714285714285714

