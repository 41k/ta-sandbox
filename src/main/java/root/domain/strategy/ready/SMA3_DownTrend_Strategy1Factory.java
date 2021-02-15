package root.domain.strategy.ready;

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
import root.domain.indicator.bar.StrongBarIndicator;
import root.domain.indicator.trend.DownTrendIndicator;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;
import java.util.Set;

import static root.domain.indicator.bar.BarType.BEARISH;
import static root.domain.indicator.bar.BarType.BULLISH;

// Based on 4
//
//    Buy rule:
//        (sma7 < sma100)
//        AND
//        (sma25 < sma100)
//        AND
//        (sma100 is in downTrend(70, 0.1))
//        AND
//        (sma25 is in downTrend(10, 0.1))
//        AND
//        (strongBullishBar(4) crosses up sma7 and sma25)
//        AND
//        (closePrice < sma100)
//
//    Sell rule:
//        (closePrice crosses up sma100)

public class SMA3_DownTrend_Strategy1Factory extends AbstractStrategyFactory
{
    private final ClosePriceIndicator closePrice;
    private final SMAIndicator sma7;
    private final SMAIndicator sma25;
    private final SMAIndicator sma100;
    private final List<Indicator<Num>> numIndicators;

    public SMA3_DownTrend_Strategy1Factory(String strategyId, BarSeries series)
    {
        super(strategyId, series);
        this.closePrice = new ClosePriceIndicator(series);
        this.sma7 = new SMAIndicator(closePrice, 7);
        this.sma25 = new SMAIndicator(closePrice, 25);
        this.sma100 = new SMAIndicator(closePrice, 100);
        this.numIndicators = List.of(sma7, sma25, sma100);
    }

    @Override
    public Strategy create()
    {
        StrongBarIndicator strongBullishBarIndicator = new StrongBarIndicator(BULLISH, Set.of(BULLISH, BEARISH), 4, series);
        Rule sma100IsInDownTrend = new BooleanIndicatorRule(new DownTrendIndicator(sma100, 70, 0.1));
        Rule sma25IsInDownTrend = new BooleanIndicatorRule(new DownTrendIndicator(sma25, 10, 0.1));

        Rule entryRule = // Buy rule:
                // (sma7 < sma100)
                new UnderIndicatorRule(sma7, sma100)
                // AND
                // (sma25 < sma100)
                .and(new UnderIndicatorRule(sma25, sma100))
                // AND
                // (sma100 is in downTrend(70, 0.1))
                .and(sma100IsInDownTrend)
                // AND
                // (sma25 is in downTrend(10, 0.1))
                .and(sma25IsInDownTrend)
                // AND
                // (strongBullishBar(4) crosses up sma7 and sma25)
                .and(new BooleanIndicatorRule(strongBullishBarIndicator))
                .and(new CrossedUpIndicatorRule(closePrice, sma7))
                .and(new CrossedUpIndicatorRule(closePrice, sma25))
                // AND
                // (closePrice < sma100)
                .and(new UnderIndicatorRule(closePrice, sma100));

        Rule exitRule = // Sell rule:
                // (closePrice crosses up sma100)
                new CrossedUpIndicatorRule(closePrice, sma100);

        return new BaseStrategy(strategyId, entryRule, exitRule);
    }

    @Override
    public List<Indicator<Num>> getNumIndicators()
    {
        return numIndicators;
    }
}

//    1m ETH/USD 1610196540000-1611950460000 results:
//
//    Total profit:	54.95
//    Average profit per trade:	5.495
//    N trades:	10
//    N profitable trades (UP):	9
//    N unprofitable trades (DOWN):	1
//    Risk/Reward ratio:	0.11

//    5m ETH/USD 1610196540000-1611950460000 results:
//
//    Total profit:	0.7
//    Average profit per trade:	0.35
//    N trades:	2
//    N profitable trades (UP):	1
//    N unprofitable trades (DOWN):	1
//    Risk/Reward ratio:	1.00

//    1m BTC/USD 1610196540000-1611950460000 results:
//
//    Total profit:	329.75
//    Average profit per trade:	17.355263157894736
//    N trades:	19
//    N profitable trades (UP):	13
//    N unprofitable trades (DOWN):	6
//    Risk/Reward ratio:	0.46

//    5m BTC/USD 1610196540000-1611950460000 results:
//
//    Total profit:	-84.5
//    Average profit per trade:	-21.125
//    N trades:	4
//    N profitable trades (UP):	2
//    N unprofitable trades (DOWN):	2
//    Risk/Reward ratio:	1.00

