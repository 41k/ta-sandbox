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

public class BTCUSD_5m_DownTrend_Strategy1Factory extends AbstractStrategyFactory
{
    private final ClosePriceIndicator closePrice;
    private final NumberIndicator ema7;
    private final NumberIndicator ema35;
    private final List<NumberIndicator> numberIndicators;

    public BTCUSD_5m_DownTrend_Strategy1Factory(String strategyId, BarSeries series)
    {
        super(strategyId, series);
        this.closePrice = new ClosePriceIndicator(series);
        this.ema7 = ema(closePrice, 7);
        this.ema35 = ema(closePrice, 35);
        this.numberIndicators = List.of(ema7, ema35);
    }

    @Override
    public Strategy create()
    {
        var entryRule = new BooleanIndicatorRule(new DownTrendIndicator(ema7, 15, 110));
        var exitRule = new CrossedUpIndicatorRule(closePrice, ema35);
        return new BaseStrategy(strategyId, entryRule, exitRule);
    }

    @Override
    public List<NumberIndicator> getNumberIndicators()
    {
        return numberIndicators;
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