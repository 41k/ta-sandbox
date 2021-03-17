package root.domain.strategy.ready;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.trading.rules.BooleanIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;
import root.domain.indicator.NumberIndicator;
import root.domain.indicator.price_action.StrongBarIndicator;
import root.domain.indicator.trend.UpTrendIndicator;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;
import java.util.Set;

import static root.domain.indicator.NumberIndicators.sma;
import static root.domain.indicator.price_action.BarType.BEARISH;
import static root.domain.indicator.price_action.BarType.BULLISH;

// Based on 2A
//
//    Buy rule:
//        (sma7 < sma25 < sma100)
//        AND
//        (strongBullishBar(2) crosses up sma7 and sma25)
//        AND
//        (closePrice < sma100)
//        AND
//        (sma100 is in upTrend(70, 0.1))
//
//    Sell rule:
//        (closePrice crosses up longSma)

public class SMA3_UpTrend_Strategy1Factory extends AbstractStrategyFactory
{
    private final ClosePriceIndicator closePrice;
    private final NumberIndicator sma7;
    private final NumberIndicator sma25;
    private final NumberIndicator sma100;
    private final List<NumberIndicator> numberIndicators;

    public SMA3_UpTrend_Strategy1Factory(String strategyId, BarSeries series)
    {
        super(strategyId, series);
        this.closePrice = new ClosePriceIndicator(series);
        this.sma7 = sma(closePrice, 7);
        this.sma25 = sma(closePrice, 25);
        this.sma100 = sma(closePrice, 100);
        this.numberIndicators = List.of(sma7, sma25, sma100);
    }

    @Override
    public Strategy create()
    {
        StrongBarIndicator strongBullishBarIndicator = new StrongBarIndicator(BULLISH, Set.of(BULLISH, BEARISH), 2, series);
        Rule sma100IsInUpTrend = new BooleanIndicatorRule(new UpTrendIndicator(sma100, 70, 0.1));

        Rule entryRule = // Buy rule:
                // (sma7 < sma25 < sma100)
                new UnderIndicatorRule(sma7, sma25)
                .and(new UnderIndicatorRule(sma25, sma100))
                // AND
                // (strongBullishBar(2) crosses up sma7 and sma25)
                .and(new BooleanIndicatorRule(strongBullishBarIndicator))
                .and(new CrossedUpIndicatorRule(closePrice, sma7))
                .and(new CrossedUpIndicatorRule(closePrice, sma25))
                // AND
                // (closePrice < sma100)
                .and(new UnderIndicatorRule(closePrice, sma100))
                // AND
                // (sma100 is in upTrend(70, 0.1))
                .and(sma100IsInUpTrend);

        Rule exitRule = // Sell rule:
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
//    Total profit:	51.56
//    Average profit per trade:	6.445
//    N trades:	8
//    N profitable trades (UP):	7
//    N unprofitable trades (DOWN):	1
//    Risk/Reward ratio:	0.14

//    5m ETH/USD 1610196540000-1611950460000 results:
//
//    Total profit:	36.4
//    Average profit per trade:	12.133333333333333
//    N trades:	3
//    N profitable trades (UP):	3
//    N unprofitable trades (DOWN):	0
//    Risk/Reward ratio:	0.00

//    1m BTC/USD 1610196540000-1611950460000 results:
//
//    Total profit:	-1545.849999999997
//    Average profit per trade:	-48.307812499999905
//    N trades:	32
//    N profitable trades (UP):	22
//    N unprofitable trades (DOWN):	10
//    Risk/Reward ratio:	0.45

//    5m BTC/USD 1610196540000-1611950460000 results:
//
//    Total profit:	243.5
//    Average profit per trade:	81.16666666666667
//    N trades:	3
//    N profitable trades (UP):	3
//    N unprofitable trades (DOWN):	0
//    Risk/Reward ratio:	0.00