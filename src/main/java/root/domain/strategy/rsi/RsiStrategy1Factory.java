package root.domain.strategy.rsi;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.trading.rules.CrossedDownIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import root.domain.indicator.NumberIndicator;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;

import static root.domain.indicator.NumberIndicators.rsi;
import static root.domain.indicator.NumberIndicators.rsiLevel;

//    Buy rule:
//        (rsi(12) crosses down rsiLevel(30))
//
//    Sell rule:
//        (rsi(12) crosses up rsiLevel(30))

public class RsiStrategy1Factory extends AbstractStrategyFactory
{
    private final ClosePriceIndicator closePrice;
    private final NumberIndicator rsi;
    private final NumberIndicator rsiLevel30;
    private final List<NumberIndicator> numberIndicators;

    public RsiStrategy1Factory(String strategyId, BarSeries series)
    {
        super(strategyId, series);
        this.closePrice = new ClosePriceIndicator(series);
        this.rsi = rsi(closePrice, 12);
        this.rsiLevel30 = rsiLevel(30, series);
        this.numberIndicators = List.of(rsi, rsiLevel30);
    }

    @Override
    public Strategy create()
    {
        var entryRule = new CrossedDownIndicatorRule(rsi, rsiLevel30);
        var exitRule = new CrossedUpIndicatorRule(rsi, rsiLevel30);
        return new BaseStrategy(strategyId, entryRule, exitRule);
    }

    @Override
    public List<NumberIndicator> getNumberIndicators()
    {
        return numberIndicators;
    }
}

//    Series-1 [ohlcvt-1m-1.csv] results:
//        Total profit: 123.11
//        N trades: 32
//        N profitable trades (UP): 29
//        N unprofitable trades (DOWN): 3
//        Risk/Reward ratio: 0.10344827586206896
//
//    Series-2 [ohlcvt-1m-2.csv] results:
//        Total profit: 414.91
//        N trades: 168
//        N profitable trades (UP): 126
//        N unprofitable trades (DOWN): 42
//        Risk/Reward ratio: 0.3333333333333333
//
//    Series-3 [ohlcvt-1m-3.csv] results:
//        Total profit: 242.04
//        N trades: 110
//        N profitable trades (UP): 79
//        N unprofitable trades (DOWN): 31
//        Risk/Reward ratio: 0.3924050632911392
