package root.domain.strategy.ready;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.trading.rules.BooleanIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;
import root.domain.indicator.NumberIndicator;
import root.domain.indicator.trend.UpTrendIndicator;
import root.domain.rule.SequenceBooleanRule;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;

import static root.domain.indicator.NumberIndicators.ema;
import static root.domain.indicator.NumberIndicators.sma;

// Based 3B
//
//    Buy rule:
//        (sma7 < sma25 < sma100)
//        AND
//        (closePrice crosses up sma25)
//        AND
//        (closePrice < ema150)
//        AND
//        (sma100 is in upTrend(70, 0.2))
//        AND
//        (ema150 is under sma100 during last 100 bars)
//
//    Sell rule:
//        (closePrice crosses up sma100)

public class SMA3_EMA_Strategy1Factory extends AbstractStrategyFactory
{
    private final ClosePriceIndicator closePrice;
    private final NumberIndicator sma7;
    private final NumberIndicator sma25;
    private final NumberIndicator sma100;
    private final NumberIndicator ema150;
    private final List<NumberIndicator> numberIndicators;

    public SMA3_EMA_Strategy1Factory(String strategyId, BarSeries series)
    {
        super(strategyId, series);
        this.closePrice = new ClosePriceIndicator(series);
        this.sma7 = sma(closePrice, 7);
        this.sma25 = sma(closePrice, 25);
        this.sma100 = sma(closePrice, 100);
        this.ema150 = ema(closePrice, 150);
        this.numberIndicators = List.of(sma7, sma25, sma100, ema150);
    }

    @Override
    public Strategy create()
    {
        var sma100IsInUpTrend = new BooleanIndicatorRule(new UpTrendIndicator(sma100, 70, 0.2));
        var ema150IsUnderSma100DuringLast100Bars = new SequenceBooleanRule(new UnderIndicatorRule(ema150, sma100), 100);

        var entryRule = // Buy rule:
                // (sma7 < sma25 < sma100)
                new UnderIndicatorRule(sma7, sma25)
                .and(new UnderIndicatorRule(sma25, sma100))
                // AND
                // (closePrice crosses up sma25)
                .and(new CrossedUpIndicatorRule(closePrice, sma25))
                // AND
                // (closePrice < ema150)
                .and(new UnderIndicatorRule(closePrice, ema150))
                // AND
                // (sma100 is in upTrend(70, 0.2))
                .and(sma100IsInUpTrend)
                // AND
                // (ema150 is under sma100 during last 100 bars)
                .and(ema150IsUnderSma100DuringLast100Bars);

        var exitRule = // Sell rule:
                // (closePrice crosses up sma100)
                new CrossedUpIndicatorRule(closePrice, sma100);

        return new BaseStrategy(strategyId, entryRule, exitRule);
    }

    @Override
    public List<NumberIndicator> getNumberIndicators()
    {
        return numberIndicators;
    }
}

//    1m ETH/USD 1610196540000-1611950460000 results:
//
//    Total profit:	31.34
//    Average profit per trade:	10.446666666666667
//    N trades:	3
//    N profitable trades (UP):	3
//    N unprofitable trades (DOWN):	0
//    Risk/Reward ratio:	0.00

//    5m ETH/USD 1610196540000-1611950460000 results:
//
//    Total profit:	82.15
//    Average profit per trade:	20.5375
//    N trades:	4
//    N profitable trades (UP):	4
//    N unprofitable trades (DOWN):	0
//    Risk/Reward ratio:	0.00

//    1m BTC/USD 1610196540000-1611950460000 results:
//
//    Total profit:	787.75
//    Average profit per trade:	71.61363636363636
//    N trades:	11
//    N profitable trades (UP):	8
//    N unprofitable trades (DOWN):	3
//    Risk/Reward ratio:	0.38

//    5m BTC/USD 1610196540000-1611950460000 results:
//
//    Total profit:	1521.549999999996
//    Average profit per trade:	507.18333333333203
//    N trades:	3
//    N profitable trades (UP):	3
//    N unprofitable trades (DOWN):	0
//    Risk/Reward ratio:	0.00