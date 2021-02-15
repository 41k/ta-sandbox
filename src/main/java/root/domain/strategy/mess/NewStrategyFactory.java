package root.domain.strategy.mess;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.candles.LowerShadowIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.trading.rules.BooleanIndicatorRule;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;
import org.ta4j.core.trading.rules.WaitForRule;
import root.domain.indicator.Indicator;
import root.domain.indicator.PreviousBooleanIndicator;
import root.domain.indicator.SMAIndicator;
import root.domain.indicator.bar.BarIndicator;
import root.domain.indicator.bar.SameBarTypeSubSeriesIndicator;
import root.domain.indicator.bollinger.BBLowerIndicator;
import root.domain.indicator.bollinger.BBMiddleIndicator;
import root.domain.indicator.bollinger.BBUpperIndicator;
import root.domain.indicator.rsi.RSIIndicator;
import root.domain.indicator.rsi.RSILevelIndicator;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;

import static java.lang.Double.MAX_VALUE;
import static org.ta4j.core.Order.OrderType.BUY;
import static root.domain.indicator.bar.BarType.BEARISH;
import static root.domain.indicator.bar.BarType.BULLISH;

public class NewStrategyFactory extends AbstractStrategyFactory
{
    private final ClosePriceIndicator closePrice;
    private final LowPriceIndicator lowPrice;
    private final BBMiddleIndicator bbm;
    private final BBUpperIndicator bbu;
    private final BBLowerIndicator bbl;
    private final RSIIndicator rsi;
    private final RSILevelIndicator rsiLevel50;
    private final RSILevelIndicator rsiLevel30;
    private final List<Indicator<Num>> numIndicators;

    public NewStrategyFactory(String strategyId, BarSeries series)
    {
        super(strategyId, series);
        this.closePrice = new ClosePriceIndicator(series);
        this.lowPrice = new LowPriceIndicator(series);
        var periodLength = 20;
        var standardDeviation = new StandardDeviationIndicator(closePrice, periodLength);
        this.bbm = new BBMiddleIndicator(new SMAIndicator(closePrice, periodLength));
        this.bbu = new BBUpperIndicator(bbm, standardDeviation, series.numOf(2));
        this.bbl = new BBLowerIndicator(bbm, standardDeviation, series.numOf(2));
        this.rsi = new RSIIndicator(closePrice, 14);
        this.rsiLevel50 = new RSILevelIndicator(series, series.numOf(50));
        this.rsiLevel30 = new RSILevelIndicator(series, series.numOf(30));
        numIndicators = List.of(bbu, bbm, bbl, rsi, rsiLevel50, rsiLevel30);
    }

    @Override
    public Strategy create()
    {
        Rule barWithLongLowerShadow = new OverIndicatorRule(new LowerShadowIndicator(series), 15);
        Rule entryRule = new UnderIndicatorRule(lowPrice, bbl).and(barWithLongLowerShadow);
        Rule exitRule = new OverIndicatorRule(closePrice, bbm);
        return new BaseStrategy(strategyId, entryRule, exitRule);
    }

    @Override
    public List<Indicator<Num>> getNumIndicators()
    {
        return numIndicators;
    }
}

// TF:5m A:1 ETH/USD
// Rule hammerBar = new BooleanIndicatorRule(new BarIndicator(BULLISH, 0, 5, 11, MAX_VALUE, 0, MAX_VALUE, series));
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