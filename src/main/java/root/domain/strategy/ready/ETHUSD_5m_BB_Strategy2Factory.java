package root.domain.strategy.ready;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.candles.LowerShadowIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;
import root.domain.indicator.NumberIndicator;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;

import static root.domain.indicator.NumberIndicators.*;

public class ETHUSD_5m_BB_Strategy2Factory extends AbstractStrategyFactory
{
    private final ClosePriceIndicator closePrice;
    private final LowPriceIndicator lowPrice;
    private final NumberIndicator bbm;
    private final NumberIndicator bbu;
    private final NumberIndicator bbl;
    private final List<NumberIndicator> numberIndicators;

    public ETHUSD_5m_BB_Strategy2Factory(String strategyId, BarSeries series)
    {
        super(strategyId, series);
        this.closePrice = new ClosePriceIndicator(series);
        this.lowPrice = new LowPriceIndicator(series);
        var periodLength = 20;
        this.bbm = bollingerBandsMiddle(closePrice, periodLength);
        this.bbu = bollingerBandsUpper(bbm, closePrice, periodLength);
        this.bbl = bollingerBandsLower(bbm, closePrice, periodLength);
        this.numberIndicators = List.of(bbu, bbm, bbl);
    }

    @Override
    public Strategy create()
    {
        var barWithLongLowerShadow = new OverIndicatorRule(new LowerShadowIndicator(series), 15);
        var entryRule = new UnderIndicatorRule(lowPrice, bbl).and(barWithLongLowerShadow);
        var exitRule = new OverIndicatorRule(closePrice, bbm);
        return new BaseStrategy(strategyId, entryRule, exitRule);
    }

    @Override
    public List<NumberIndicator> getNumberIndicators()
    {
        return numberIndicators;
    }
}

// TF:5m A:1 ETH/USD
//
//    data-set-1:
//
//    Total profit:	338.1499999999997
//    Average profit per trade:	24.153571428571407
//    N trades:	14
//    N profitable trades (UP):	12
//    N unprofitable trades (DOWN):	2
//    Risk/Reward ratio:	0.17
//
//    data-set-2
//
//    Total profit:	389.2
//    Average profit per trade:	43.24444444444444
//    N trades:	9
//    N profitable trades (UP):	8
//    N unprofitable trades (DOWN):	1
//    Risk/Reward ratio:	0.13