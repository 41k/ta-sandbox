package root.domain.strategy.sma;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.trading.rules.BooleanIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;
import root.domain.indicator.bar.StrongBarIndicator;
import root.domain.indicator.trend.UpTrendIndicator;

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
//        AND
//        (longSma is in upTrend(70, 0.1))
//
//    Sell rule:
//        (closePrice crosses up longSma)

public class SmaStrategy2AFactory extends AbstractSmaStrategyFactory
{
    public SmaStrategy2AFactory(String strategyId, BarSeries series, int shortSmaLength, int mediumSmaLength, int longSmaLength)
    {
        super(strategyId, series, shortSmaLength, mediumSmaLength, longSmaLength);
    }

    @Override
    public Strategy create()
    {
        StrongBarIndicator strongBullishBarIndicator = new StrongBarIndicator(BULLISH, Set.of(BULLISH, BEARISH), 2, series);
        UpTrendIndicator longSmaUpTrendIndicator = new UpTrendIndicator(longSmaIndicator, 70, 0.1);

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
                .and(new UnderIndicatorRule(closePriceIndicator, longSmaIndicator))
                // AND
                // (longSma is in upTrend(70, 0.1))
                .and(new BooleanIndicatorRule(longSmaUpTrendIndicator));

        Rule exitRule = // Sell rule:
                // (closePrice crosses up longSma)
                new CrossedUpIndicatorRule(closePriceIndicator, longSmaIndicator);

        return new BaseStrategy(strategyId, entryRule, exitRule);
    }
}

//    Series-1 [ohlcvt-1m-1.csv] results:
//        Total profit: 19.9
//        N trades: 2
//        N profitable trades (UP): 2
//        N unprofitable trades (DOWN): 0
//        Risk/Reward ratio: 0
//
//    Series-2 [ohlcvt-1m-2.csv] results:
//        Total profit: 72.48
//        N trades: 8
//        N profitable trades (UP): 8
//        N unprofitable trades (DOWN): 0
//        Risk/Reward ratio: 0
//
//    Series-3 [ohlcvt-1m-3.csv] results:
//        Total profit: 184.67
//        N trades: 8
//        N profitable trades (UP): 8
//        N unprofitable trades (DOWN): 0
//        Risk/Reward ratio: 0
