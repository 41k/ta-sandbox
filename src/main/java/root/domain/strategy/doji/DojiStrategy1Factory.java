package root.domain.strategy.doji;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.candles.DojiIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.trading.rules.BooleanIndicatorRule;
import org.ta4j.core.trading.rules.CrossedDownIndicatorRule;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;
import root.domain.indicator.EMAIndicator;
import root.domain.indicator.Indicator;
import root.domain.indicator.SMAIndicator;
import root.domain.indicator.bar.BarType;
import root.domain.indicator.bar.BarTypeIndicator;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;

import static root.domain.indicator.bar.BarType.BULLISH;

// https://www.youtube.com/watch?v=KFKONLEFwOE

public class DojiStrategy1Factory extends AbstractStrategyFactory
{
    protected final ClosePriceIndicator closePriceIndicator;
    protected final SMAIndicator shortSmaIndicator;
    protected final SMAIndicator mediumSmaIndicator;
    protected final SMAIndicator longSmaIndicator;
    protected final List<Indicator<Num>> numIndicators;

    public DojiStrategy1Factory(String strategyId, BarSeries series)
    {
        super(strategyId, series);
        this.closePriceIndicator = new ClosePriceIndicator(series);
        this.shortSmaIndicator = new SMAIndicator(closePriceIndicator, 7);
        this.mediumSmaIndicator = new SMAIndicator(closePriceIndicator, 25);
        this.longSmaIndicator = new SMAIndicator(closePriceIndicator, 100);
        numIndicators = List.of(
                shortSmaIndicator, mediumSmaIndicator, longSmaIndicator
        );
    }

    @Override
    public Strategy create()
    {
        Rule entryRule = // Buy rule:
                new OverIndicatorRule(closePriceIndicator, shortSmaIndicator)
                .and(new BooleanIndicatorRule(new DojiIndicator(series, 100, 0.1)))
                .and(new BooleanIndicatorRule(new BarTypeIndicator(BULLISH, series)));

        Rule exitRule = // Sell rule:
                new CrossedDownIndicatorRule(closePriceIndicator, shortSmaIndicator);

        return new BaseStrategy(strategyId, entryRule, exitRule);
    }

    @Override
    public List<Indicator<Num>> getNumIndicators()
    {
        return numIndicators;
    }
}
