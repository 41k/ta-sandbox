package root.domain.strategy;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.PrecisionNum;
import org.ta4j.core.trading.rules.BooleanIndicatorRule;
import org.ta4j.core.trading.rules.StopGainRule;
import org.ta4j.core.trading.rules.StopLossRule;
import root.domain.indicator.bar.BullishReboundIndicator;

public class BullishReboundStrategy
{
    public static Strategy buildStrategy(BarSeries series)
    {
        if (series == null)
        {
            throw new IllegalArgumentException("Series cannot be null");
        }
        BullishReboundIndicator bullishReboundIndicator = new BullishReboundIndicator(series);
        Rule entryRule = new BooleanIndicatorRule(bullishReboundIndicator);

        ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(series);
        Rule exitRule = new StopLossRule(closePriceIndicator, PrecisionNum.valueOf(0.05))
                .or(new StopGainRule(closePriceIndicator, PrecisionNum.valueOf(0.3)));

        Strategy strategy = new BaseStrategy(entryRule, exitRule);
        strategy.setUnstablePeriod(3);
        return strategy;
    }
}
