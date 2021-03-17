package root.domain.strategy.ready;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.trading.rules.*;
import root.domain.indicator.NumberIndicator;
import root.domain.indicator.trend.UpTrendIndicator;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;

import static root.domain.indicator.NumberIndicators.*;

public class ETHUSD_5m_UpTrend_Strategy1Factory extends AbstractStrategyFactory
{
    private final ClosePriceIndicator closePrice;
    private final NumberIndicator sma10;
    private final NumberIndicator sma100;
    private final NumberIndicator sma200;
    private final NumberIndicator wr;
    private final NumberIndicator wrLevelMinus10;
    private final NumberIndicator wrLevelMinus90;
    private final List<NumberIndicator> numberIndicators;

    public ETHUSD_5m_UpTrend_Strategy1Factory(String strategyId, BarSeries series)
    {
        super(strategyId, series);
        this.closePrice = new ClosePriceIndicator(series);
        this.sma10 = sma(closePrice,10);
        this.sma100 = sma(closePrice,100);
        this.sma200 = sma(closePrice,200);
        this.wr = williamsR(10, series);
        this.wrLevelMinus10 = williamsRLevel(-10, series);
        this.wrLevelMinus90 = williamsRLevel(-90, series);
        this.numberIndicators = List.of(sma10, sma100, sma200, wr, wrLevelMinus10, wrLevelMinus90);
    }
    @Override
    public Strategy create()
    {
        var entryRule =
                new OverIndicatorRule(closePrice, sma200)
                        .and(new UnderIndicatorRule(closePrice, sma10))
                        .and(new CrossedDownIndicatorRule(wr, wrLevelMinus90))
                        .and(new UnderIndicatorRule(sma100, sma200))
                        .and(new BooleanIndicatorRule(new UpTrendIndicator(sma100, 10, 0.01)));
                        // as more safe replacement for sma100 up trend rule
                        //.and(new IsFallingRule(new DifferenceIndicator(sma200, sma100), 10));

        var exitRule =
                new CrossedUpIndicatorRule(wr, wrLevelMinus10);

        var unstablePeriod = 200;

        return new BaseStrategy(strategyId, entryRule, exitRule, unstablePeriod);
    }

    @Override
    public List<NumberIndicator> getNumberIndicators()
    {
        return numberIndicators;
    }
}

//    TF:5m A:1 ETH/USD data set (2021-01-09 -- 2021-02-03) results:
//
//    Total profit:	199.6500000000002
//    Average profit per trade:	13.310000000000013
//    N trades:	15
//    N profitable trades (UP):	14
//    N unprofitable trades (DOWN):	1
//    Risk/Reward ratio:	0.07