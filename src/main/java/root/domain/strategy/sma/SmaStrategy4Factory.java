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
//        (longSma < shortSma < mediumSma)
//        AND
//        (strongBullishBar(2) crosses up shortSma)
//        AND
//        (closePrice < mediumSma)

//    Sell rule:
//        [(closePrice crosses up mediumSma)
//        AND
//        (longSma < mediumSma)]
//        OR
//        [(mediumSma < longSma)
//        AND
//        (shortSma crosses up longSma)]

public class SmaStrategy4Factory extends AbstractSmaStrategyFactory
{
    public SmaStrategy4Factory(String strategyId, BarSeries series, int shortSmaLength, int mediumSmaLength, int longSmaLength)
    {
        super(strategyId, series, shortSmaLength, mediumSmaLength, longSmaLength);
    }

    @Override
    public Strategy create()
    {
        StrongBarIndicator strongBullishBarIndicator = new StrongBarIndicator(BULLISH, Set.of(BULLISH, BEARISH), 2, series);

        Rule entryRule = // Buy rule
                // (longSma < shortSma < mediumSma)
                new UnderIndicatorRule(longSmaIndicator, shortSmaIndicator)
                .and(new UnderIndicatorRule(shortSmaIndicator, mediumSmaIndicator))
                // AND
                // (strongBullishBar(2) crosses up shortSma)
                .and(new CrossedUpIndicatorRule(closePriceIndicator, shortSmaIndicator))
                .and(new BooleanIndicatorRule(strongBullishBarIndicator))
                // AND
                // (closePrice < mediumSma)
                .and(new UnderIndicatorRule(closePriceIndicator, mediumSmaIndicator));

        Rule exitRule = // Sell rule
                // ((closePrice crosses up mediumSma)
                new CrossedUpIndicatorRule(closePriceIndicator, mediumSmaIndicator)
                // AND
                // (longSma < mediumSma))
                .and(new UnderIndicatorRule(longSmaIndicator, mediumSmaIndicator))
                //
                // OR
                //
                // ((mediumSma < longSma)
                .or(new UnderIndicatorRule(mediumSmaIndicator, longSmaIndicator)
                // AND
                // (shortSma crosses up longSma))
                .and(new CrossedUpIndicatorRule(shortSmaIndicator, longSmaIndicator)));

        return new BaseStrategy(strategyId, entryRule, exitRule);
    }
}

//    Series-1 [ohlcvt-1m-1.csv] results:
//        Total profit: 46.29
//        N trades: 6
//        N profitable trades (UP): 6
//        N unprofitable trades (DOWN): 0
//        Risk/Reward ratio: 0
//
//    Series-2 [ohlcvt-1m-2.csv] results:
//        Total profit: 93.76
//        N trades: 29
//        N profitable trades (UP): 25
//        N unprofitable trades (DOWN): 4
//        Risk/Reward ratio: 0.16
