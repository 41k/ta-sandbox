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
import root.domain.indicator.bar.DominatingBarTypeSubSeriesIndicator;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;

import static root.domain.indicator.bar.BarType.BEARISH;

public class ETHUSD_1m_DownTrend_Strategy2Factory extends AbstractStrategyFactory
{
    private final ClosePriceIndicator closePrice;
    private final EMAIndicator ema;
    private final List<Indicator<Num>> numIndicators;

    public ETHUSD_1m_DownTrend_Strategy2Factory(String strategyId, BarSeries series)
    {
        super(strategyId, series);
        this.closePrice = new ClosePriceIndicator(series);
        this.ema = new EMAIndicator(closePrice, 60);
        this.numIndicators = List.of(ema);
    }

    @Override
    public Strategy create()
    {
        Rule entryRule = new BooleanIndicatorRule(new DominatingBarTypeSubSeriesIndicator(BEARISH, 25, 7, series));
        Rule exitRule = new CrossedUpIndicatorRule(closePrice, ema);
        return new BaseStrategy(strategyId, entryRule, exitRule);
    }

    @Override
    public List<Indicator<Num>> getNumIndicators()
    {
        return numIndicators;
    }
}

//    1m ETH/USD 1610196540000-1611950460000 results:
//
//    Total profit:	494.2600000000004
//    Average profit per trade:	6.5901333333333385
//    N trades:	75
//    N profitable trades (UP):	57
//    N unprofitable trades (DOWN):	17
//    Risk/Reward ratio:	0.30

//    5m ETH/USD 1610196540000-1611950460000 results:
//
//    Total profit:	156.02
//    Average profit per trade:	26.003333333333334
//    N trades:	6
//    N profitable trades (UP):	5
//    N unprofitable trades (DOWN):	1
//    Risk/Reward ratio:	0.20

//    1m BTC/USD 1610196540000-1611950460000 results:
//
//    Total profit:	4537.700000000004
//    Average profit per trade:	59.70657894736848
//    N trades:	76
//    N profitable trades (UP):	58
//    N unprofitable trades (DOWN):	18
//    Risk/Reward ratio:	0.31

//    5m BTC/USD 1610196540000-1611950460000 results:
//
//    Total profit:	4477.75
//    Average profit per trade:	279.859375
//    N trades:	16
//    N profitable trades (UP):	10
//    N unprofitable trades (DOWN):	6
//    Risk/Reward ratio:	0.60