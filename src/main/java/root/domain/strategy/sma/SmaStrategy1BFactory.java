package root.domain.strategy.sma;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;
import root.domain.indicator.RSIIndicator;
import root.domain.indicator.RSILevelIndicator;

import java.util.ArrayList;

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
//
//    Sell rule:
//        (closePrice crosses up mediumSma)

public class SmaStrategy1BFactory extends AbstractSmaStrategyFactory
{
    public SmaStrategy1BFactory(String strategyId, BarSeries series, int shortSmaLength, int mediumSmaLength, int longSmaLength)
    {
        super(strategyId, series, shortSmaLength, mediumSmaLength, longSmaLength);
        numIndicators.add(new RSIIndicator(closePriceIndicator, 12));
        numIndicators.add(new RSILevelIndicator(series, series.numOf(70)));
        numIndicators.add(new RSILevelIndicator(series, series.numOf(50)));
        numIndicators.add(new RSILevelIndicator(series, series.numOf(30)));
    }

    @Override
    public Strategy create()
    {
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
                .and(new UnderIndicatorRule(closePriceIndicator, longSmaIndicator));

        Rule exitRule = // Sell rule:
                // (closePrice crosses up mediumSma)
                new CrossedUpIndicatorRule(closePriceIndicator, mediumSmaIndicator);

        return new BaseStrategy(strategyId, entryRule, exitRule);
    }
}

//    Series-1 [ohlcvt-1m-1.csv] results:
//        Total profit: 156.79
//        N trades: 30
//        N profitable trades (UP): 23
//        N unprofitable trades (DOWN): 7
//        Risk/Reward ratio: 0.30434782608695654
//
//    Series-2 [ohlcvt-1m-2.csv] results:
//        Total profit: 127.76
//        N trades: 120
//        N profitable trades (UP): 88
//        N unprofitable trades (DOWN): 32
//        Risk/Reward ratio: 0.36363636363636365
