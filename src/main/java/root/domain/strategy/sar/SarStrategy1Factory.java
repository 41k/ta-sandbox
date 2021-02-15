package root.domain.strategy.sar;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.trading.rules.*;
import root.domain.indicator.Indicator;
import root.domain.indicator.SARIndicator;
import root.domain.indicator.bar.StrongBarIndicator;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;
import java.util.Set;

import static org.ta4j.core.Order.OrderType.BUY;
import static root.domain.indicator.bar.BarType.BEARISH;
import static root.domain.indicator.bar.BarType.BULLISH;

//    Buy rule:
//
//
//    Sell rule:
//

public class SarStrategy1Factory extends AbstractStrategyFactory
{
    private final ClosePriceIndicator closePriceIndicator;
    private final SARIndicator sarIndicator;
    private final StrongBarIndicator strongBullishBarIndicator;
    private final List<Indicator<Num>> numIndicators;

    public SarStrategy1Factory(String strategyId, BarSeries series)
    {
        super(strategyId, series);
        this.closePriceIndicator = new ClosePriceIndicator(series);
        this.sarIndicator = new SARIndicator(series);
        this.strongBullishBarIndicator = new StrongBarIndicator(BULLISH, Set.of(BULLISH, BEARISH), 2, series);
        numIndicators = List.of(sarIndicator);
    }

    @Override
    public Strategy create()
    {
        Rule entryRule = // Buy rule:
                new OverIndicatorRule(closePriceIndicator, sarIndicator)
                        .and(new IsFallingRule(sarIndicator, 16))
                        .and(new BooleanIndicatorRule(strongBullishBarIndicator));

        Rule exitRule = // Sell rule:
                new CrossedDownIndicatorRule(closePriceIndicator, sarIndicator);

        return new BaseStrategy(strategyId, entryRule, exitRule);
    }

    @Override
    public List<Indicator<Num>> getNumIndicators()
    {
        return numIndicators;
    }
}
