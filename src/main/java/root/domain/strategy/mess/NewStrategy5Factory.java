package root.domain.strategy.mess;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.PreviousValueIndicator;
import org.ta4j.core.trading.rules.CrossedDownIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;
import root.domain.indicator.NumberIndicator;
import root.domain.level.MainChartLevelProvider;
import root.domain.rule.OverMainChartLevelRule;
import root.domain.rule.SequenceBooleanRule;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;

import static root.domain.indicator.NumberIndicators.*;

public class NewStrategy5Factory extends AbstractStrategyFactory
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

    public NewStrategy5Factory(String strategyId, BarSeries series)
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

        this.numberIndicators = List.of(ema50, ema100, ema200, ema300);

        this.takeProfitLevel = new MainChartLevelProvider("TP", this::calculateTakeProfitLevel);
        this.mainChartLevelProviders = List.of(takeProfitLevel);
    }

    @Override
    public Strategy create()
    {
//        var entryRule = new UnderIndicatorRule(ema50, ema100)
//                .and(new UnderIndicatorRule(ema100, ema200))
//                .and(new UnderIndicatorRule(ema200, ema400))
//                .and(new OverIndicatorRule(closePrice, ema400));
//        var entryRule = new UnderIndicatorRule(ema100, ema200)
//                .and(new UnderIndicatorRule(ema200, ema400))
//                .and(new OverIndicatorRule(ema50, ema200));

//        TP=200
//        var entryRule = new SequenceBooleanRule(new UnderIndicatorRule(ema300, ema400), 800);

//        TP=200
//        var entryRule = new CrossedUpIndicatorRule(ema300, ema400)
//                .and(new SequenceBooleanRule(new UnderIndicatorRule(new PreviousValueIndicator(ema300), new PreviousValueIndicator(ema400)), 100));

//        TP=20
//        var entryRule = new OverIndicatorRule(ema400, ema200)
//                .and(new OverIndicatorRule(ema200, ema100))
//                .and(new OverIndicatorRule(ema100, ema50))
//                .and(new CrossedUpIndicatorRule(closePrice, ema100));

//        TP=25
        var entryRule = new OverIndicatorRule(ema300, ema200)
                .and(new OverIndicatorRule(ema200, ema100))
                .and(new OverIndicatorRule(ema100, ema50))
                .and(new CrossedUpIndicatorRule(closePrice, ema100));

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
        return closePrice.getValue(entryIndex).doubleValue() + 25;
    }
}