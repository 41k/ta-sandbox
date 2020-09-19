package root.domain.strategy.rsi_sma;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.trading.rules.BooleanIndicatorRule;
import org.ta4j.core.trading.rules.CrossedDownIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import root.domain.indicator.Indicator;
import root.domain.indicator.SMAIndicator;
import root.domain.indicator.rsi.RSIIndicator;
import root.domain.indicator.rsi.RSILevelIndicator;
import root.domain.indicator.trend.UpTrendIndicator;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;

public class RsiSmaStrategy2Factory extends AbstractStrategyFactory
{
    protected final ClosePriceIndicator closePriceIndicator;
    protected final SMAIndicator shortSmaIndicator;
    protected final SMAIndicator mediumSmaIndicator;
    protected final SMAIndicator longSmaIndicator;
    protected final RSIIndicator rsi12Indicator;
    protected final RSIIndicator rsi24Indicator;
    protected final RSILevelIndicator rsiLevel60Indicator;
    protected final RSILevelIndicator rsiLevel20Indicator;
    protected final List<Indicator<Num>> numIndicators;

    public RsiSmaStrategy2Factory(String strategyId, BarSeries series)
    {
        super(strategyId, series);
        this.closePriceIndicator = new ClosePriceIndicator(series);
        this.shortSmaIndicator = new SMAIndicator(closePriceIndicator, 7);
        this.mediumSmaIndicator = new SMAIndicator(closePriceIndicator, 25);
        this.longSmaIndicator = new SMAIndicator(closePriceIndicator, 100);
        this.rsi12Indicator = new RSIIndicator(closePriceIndicator, 12);
        this.rsi24Indicator = new RSIIndicator(closePriceIndicator, 24);
        this.rsiLevel60Indicator = new RSILevelIndicator(series, series.numOf(60));
        this.rsiLevel20Indicator = new RSILevelIndicator(series, series.numOf(20));
        numIndicators = List.of(
                shortSmaIndicator, mediumSmaIndicator, longSmaIndicator,
                rsi12Indicator, rsi24Indicator, rsiLevel60Indicator, rsiLevel20Indicator
        );
    }

    @Override
    public Strategy create()
    {
        UpTrendIndicator longSmaUpTrendIndicator = new UpTrendIndicator(longSmaIndicator, 70, 0.2);

        Rule entryRule = // Buy rule:
                new CrossedUpIndicatorRule(rsi12Indicator, rsiLevel20Indicator);

        Rule exitRule = // Sell rule:
                new CrossedUpIndicatorRule(rsi12Indicator, rsiLevel60Indicator);

        return new BaseStrategy(strategyId, entryRule, exitRule);
    }

    @Override
    public List<Indicator<Num>> getNumIndicators()
    {
        return numIndicators;
    }
}
