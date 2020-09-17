package root.domain.strategy.macd_sma;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.trading.rules.*;
import root.domain.indicator.Indicator;
import root.domain.indicator.SMAIndicator;
import root.domain.indicator.direction.UpDirectionIndicator;
import root.domain.indicator.macd.MACDIndicator;
import root.domain.indicator.macd.MACDLevelIndicator;
import root.domain.indicator.macd.MACDSignalLineIndicator;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;

public class MacdSmaStrategy2Factory extends AbstractStrategyFactory
{
    private final ClosePriceIndicator closePriceIndicator;
    private final SMAIndicator shortSmaIndicator;
    private final SMAIndicator mediumSmaIndicator;
    private final SMAIndicator longSmaIndicator;
    private final SMAIndicator sma150Indicator;
    private final SMAIndicator sma200Indicator;
    private final MACDIndicator macdIndicator;
    private final MACDSignalLineIndicator macdSignalLineIndicator;
    private final MACDLevelIndicator macdLevel0Indicator;
    private final List<Indicator<Num>> numIndicators;

    public MacdSmaStrategy2Factory(String strategyId, BarSeries series)
    {
        super(strategyId, series);
        this.closePriceIndicator = new ClosePriceIndicator(series);
        this.shortSmaIndicator = new SMAIndicator(closePriceIndicator, 7);
        this.mediumSmaIndicator = new SMAIndicator(closePriceIndicator, 25);
        this.longSmaIndicator = new SMAIndicator(closePriceIndicator, 100);
        this.sma150Indicator = new SMAIndicator(closePriceIndicator, 150);
        this.sma200Indicator = new SMAIndicator(closePriceIndicator, 200);
        this.macdIndicator = new MACDIndicator(closePriceIndicator, 12, 26);
        this.macdSignalLineIndicator = new MACDSignalLineIndicator(macdIndicator, 9);
        this.macdLevel0Indicator = new MACDLevelIndicator(series, series.numOf(0));
        this.numIndicators = List.of(
                shortSmaIndicator, mediumSmaIndicator, longSmaIndicator,
                sma150Indicator, sma200Indicator,
                macdIndicator, macdSignalLineIndicator, macdLevel0Indicator
        );
    }

    @Override
    public Strategy create()
    {
        UpDirectionIndicator longSmaUpDirectionIndicator = new UpDirectionIndicator(longSmaIndicator, 10);
        UpDirectionIndicator sma150UpDirectionIndicator = new UpDirectionIndicator(sma150Indicator, 10);
        UpDirectionIndicator sma200UpDirectionIndicator = new UpDirectionIndicator(sma200Indicator, 10);

        Rule entryRule = // Buy rule:
                // (macd < 0)
                new UnderIndicatorRule(macdIndicator, macdLevel0Indicator)
                // AND
                // (macdSignalLine < 0)
                .and(new UnderIndicatorRule(macdSignalLineIndicator, macdLevel0Indicator))
                // AND
                // (macd crosses up macdSignalLine)
                .and(new CrossedUpIndicatorRule(macdIndicator, macdSignalLineIndicator))

                        .and(new UnderIndicatorRule(sma200Indicator, sma150Indicator))
                        .and(new UnderIndicatorRule(sma150Indicator, longSmaIndicator))
                        .and(new UnderIndicatorRule(longSmaIndicator, mediumSmaIndicator))
                        .and(new BooleanIndicatorRule(longSmaUpDirectionIndicator))
                        .and(new BooleanIndicatorRule(sma150UpDirectionIndicator))
                        .and(new BooleanIndicatorRule(sma200UpDirectionIndicator));

        Rule exitRule = new OverIndicatorRule(macdIndicator, macdLevel0Indicator)
                .and(new OverIndicatorRule(macdSignalLineIndicator, macdLevel0Indicator))
                .and(new CrossedDownIndicatorRule(macdIndicator, macdSignalLineIndicator));

        return new BaseStrategy(strategyId, entryRule, exitRule);
    }

    @Override
    public List<Indicator<Num>> getNumIndicators()
    {
        return numIndicators;
    }
}
