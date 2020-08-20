package root.domain.strategy;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.PrecisionNum;
import org.ta4j.core.trading.rules.BooleanIndicatorRule;
import org.ta4j.core.trading.rules.StopLossRule;
import root.domain.indicator.bar.SameBarTypeSubSeriesIndicator;
import root.domain.indicator.bar.StrongBarIndicator;

import java.util.Set;

import static root.domain.indicator.bar.BarType.BEARISH;
import static root.domain.indicator.bar.BarType.BULLISH;

public class BullishReboundStrategy
{
    public static Strategy buildStrategy(BarSeries series)
    {
        if (series == null)
        {
            throw new IllegalArgumentException("Series cannot be null");
        }
        StrongBarIndicator bullishReboundIndicator = new StrongBarIndicator(BULLISH, Set.of(BEARISH), 2, series);
        Rule entryRule = new BooleanIndicatorRule(bullishReboundIndicator);

        ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(series);
        StrongBarIndicator bearishBarIsStrongerThanPreviousBullishBarIndicator = new StrongBarIndicator(BEARISH, Set.of(BULLISH), 1, series);
        SameBarTypeSubSeriesIndicator bullishBarSequenceIndicator = new SameBarTypeSubSeriesIndicator(BEARISH, 2, series);
        Rule exitRule = new StopLossRule(closePriceIndicator, PrecisionNum.valueOf(0.0001))
                .or(new BooleanIndicatorRule(bearishBarIsStrongerThanPreviousBullishBarIndicator))
                .or(new BooleanIndicatorRule(bullishBarSequenceIndicator));
                //.or(new StopGainRule(closePriceIndicator, PrecisionNum.valueOf(0.14)));

        Strategy strategy = new BaseStrategy(entryRule, exitRule);
        strategy.setUnstablePeriod(3);
        return strategy;
    }
}
