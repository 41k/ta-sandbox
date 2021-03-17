package root.domain.strategy.ready;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.trading.rules.CrossedDownIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import org.ta4j.core.trading.rules.IsFallingRule;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import root.domain.indicator.NumberIndicator;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;

import static root.domain.indicator.NumberIndicators.*;

public class SAR_WR_Strategy1Factory extends AbstractStrategyFactory
{
    private final ClosePriceIndicator closePrice;
    private final NumberIndicator sar;
    private final NumberIndicator wr;
    private final NumberIndicator wrLevelMinus10;
    private final NumberIndicator wrLevelMinus90;
    private final List<NumberIndicator> numberIndicators;

    public SAR_WR_Strategy1Factory(String strategyId, BarSeries series)
    {
        super(strategyId, series);
        this.closePrice = new ClosePriceIndicator(series);
        this.sar = parabolicSAR(series);
        this.wr = williamsR(10, series);
        this.wrLevelMinus10 = williamsRLevel(-10, series);
        this.wrLevelMinus90 = williamsRLevel(-90, series);
        this.numberIndicators = List.of(sar, wr, wrLevelMinus10, wrLevelMinus90);
    }

    @Override
    public Strategy create()
    {
        var entryRule = new OverIndicatorRule(closePrice, sar)
                .and(new IsFallingRule(sar, 1))
                .and(new CrossedDownIndicatorRule(wr, wrLevelMinus90));

        var exitRule = new CrossedUpIndicatorRule(wr, wrLevelMinus10);

        return new BaseStrategy(strategyId, entryRule, exitRule);
    }

    @Override
    public List<NumberIndicator> getNumberIndicators()
    {
        return numberIndicators;
    }
}