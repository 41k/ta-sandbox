package root.domain.strategy.ready;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.trading.rules.BooleanIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;
import root.domain.indicator.EMAIndicator;
import root.domain.indicator.Indicator;
import root.domain.indicator.trend.DownTrendIndicator;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;

public class ETHUSD_5m_DownTrend_Strategy1Factory extends AbstractStrategyFactory
{
    private final ClosePriceIndicator closePrice;
    private final EMAIndicator ema7;
    private final EMAIndicator ema20;
    private final List<Indicator<Num>> numIndicators;

    public ETHUSD_5m_DownTrend_Strategy1Factory(String strategyId, BarSeries series)
    {
        super(strategyId, series);
        this.closePrice = new ClosePriceIndicator(series);
        this.ema7 = new EMAIndicator(closePrice, 7);
        this.ema20 = new EMAIndicator(closePrice, 20);
        this.numIndicators = List.of(ema7, ema20);
    }

    @Override
    public Strategy create()
    {
        Rule entryRule = new BooleanIndicatorRule(new DownTrendIndicator(ema7, 15, 4.2))
                .and(new UnderIndicatorRule(closePrice, ema7))
                .and(new UnderIndicatorRule(ema7, ema20));
        Rule exitRule = new CrossedUpIndicatorRule(closePrice, ema20);
        return new BaseStrategy(strategyId, entryRule, exitRule);
    }

    @Override
    public List<Indicator<Num>> getNumIndicators()
    {
        return numIndicators;
    }
}

//    5m ETH/USD 1610196540000-1611950460000 results:
//
//    length:15_slope:4 -- 9:0 -- 374.9099999999997 -- 41.65666666666663
//    length:15_slope:4.2 -- 7:0 -- 386.2499999999998 -- 55.178571428571395