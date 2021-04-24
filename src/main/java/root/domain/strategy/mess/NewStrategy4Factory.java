package root.domain.strategy.mess;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.helpers.*;
import org.ta4j.core.trading.rules.CrossedDownIndicatorRule;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;
import root.domain.indicator.NumberIndicator;
import root.domain.level.MainChartLevelProvider;
import root.domain.rule.OverMainChartLevelRule;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;

import static root.domain.indicator.NumberIndicators.*;

public class NewStrategy4Factory extends AbstractStrategyFactory
{
    private final ClosePriceIndicator closePrice;
    private final NumberIndicator sma50;
    private final NumberIndicator sma100;
    private final NumberIndicator sma200;
    private final NumberIndicator ema50;
    private final NumberIndicator ema100;
    private final NumberIndicator ema200;
    private final NumberIndicator ema300;
    private final NumberIndicator ema400;
    private final NumberIndicator tema;
    private final NumberIndicator trendLine;
    private final NumberIndicator trendOscillator;
    private final NumberIndicator wr;
    private final NumberIndicator wrLevel;
    private final NumberIndicator adx;
    private final NumberIndicator adxLevel;
    private final List<NumberIndicator> numberIndicators;

    private final MainChartLevelProvider takeProfitLevel;
    private final List<MainChartLevelProvider> mainChartLevelProviders;

    public NewStrategy4Factory(String strategyId, BarSeries series)
    {
        super(strategyId, series);
        this.closePrice = new ClosePriceIndicator(series);

        this.sma50 = sma(closePrice, 50);
        this.sma100 = sma(closePrice, 100);
        this.sma200 = sma(closePrice, 200);
        this.ema50 = ema(closePrice, 50);
        this.ema100 = ema(closePrice, 100);
        this.ema200 = ema(closePrice, 200);
        this.ema300 = ema(closePrice, 300);
        this.ema400 = ema(closePrice, 400);

        this.tema = tema(closePrice, 14);

        this.trendLine = trendLine(series, true);
        this.trendOscillator = trendOscillator(series, true);

        this.wr = williamsR(20, series);
        this.wrLevel = williamsRLevel(-20, series);

        this.adx = adx(series, 20, 20);
        this.adxLevel = adxLevel(25, series);

        this.numberIndicators = List.of(ema50, ema100, ema200, ema400);

        this.takeProfitLevel = new MainChartLevelProvider("TP", this::calculateTakeProfitLevel);
        this.mainChartLevelProviders = List.of(takeProfitLevel);
    }

    @Override
    public Strategy create()
    {
//        var entryRule = new OverIndicatorRule(sma200, sma50)
//                .and(new OverIndicatorRule(sma200, sma100))
//                .and(new OverIndicatorRule(sma50, sma100));
//        var entryRule = new OverIndicatorRule(sma100, sma50);
//        var entryRule = new OverIndicatorRule(trendLine, new PreviousValueIndicator(trendLine));
//        var entryRule = new OverIndicatorRule(new DifferenceIndicator(sma100, closePrice), 30);
//        var entryRule = new OverIndicatorRule(sma50, sma100).and(new UnderIndicatorRule(closePrice, sma50));
//        var entryRule = new OverIndicatorRule(trendOscillator, 0)
//                .and(new UnderIndicatorRule(new PreviousValueIndicator(trendOscillator), 0));
//        var entryRule = new CrossedUpIndicatorRule(closePrice, trendLine);
//        var entryRule = new UnderIndicatorRule(closePrice, sma50);

//        var lowestPriceOfShortPeriod = new LowestValueIndicator(closePrice, 3);
//        var lowestPriceOfLongPeriod = new LowestValueIndicator(closePrice, 200);
//        var entryRule = new UnderIndicatorRule(lowestPriceOfShortPeriod, new PreviousValueIndicator(lowestPriceOfLongPeriod, 1))
//                .or(new UnderIndicatorRule(lowestPriceOfShortPeriod, new PreviousValueIndicator(lowestPriceOfLongPeriod, 2)))
//                .or(new UnderIndicatorRule(lowestPriceOfShortPeriod, new PreviousValueIndicator(lowestPriceOfLongPeriod, 3)));

//        var entryRule = new BooleanIndicatorRule(new ConstantIndicator<>(series, TRUE));

//        var entryRule = new OverIndicatorRule(tema, new PreviousValueIndicator(tema));

//        var entryRule = new CrossedUpIndicatorRule(wr, wrLevel);

//        var entryRule = new CrossedUpIndicatorRule(wr, wrLevel)
//                .and(new OverIndicatorRule(tema, new PreviousValueIndicator(tema)));

//        var entryRule = new OverIndicatorRule(trendLine, new PreviousValueIndicator(trendLine))
//                .and(new OverIndicatorRule(adx, adxLevel));

//        var entryRule = new OverIndicatorRule(trendOscillator, 0)
//                .and(new UnderIndicatorRule(new PreviousValueIndicator(trendOscillator), 0))
//                .and(new OverIndicatorRule(adx, adxLevel));

//        var entryRule = new OverIndicatorRule(adx, adxLevel).and(new OverIndicatorRule(wr, wrLevel));


        // TP=40 --> a=1, c=0.002, ds2: t=15 p=572
        // TP=30 --> a=1, c=0.002, ds2: t=20 p=527
        // TP=20 --> a=1, c=0.002, ds2: t=26 p=425
        // TP=10 --> a=1, c=0.002, ds2: t=33 p=191
//        var entryRule = new OverIndicatorRule(ema50, ema100)
//                .and(new OverIndicatorRule(ema100, ema200))
//                .and(new OverIndicatorRule(ema200, ema400))
//                .and(new UnderIndicatorRule(closePrice, ema400));

//        //TP=40 --> a=1, c=0.002, ds2: t=14 p=507, ds1: t=11 p=434
//        //TP=30 --> a=1, c=0.002, ds2: t=18 p=476, ds1: t=13 p=378
//        //TP=20 --> a=1, c=0.002, ds2: t=40 p=616, ds1: t=25 p=463 !!!
//        //TP=15 --> a=1, c=0.002, ds2: t=51 p=526, ds1: t=26 p=336
//        //TP=10 --> a=1, c=0.002, ds2: t=65 p=332, ds1: t=31 p=243
        var entryRule = new OverIndicatorRule(ema50, ema100)
                .and(new OverIndicatorRule(ema100, ema200))
                .and(new OverIndicatorRule(ema200, ema300))
                .and(new UnderIndicatorRule(closePrice, ema200));

        var exitRule = new OverMainChartLevelRule(closePrice, takeProfitLevel);
        return new BaseStrategy(strategyId, entryRule, exitRule);
    }

    @Override
    public List<NumberIndicator> getNumberIndicators()
    {
        return numberIndicators;
    }

    @Override
    public List<MainChartLevelProvider> getMainChartLevelProviders()
    {
        return mainChartLevelProviders;
    }

    private Double calculateTakeProfitLevel(Integer entryIndex)
    {
        return closePrice.getValue(entryIndex).doubleValue() + 20;
    }
}