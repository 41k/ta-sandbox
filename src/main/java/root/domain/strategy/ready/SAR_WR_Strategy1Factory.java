package root.domain.strategy.ready;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.trading.rules.CrossedDownIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import org.ta4j.core.trading.rules.IsFallingRule;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import root.domain.indicator.Indicator;
import root.domain.indicator.SARIndicator;
import root.domain.indicator.wri.WRIndicator;
import root.domain.indicator.wri.WRLevelIndicator;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;

public class SAR_WR_Strategy1Factory extends AbstractStrategyFactory
{
    private final ClosePriceIndicator closePrice;
    private final SARIndicator sar;
    private final WRIndicator wr;
    private final WRLevelIndicator wrLevelMinus10;
    private final WRLevelIndicator wrLevelMinus90;
    private final List<Indicator<Num>> numIndicators;

    public SAR_WR_Strategy1Factory(String strategyId, BarSeries series)
    {
        super(strategyId, series);
        this.closePrice = new ClosePriceIndicator(series);
        this.sar = new SARIndicator(series);
        this.wr = new WRIndicator(series, 30);
        this.wrLevelMinus10 = new WRLevelIndicator(series, series.numOf(-10));
        this.wrLevelMinus90 = new WRLevelIndicator(series, series.numOf(-90));
        this.numIndicators = List.of(sar, wr, wrLevelMinus10, wrLevelMinus90);
    }

    @Override
    public Strategy create()
    {
        Rule entryRule = // Buy rule:
                new OverIndicatorRule(closePrice, sar)
                        .and(new IsFallingRule(sar, 1))
                        .and(new CrossedDownIndicatorRule(wr, wrLevelMinus90));

        Rule exitRule = // Sell rule:
                new CrossedUpIndicatorRule(wr, wrLevelMinus10);

        return new BaseStrategy(strategyId, entryRule, exitRule);
    }

    @Override
    public List<Indicator<Num>> getNumIndicators()
    {
        return numIndicators;
    }
}

//    1m ETH/USD 1610196540000-1612321560000 results:
//
//    Total profit:	90.7000000000001
//    Average profit per trade:	9.070000000000011
//    N trades:	10
//    N profitable trades (UP):	10
//    N unprofitable trades (DOWN):	0
//    Risk/Reward ratio:	0.00