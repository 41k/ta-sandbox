package root.domain.strategy.macd_sma;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.trading.rules.CrossedDownIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;
import root.domain.indicator.Indicator;
import root.domain.indicator.OBVIndicator;
import root.domain.indicator.SMAIndicator;
import root.domain.indicator.macd.MACDIndicator;
import root.domain.indicator.macd.MACDLevelIndicator;
import root.domain.indicator.macd.MACDSignalLineIndicator;
import root.domain.strategy.AbstractStrategyFactory;
import root.domain.strategy.sma.AbstractSmaStrategyFactory;

import java.util.ArrayList;
import java.util.List;

public class MacdSmaStrategyXFactory extends AbstractStrategyFactory
{
    private final ClosePriceIndicator closePriceIndicator;
    private final SMAIndicator shortSmaIndicator;
    private final SMAIndicator mediumSmaIndicator;
    private final SMAIndicator longSmaIndicator;
    private final MACDIndicator macdIndicator;
    private final MACDSignalLineIndicator macdSignalLineIndicator;
    private final MACDLevelIndicator macdLevelIndicator;
    private final List<Indicator<Num>> numIndicators;

    public MacdSmaStrategyXFactory(String strategyId, BarSeries series)
    {
        super(strategyId, series);
        this.closePriceIndicator = new ClosePriceIndicator(series);
        this.shortSmaIndicator = new SMAIndicator(closePriceIndicator, 7);
        this.mediumSmaIndicator = new SMAIndicator(closePriceIndicator, 25);
        this.longSmaIndicator = new SMAIndicator(closePriceIndicator, 100);
        this.macdIndicator = new MACDIndicator(closePriceIndicator, 12, 26);
        this.macdSignalLineIndicator = new MACDSignalLineIndicator(macdIndicator, 9);
        this.macdLevelIndicator = new MACDLevelIndicator(series, series.numOf(0));
        this.numIndicators = List.of(
                shortSmaIndicator, mediumSmaIndicator, longSmaIndicator,
                macdIndicator, macdSignalLineIndicator, macdLevelIndicator
        );
    }

    @Override
    public Strategy create()
    {
        Rule entryRule = new CrossedUpIndicatorRule(macdIndicator, macdSignalLineIndicator);
        Rule exitRule = new CrossedDownIndicatorRule(macdIndicator, macdSignalLineIndicator);
        return new BaseStrategy(strategyId, entryRule, exitRule);
    }

    @Override
    public List<Indicator<Num>> getNumIndicators()
    {
        return numIndicators;
    }
}
