package root.domain.strategy.ready;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.trading.rules.BooleanIndicatorRule;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;
import root.domain.indicator.NumberIndicator;
import root.domain.indicator.price_action.SingleBarPriceActionIndicator;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;

import static java.lang.Double.MAX_VALUE;
import static root.domain.indicator.NumberIndicators.*;
import static root.domain.indicator.price_action.BarType.BULLISH;

public class ETHUSD_5m_BB_Strategy1Factory extends AbstractStrategyFactory
{
    private final ClosePriceIndicator closePrice;
    private final LowPriceIndicator lowPrice;
    private final NumberIndicator bbm;
    private final NumberIndicator bbu;
    private final NumberIndicator bbl;
    private final List<NumberIndicator> numberIndicators;

    public ETHUSD_5m_BB_Strategy1Factory(String strategyId, BarSeries series)
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
        var hammerBar = new BooleanIndicatorRule(new SingleBarPriceActionIndicator(BULLISH, 0, 5, 11, MAX_VALUE, 0, MAX_VALUE, series));
        var entryRule = new UnderIndicatorRule(lowPrice, bbl).and(hammerBar);
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
//    Total profit:	257.2499999999998
//    Average profit per trade:	15.132352941176457
//    N trades:	17
//    N profitable trades (UP):	15
//    N unprofitable trades (DOWN):	2
//    Risk/Reward ratio:	0.13
//
//    data-set-2
//
//    Total profit:	88.0300000000001
//    Average profit per trade:	11.003750000000013
//    N trades:	8
//    N profitable trades (UP):	7
//    N unprofitable trades (DOWN):	1
//    Risk/Reward ratio:	0.14