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

public class SmaStrategyXFactory extends AbstractSmaStrategyFactory
{
    public SmaStrategyXFactory(String strategyId, BarSeries series, int shortSmaLength, int mediumSmaLength, int longSmaLength)
    {
        super(strategyId, series, shortSmaLength, mediumSmaLength, longSmaLength);
    }

    @Override
    public Strategy create()
    {
        StrongBarIndicator strongBullishBarIndicator = new StrongBarIndicator(BULLISH, Set.of(BULLISH, BEARISH), 2, series);

        Rule entryRule = new UnderIndicatorRule(shortSmaIndicator, mediumSmaIndicator)
                .and(new CrossedUpIndicatorRule(closePriceIndicator, shortSmaIndicator))
                .and(new BooleanIndicatorRule(strongBullishBarIndicator));

        Rule exitRule = new CrossedUpIndicatorRule(shortSmaIndicator, mediumSmaIndicator)
                .or(new CrossedUpIndicatorRule(closePriceIndicator, longSmaIndicator));

        return new BaseStrategy(strategyId, entryRule, exitRule);
    }
}

//    Series-1 [ohlcvt-1m-1.csv] results:
//        Total profit: 125.59
//        N trades: 39
//        N profitable trades (UP): 29
//        N unprofitable trades (DOWN): 10
//        Risk/Reward ratio: 0.3448275862068966
