package root.domain.strategy.ready;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.trading.rules.BooleanIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import root.domain.indicator.EMAIndicator;
import root.domain.indicator.Indicator;
import root.domain.indicator.trend.DownTrendIndicator;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;

public class BTCUSD_5m_DownTrend_Strategy1Factory extends AbstractStrategyFactory
{
    private final ClosePriceIndicator closePrice;
    private final EMAIndicator ema7;
    private final EMAIndicator ema35;
    private final List<Indicator<Num>> numIndicators;

    public BTCUSD_5m_DownTrend_Strategy1Factory(String strategyId, BarSeries series)
    {
        super(strategyId, series);
        this.closePrice = new ClosePriceIndicator(series);
        this.ema7 = new EMAIndicator(closePrice, 7);
        this.ema35 = new EMAIndicator(closePrice, 35);
        this.numIndicators = List.of(ema7, ema35);
    }

    @Override
    public Strategy create()
    {
        Rule entryRule = new BooleanIndicatorRule(new DownTrendIndicator(ema7, 15, 110));
        Rule exitRule = new CrossedUpIndicatorRule(closePrice, ema35);
        return new BaseStrategy(strategyId, entryRule, exitRule);
    }

    @Override
    public List<Indicator<Num>> getNumIndicators()
    {
        return numIndicators;
    }
}

//    5m BTC/USD 1610196540000-1611950460000 results:
//
//    Total profit:	11551
//    Average profit per trade:	1925.1666666666667
//    N trades:	6
//    N profitable trades (UP):	6
//    N unprofitable trades (DOWN):	0
//    Risk/Reward ratio:	0.00