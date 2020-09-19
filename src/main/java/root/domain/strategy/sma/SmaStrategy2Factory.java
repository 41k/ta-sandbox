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
//        (strongBullishBar(2) crosses up shortSma and mediumSma)
//        AND
//        (closePrice < longSma)
//
//    Sell rule:
//        (closePrice crosses up longSma)

public class SmaStrategy2Factory extends AbstractSmaStrategyFactory
{
    public SmaStrategy2Factory(String strategyId, BarSeries series, int shortSmaLength, int mediumSmaLength, int longSmaLength)
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
                // (strongBullishBar(2) crosses up shortSma and mediumSma)
                .and(new BooleanIndicatorRule(strongBullishBarIndicator))
                .and(new CrossedUpIndicatorRule(closePriceIndicator, shortSmaIndicator))
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
//        Total profit: 39.97
//        N trades: 4
//        N profitable trades (UP): 4
//        N unprofitable trades (DOWN): 0
//        Risk/Reward ratio: 0
//
//    Series-2 [ohlcvt-1m-2.csv] results:
//        Total profit: 155.62
//        N trades: 30
//        N profitable trades (UP): 27
//        N unprofitable trades (DOWN): 3
//        Risk/Reward ratio: 0.1111111111111111
//
//    Series-3 [ohlcvt-1m-3.csv] results:
//        Total profit: 303.44
//        N trades: 25
//        N profitable trades (UP): 21
//        N unprofitable trades (DOWN): 4
//        Risk/Reward ratio: 0.19047619047619047
