package root.domain.strategy.ready;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.PreviousValueIndicator;
import org.ta4j.core.trading.rules.BooleanIndicatorRule;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;
import root.domain.indicator.NumberIndicator;
import root.domain.indicator.price_action.PiercingLineIndicator;
import root.domain.indicator.price_action.SingleBarPriceActionIndicator;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;

import static java.lang.Double.MAX_VALUE;
import static root.domain.indicator.NumberIndicators.ema;
import static root.domain.indicator.price_action.BarType.BULLISH;

public class ETHUSD_15m_EMA_PA_Strategy1Factory extends AbstractStrategyFactory
{
    private final ClosePriceIndicator closePrice;
    private final NumberIndicator ema30;
    private final NumberIndicator ema60;
    private final NumberIndicator ema100;
    private final List<NumberIndicator> numberIndicators;

    public ETHUSD_15m_EMA_PA_Strategy1Factory(String strategyId, BarSeries series)
    {
        super(strategyId, series);
        this.closePrice = new ClosePriceIndicator(series);
        this.ema30 = ema(closePrice, 30);
        this.ema60 = ema(closePrice, 60);
        this.ema100 = ema(closePrice, 100);
        this.numberIndicators = List.of(ema30, ema60, ema100);
    }

    @Override
    public Strategy create()
    {
        var upTrend = new OverIndicatorRule(ema30, ema60).and(new OverIndicatorRule(ema60, ema100));

        var piercingLine = new BooleanIndicatorRule(new PiercingLineIndicator(series));
        var hammer = new BooleanIndicatorRule(new SingleBarPriceActionIndicator(BULLISH, 0, MAX_VALUE, 10, MAX_VALUE, 0, MAX_VALUE, series));
        var bullishPriceActions = piercingLine.or(hammer);

        var entryRule = upTrend.and(bullishPriceActions)
                .and(new UnderIndicatorRule(new PreviousValueIndicator(closePrice), ema100));

        var exitRule = new OverIndicatorRule(closePrice, ema30);

        return new BaseStrategy(strategyId, entryRule, exitRule);
    }

    @Override
    public List<NumberIndicator> getNumberIndicators()
    {
        return numberIndicators;
    }
}

//    TF:15m A:1 ETH/USD
//
//    data-set-2: 1613045040000 - 1616581994000
//
//    Total profit:	158.17
//    Average profit per trade:	19.77125
//    N trades:	8
//    N profitable trades (UP):	8
//    N unprofitable trades (DOWN):	0
//    Risk/Reward ratio:	0.00