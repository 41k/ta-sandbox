package root.domain.strategy.rsi_sma;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.trading.rules.BooleanIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;
import root.domain.indicator.Indicator;
import root.domain.indicator.SMAIndicator;
import root.domain.indicator.macd.MACDIndicator;
import root.domain.indicator.macd.MACDLevelIndicator;
import root.domain.indicator.macd.MACDSignalLineIndicator;
import root.domain.indicator.rsi.RSIIndicator;
import root.domain.indicator.rsi.RSILevelIndicator;
import root.domain.indicator.trend.UpTrendIndicator;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;

public class RsiSmaStrategy1Factory extends AbstractStrategyFactory
{
    protected final ClosePriceIndicator closePriceIndicator;
    protected final SMAIndicator shortSmaIndicator;
    protected final SMAIndicator mediumSmaIndicator;
    protected final SMAIndicator longSmaIndicator;
    protected final RSIIndicator rsiIndicator;
    protected final RSILevelIndicator rsiLevel60Indicator;
    protected final RSILevelIndicator rsiLevel30Indicator;
    protected final List<Indicator<Num>> numIndicators;

    public RsiSmaStrategy1Factory(String strategyId, BarSeries series)
    {
        super(strategyId, series);
        this.closePriceIndicator = new ClosePriceIndicator(series);
        this.shortSmaIndicator = new SMAIndicator(closePriceIndicator, 7);
        this.mediumSmaIndicator = new SMAIndicator(closePriceIndicator, 25);
        this.longSmaIndicator = new SMAIndicator(closePriceIndicator, 100);
        this.rsiIndicator = new RSIIndicator(closePriceIndicator, 12);
        this.rsiLevel60Indicator = new RSILevelIndicator(series, series.numOf(60));
        this.rsiLevel30Indicator = new RSILevelIndicator(series, series.numOf(30));
        numIndicators = List.of(
                shortSmaIndicator, mediumSmaIndicator, longSmaIndicator,
                rsiIndicator, rsiLevel60Indicator, rsiLevel30Indicator
        );
    }

    @Override
    public Strategy create()
    {
        UpTrendIndicator longSmaUpTrendIndicator = new UpTrendIndicator(longSmaIndicator, 70, 0.2);

        Rule entryRule = // Buy rule:
                // (shortSma < mediumSma < longSma)
                new UnderIndicatorRule(shortSmaIndicator, mediumSmaIndicator)
                .and(new UnderIndicatorRule(mediumSmaIndicator, longSmaIndicator))
                // AND
                // (longSma is in upTrend(70, 0.2))
                .and(new BooleanIndicatorRule(longSmaUpTrendIndicator))
                // AND
                // (rsi crosses up rsiLevel30)
                .and(new CrossedUpIndicatorRule(rsiIndicator, rsiLevel30Indicator));

        Rule exitRule = // Sell rule:
                // (rsi crosses up rsiLevel60)
                new CrossedUpIndicatorRule(rsiIndicator, rsiLevel60Indicator);

        return new BaseStrategy(strategyId, entryRule, exitRule);
    }

    @Override
    public List<Indicator<Num>> getNumIndicators()
    {
        return numIndicators;
    }
}
