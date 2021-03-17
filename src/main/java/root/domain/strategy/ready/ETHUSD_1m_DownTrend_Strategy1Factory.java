package root.domain.strategy.ready;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.trading.rules.BooleanIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import root.domain.indicator.NumberIndicator;
import root.domain.indicator.trend.DownTrendIndicator;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;

import static root.domain.indicator.NumberIndicators.ema;

public class ETHUSD_1m_DownTrend_Strategy1Factory extends AbstractStrategyFactory
{
    private final ClosePriceIndicator closePrice;
    private final NumberIndicator ema7;
    private final NumberIndicator ema20;
    private final List<NumberIndicator> numberIndicators;

    public ETHUSD_1m_DownTrend_Strategy1Factory(String strategyId, BarSeries series)
    {
        super(strategyId, series);
        this.closePrice = new ClosePriceIndicator(series);
        this.ema7 = ema(closePrice, 7);
        this.ema20 = ema(closePrice, 20);
        this.numberIndicators = List.of(ema7, ema20);
    }

    @Override
    public Strategy create()
    {
        var entryRule = new BooleanIndicatorRule(new DownTrendIndicator(ema7, 5, 7));
        var exitRule = new CrossedUpIndicatorRule(closePrice, ema20);
        return new BaseStrategy(strategyId, entryRule, exitRule);
    }

    @Override
    public List<NumberIndicator> getNumberIndicators()
    {
        return numberIndicators;
    }
}

//    1m ETH/USD 1610196540000-1611950460000 results:
//
//    length:6_slope:4 -- 11:1 -- 156.4200000000004 -- 13.035000000000034
//    length:5_slope:7 -- 3:0 -- 157.89 -- 52.629999999999995