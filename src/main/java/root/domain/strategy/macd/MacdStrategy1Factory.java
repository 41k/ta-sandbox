package root.domain.strategy.macd;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.trading.rules.CrossedDownIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;
import root.domain.indicator.Indicator;
import root.domain.indicator.SMAIndicator;
import root.domain.indicator.direction.UpDirectionIndicator;
import root.domain.indicator.macd.MACDDifferenceIndicator;
import root.domain.indicator.macd.MACDIndicator;
import root.domain.indicator.macd.MACDLevelIndicator;
import root.domain.indicator.macd.MACDSignalLineIndicator;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;


//
//    Buy rule:
//        (macdDiff < level(0))
//        AND
//        (macdSignalLine crosses down macdDiff)
//
//    Sell rule:
//        (macd crosses up level(0))

public class MacdStrategy1Factory extends AbstractStrategyFactory
{
    private final ClosePriceIndicator closePriceIndicator;
    private final MACDIndicator macdIndicator;
    private final MACDSignalLineIndicator macdSignalLineIndicator;
    private final MACDDifferenceIndicator macdDifferenceIndicator;
    private final MACDLevelIndicator macdLevel0Indicator;
    private final List<Indicator<Num>> numIndicators;

    public MacdStrategy1Factory(String strategyId, BarSeries series)
    {
        super(strategyId, series);
        this.closePriceIndicator = new ClosePriceIndicator(series);
        this.macdIndicator = new MACDIndicator(closePriceIndicator, 12, 26);
        this.macdSignalLineIndicator = new MACDSignalLineIndicator(macdIndicator, 9);
        this.macdDifferenceIndicator = new MACDDifferenceIndicator(macdIndicator, macdSignalLineIndicator);
        this.macdLevel0Indicator = new MACDLevelIndicator(series, series.numOf(0));
        this.numIndicators = List.of(
                macdIndicator, macdSignalLineIndicator, macdDifferenceIndicator, macdLevel0Indicator
        );
    }

    @Override
    public Strategy create()
    {
        Rule entryRule = // Buy rule:
                // (macdDiff < level(0))
                new UnderIndicatorRule(macdDifferenceIndicator, macdLevel0Indicator)
                // AND
                // (macdSignalLine crosses down macdDiff)
                .and(new CrossedDownIndicatorRule(macdSignalLineIndicator, macdDifferenceIndicator));

        Rule exitRule = // Sell rule:
                // (macd crosses up level(0))
                new CrossedUpIndicatorRule(macdIndicator, macdLevel0Indicator);

        return new BaseStrategy(strategyId, entryRule, exitRule);
    }

    @Override
    public List<Indicator<Num>> getNumIndicators()
    {
        return numIndicators;
    }
}

//    Series-1 [ohlcvt-1m-1.csv] results:
//        Total profit:	159.01
//        N trades:	26
//        N profitable trades (UP):	19
//        N unprofitable trades (DOWN):	7
//        Risk/Reward ratio:	0.37
//
//    Series-2 [ohlcvt-1m-2.csv] results:
//        Total profit:	193.6
//        N trades:	114
//        N profitable trades (UP):	81
//        N unprofitable trades (DOWN):	33
//        Risk/Reward ratio:	0.41
//
//    Series-3 [ohlcvt-1m-3.csv] results:
//        Total profit:	834.05
//        N trades:	111
//        N profitable trades (UP):	82
//        N unprofitable trades (DOWN):	29
//        Risk/Reward ratio:	0.35
