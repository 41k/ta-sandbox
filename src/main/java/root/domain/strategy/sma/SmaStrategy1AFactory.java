package root.domain.strategy.sma;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.trading.rules.BooleanIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;
import root.domain.indicator.bar.StrongBarIndicator;

import java.util.Set;

import static root.domain.indicator.bar.BarType.BEARISH;
import static root.domain.indicator.bar.BarType.BULLISH;

//    Given 3 SMA (short, medium, long), e.g.:
//    * SMA(7) - shortSma,
//    * SMA(25) - mediumSma,
//    * SMA(100) - longSma
//
//    Buy rule:
//        (shortSma < mediumSma < longSma)
//        AND
//        (strongBullishBar(2) crosses up shortSma)
//        AND
//        (closePrice < mediumSma)
//        AND
//        (closePrice < longSma)
//
//    Sell rule:
//        (closePrice crosses up mediumSma)

public class SmaStrategy1AFactory extends AbstractSmaStrategyFactory
{
    public SmaStrategy1AFactory(String strategyId, BarSeries series, int shortSmaLength, int mediumSmaLength, int longSmaLength)
    {
        super(strategyId, series, shortSmaLength, mediumSmaLength, longSmaLength);
    }

    @Override
    public Strategy create()
    {
        StrongBarIndicator strongBullishBarIndicator = new StrongBarIndicator(BULLISH, Set.of(BULLISH, BEARISH), 2, series);

        Rule entryRule = // Buy rule:
                // (shortSma < mediumSma < longSma)
                new UnderIndicatorRule(shortSmaIndicator, mediumSmaIndicator)
                .and(new UnderIndicatorRule(mediumSmaIndicator, longSmaIndicator))
                // AND
                // (strongBullishBar(2) crosses up shortSma)
                .and(new BooleanIndicatorRule(strongBullishBarIndicator))
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
//        Total profit: 96.85
//        N trades: 19
//        N profitable trades (UP): 14
//        N unprofitable trades (DOWN): 5
//        Risk/Reward ratio: 0.35714285714285715
//
//    Series-2 [ohlcvt-1m-2.csv] results:
//        Total profit: 137.18
//        N trades: 77
//        N profitable trades (UP): 58
//        N unprofitable trades (DOWN): 19
//        Risk/Reward ratio: 0.3275862068965517
