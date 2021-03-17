package root.domain.strategy.macd;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.trading.rules.CrossedDownIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;
import root.domain.indicator.NumberIndicator;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;

import static root.domain.indicator.NumberIndicators.*;

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
    private final ClosePriceIndicator closePrice;
    private final NumberIndicator macd;
    private final NumberIndicator macdSignal;
    private final NumberIndicator macdDifference;
    private final NumberIndicator macdLevel0;
    private final List<NumberIndicator> numberIndicators;

    public MacdStrategy1Factory(String strategyId, BarSeries series)
    {
        super(strategyId, series);
        this.closePrice = new ClosePriceIndicator(series);
        this.macd = macd(closePrice, 12, 26);
        this.macdSignal = macdSignal(macd, 9);
        this.macdDifference = macdDifference(macd, macdSignal);
        this.macdLevel0 = macdLevel(0, series);
        this.numberIndicators = List.of(macd, macdSignal, macdDifference, macdLevel0);
    }

    @Override
    public Strategy create()
    {
        var entryRule = new UnderIndicatorRule(macdDifference, macdLevel0)
                .and(new CrossedDownIndicatorRule(macdSignal, macdDifference));
        var exitRule = new CrossedUpIndicatorRule(macd, macdLevel0);
        return new BaseStrategy(strategyId, entryRule, exitRule);
    }

    @Override
    public List<NumberIndicator> getNumberIndicators()
    {
        return numberIndicators;
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
