package root.domain.strategy.sma;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.trading.rules.BooleanIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;
import root.domain.indicator.bar.StrongBarIndicator;
import root.domain.indicator.trend.DownTrendIndicator;

import java.util.Set;

import static root.domain.indicator.bar.BarType.BEARISH;
import static root.domain.indicator.bar.BarType.BULLISH;

//    Given 3 SMA (short, medium, long), e.g.:
//    * SMA(7) - shortSma,
//    * SMA(25) - mediumSma,
//    * SMA(100) - longSma
//
//    Buy rule:
//        (shortSma < longSma)
//        AND
//        (mediumSma < longSma)
//        AND
//        (longSma is in downTrend(70, 0.1))
//        AND
//        (strongBullishBar(4) crosses up shortSma and mediumSma)
//        AND
//        (closePrice < longSma)
//
//    Sell rule:
//        (closePrice crosses up longSma)

public class SmaStrategy4Factory extends AbstractSmaStrategyFactory
{
    public SmaStrategy4Factory(String strategyId, BarSeries series, int shortSmaLength, int mediumSmaLength, int longSmaLength)
    {
        super(strategyId, series, shortSmaLength, mediumSmaLength, longSmaLength);
    }

    @Override
    public Strategy create()
    {
        StrongBarIndicator strongBullishBarIndicator = new StrongBarIndicator(BULLISH, Set.of(BULLISH, BEARISH), 4, series);
        DownTrendIndicator longSmaDownTrendIndicator = new DownTrendIndicator(longSmaIndicator, 70, 0.1);

        Rule entryRule = // Buy rule:
                // (shortSma < longSma)
                new UnderIndicatorRule(shortSmaIndicator, longSmaIndicator)
                // AND
                // (mediumSma < longSma)
                .and(new UnderIndicatorRule(mediumSmaIndicator, longSmaIndicator))
                // AND
                // (longSma is in downTrend(70, 0.1))
                .and(new BooleanIndicatorRule(longSmaDownTrendIndicator))
                // AND
                // (strongBullishBar(4) crosses up shortSma and mediumSma)
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
//        Total profit: 12.05
//        N trades: 1
//        N profitable trades (UP): 1
//        N unprofitable trades (DOWN): 0
//        Risk/Reward ratio: 0
//
//    Series-2 [ohlcvt-1m-2.csv] results:
//        Total profit: 174.33
//        N trades: 12
//        N profitable trades (UP): 11
//        N unprofitable trades (DOWN): 1
//        Risk/Reward ratio: 0.09090909090909091
//
//    Series-3 [ohlcvt-1m-3.csv] results:
//        Total profit: 191.89
//        N trades: 9
//        N profitable trades (UP): 8
//        N unprofitable trades (DOWN): 1
//        Risk/Reward ratio: 0.125
