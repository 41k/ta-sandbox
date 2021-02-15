package root.domain.strategy.price_action;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.trading.rules.BooleanIndicatorRule;
import org.ta4j.core.trading.rules.CrossedDownIndicatorRule;
import org.ta4j.core.trading.rules.NotRule;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import root.domain.indicator.EMAIndicator;
import root.domain.indicator.Indicator;
import root.domain.indicator.SARIndicator;
import root.domain.indicator.bar.BullishPinBarIndicator;
import root.domain.rule.SequenceBooleanRule;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;

public class BullishPinBarStrategy1Factory extends AbstractStrategyFactory
{
    private final BullishPinBarIndicator bullishPinBarIndicator;
    private final SARIndicator sarIndicator;
    private final EMAIndicator emaIndicator;
    private final ClosePriceIndicator closePriceIndicator;
    private final List<Indicator<Num>> numIndicators;

    public BullishPinBarStrategy1Factory(String strategyId, BarSeries series)
    {
        super(strategyId, series);
        this.bullishPinBarIndicator = new BullishPinBarIndicator(series, 2, 6, 4);
        this.sarIndicator = new SARIndicator(series);
        this.closePriceIndicator = new ClosePriceIndicator(series);
        this.emaIndicator = new EMAIndicator(closePriceIndicator, 50);
        numIndicators = List.of(sarIndicator, emaIndicator);
    }

    @Override
    public Strategy create()
    {
        Rule sarIsGatherThanClosePrice = new OverIndicatorRule(sarIndicator, closePriceIndicator);
        Rule entryRule = // Buy rule:
                new BooleanIndicatorRule(bullishPinBarIndicator)
                        .and(new SequenceBooleanRule(sarIsGatherThanClosePrice, 2))
                        .and(new NotRule(new SequenceBooleanRule(sarIsGatherThanClosePrice, 3)));

        Rule exitRule = // Sell rule:
                new CrossedDownIndicatorRule(sarIndicator, closePriceIndicator);

        return new BaseStrategy(strategyId, entryRule, exitRule);
    }

    @Override
    public List<Indicator<Num>> getNumIndicators()
    {
        return numIndicators;
    }
}
