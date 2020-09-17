package root.domain.strategy.sma;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.trading.rules.BooleanIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;
import root.domain.indicator.direction.UpDirectionIndicator;

public class SmaStrategy6Factory extends AbstractSmaStrategyFactory
{
    public SmaStrategy6Factory(String strategyId, BarSeries series, int shortSmaLength, int mediumSmaLength, int longSmaLength)
    {
        super(strategyId, series, shortSmaLength, mediumSmaLength, longSmaLength);
    }

    @Override
    public Strategy create()
    {
        UpDirectionIndicator mediumSmaUpDirectionIndicator = new UpDirectionIndicator(mediumSmaIndicator, 10);
        Rule entryRule = // Buy rule:
                new BooleanIndicatorRule(mediumSmaUpDirectionIndicator)
                .and(new UnderIndicatorRule(shortSmaIndicator, mediumSmaIndicator))
                .and(new CrossedUpIndicatorRule(closePriceIndicator, shortSmaIndicator))
                .and(new UnderIndicatorRule(closePriceIndicator, mediumSmaIndicator));

        Rule exitRule = // Sell rule:
                new CrossedUpIndicatorRule(closePriceIndicator, mediumSmaIndicator);

        return new BaseStrategy(strategyId, entryRule, exitRule);
    }
}