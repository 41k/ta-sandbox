package root.domain.strategy.mess;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;
import root.domain.indicator.NumberIndicator;
import root.domain.level.MainChartLevelProvider;
import root.domain.rule.OverMainChartLevelRule;
import root.domain.rule.UnderMainChartLevelRule;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;

import static root.domain.indicator.NumberIndicators.ema;

// [!]
// ETH 5m
// entry rule is almost like in NewStrategy7 but gives slightly better performance
public class NewStrategy1Factory extends AbstractStrategyFactory
{
    private final ClosePriceIndicator closePrice;

    private final NumberIndicator ema50;
    private final NumberIndicator ema100;
    private final NumberIndicator ema200;
    private final NumberIndicator ema300;
    private final List<NumberIndicator> numberIndicators;

    private final MainChartLevelProvider takeProfitLevel;
    private final MainChartLevelProvider stopLossLevel;
    private final List<MainChartLevelProvider> mainChartLevelProviders;

    public NewStrategy1Factory(String strategyId, BarSeries series)
    {
        super(strategyId, series);

        this.closePrice = new ClosePriceIndicator(series);

        this.ema50 = ema(closePrice, 50);
        this.ema100 = ema(closePrice, 100);
        this.ema200 = ema(closePrice, 200);
        this.ema300 = ema(closePrice, 300);
        this.numberIndicators = List.of(ema50, ema100, ema200, ema300);

        this.takeProfitLevel = new MainChartLevelProvider("TP", this::calculateTakeProfitLevel);
        this.stopLossLevel = new MainChartLevelProvider("SL", this::calculateStopLossLevel);
        this.mainChartLevelProviders = List.of(takeProfitLevel, stopLossLevel);
    }

    @Override
    public Strategy create()
    {
        var entryRule = new OverIndicatorRule(ema50, ema100)
                .and(new OverIndicatorRule(ema100, ema200))
                .and(new OverIndicatorRule(ema200, ema300));

        var exitRule = new OverMainChartLevelRule(closePrice, takeProfitLevel)
                .or(new UnderMainChartLevelRule(closePrice, stopLossLevel));

        return new BaseStrategy(strategyId, entryRule, exitRule, 300);
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
        return closePrice.getValue(entryIndex).doubleValue() * 1.05; // 5% rise
    }

    private Double calculateStopLossLevel(Integer entryIndex)
    {
        var entryOrderPrice = closePrice.getValue(entryIndex).doubleValue();
        return entryOrderPrice - (entryOrderPrice * 0.45); // 45% drop
    }
}