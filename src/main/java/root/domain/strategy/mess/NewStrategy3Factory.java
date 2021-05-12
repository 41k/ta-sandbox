package root.domain.strategy.mess;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import root.domain.indicator.NumberIndicator;
import root.domain.level.MainChartLevelProvider;
import root.domain.rule.OverMainChartLevelRule;
import root.domain.rule.UnderMainChartLevelRule;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;

import static root.domain.indicator.NumberIndicators.*;

// [!]
// ETH 1h
// Strategy name in NT = DownTrendRebound2
public class NewStrategy3Factory extends AbstractStrategyFactory
{
    private final ClosePriceIndicator closePrice;

    private final NumberIndicator ema9;
    private final NumberIndicator ema50;
    private final NumberIndicator ema100;
    private final NumberIndicator ema200;

    private final NumberIndicator atr;

    private final NumberIndicator rsi;
    private final NumberIndicator rsiLevel70;
    private final NumberIndicator rsiLevel65;
    private final NumberIndicator rsiLevel50;
    private final NumberIndicator rsiLevel35;
    private final NumberIndicator rsiLevel30;
    private final NumberIndicator rsiLevel20;
    private final List<NumberIndicator> numberIndicators;

    private final MainChartLevelProvider takeProfitLevel;
    private final MainChartLevelProvider stopLossLevel;
    private final List<MainChartLevelProvider> mainChartLevelProviders;

    public NewStrategy3Factory(String strategyId, BarSeries series)
    {
        super(strategyId, series);

        this.closePrice = new ClosePriceIndicator(series);

        this.atr = atr(7, series);

        this.ema9 = ema(closePrice, 9);
        this.ema50 = ema(closePrice, 50);
        this.ema100 = ema(closePrice, 100);
        this.ema200 = ema(closePrice, 200);
        this.rsi = rsi(new ClosePriceIndicator(series), 7);
        this.rsiLevel70 = rsiLevel(70, series);
        this.rsiLevel65 = rsiLevel(65, series);
        this.rsiLevel50 = rsiLevel(50, series);
        this.rsiLevel35 = rsiLevel(35, series);
        this.rsiLevel30 = rsiLevel(30, series);
        this.rsiLevel20 = rsiLevel(20, series);
        this.numberIndicators = List.of(ema9, ema50, ema100, ema200, rsi, rsiLevel70);

        this.takeProfitLevel = new MainChartLevelProvider("TP", this::calculateTakeProfitLevel);
        this.stopLossLevel = new MainChartLevelProvider("SL", this::calculateStopLossLevel);
        this.mainChartLevelProviders = List.of(takeProfitLevel, stopLossLevel);
    }

    @Override
    public Strategy create()
    {
        var entryRule = new OverIndicatorRule(ema200, ema100).and(new OverIndicatorRule(ema100, ema50))
                .and(new CrossedUpIndicatorRule(ema9, ema50))
                .and(new OverIndicatorRule(rsi, rsiLevel70));

        var exitRule = new OverMainChartLevelRule(closePrice, takeProfitLevel)
                .or(new UnderMainChartLevelRule(closePrice, stopLossLevel));

        return new BaseStrategy(strategyId, entryRule, exitRule, 200);
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
        var entryOrderPrice = closePrice.getValue(entryIndex).doubleValue();
        return entryOrderPrice + (entryOrderPrice * 0.06); // 6% rise
    }

    private Double calculateStopLossLevel(Integer entryIndex)
    {
        var entryOrderPrice = closePrice.getValue(entryIndex).doubleValue();
        return entryOrderPrice - (entryOrderPrice * 0.04); // 4% drop
    }
}