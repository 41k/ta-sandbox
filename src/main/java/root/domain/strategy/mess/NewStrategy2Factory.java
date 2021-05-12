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
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;

import static root.domain.indicator.NumberIndicators.rsi;
import static root.domain.indicator.NumberIndicators.rsiLevel;

// [!]
// ETH 4h
// rsi > 60 or 70 (60 level provides better results for ETH)
public class NewStrategy2Factory extends AbstractStrategyFactory
{
    private final ClosePriceIndicator closePrice;

    private final NumberIndicator rsi;
    private final NumberIndicator rsiLevel70;
    private final NumberIndicator rsiLevel60;
    private final NumberIndicator rsiLevel55;
    private final NumberIndicator rsiLevel40;
    private final NumberIndicator rsiLevel30;
    private final NumberIndicator rsiLevel20;
    private final NumberIndicator rsiLevel10;
    private final List<NumberIndicator> numberIndicators;

    private final MainChartLevelProvider takeProfitLevel;
    private final List<MainChartLevelProvider> mainChartLevelProviders;

    public NewStrategy2Factory(String strategyId, BarSeries series)
    {
        super(strategyId, series);

        this.closePrice = new ClosePriceIndicator(series);

        this.rsi = rsi(new ClosePriceIndicator(series), 14);
        this.rsiLevel70 = rsiLevel(70, series);
        this.rsiLevel60 = rsiLevel(60, series);
        this.rsiLevel55 = rsiLevel(55, series);
        this.rsiLevel40 = rsiLevel(40, series);
        this.rsiLevel30 = rsiLevel(30, series);
        this.rsiLevel20 = rsiLevel(20, series);
        this.rsiLevel10 = rsiLevel(10, series);

        this.numberIndicators = List.of(rsi, rsiLevel70, rsiLevel20);

        this.takeProfitLevel = new MainChartLevelProvider("TP", this::calculateTakeProfitLevel);
        this.mainChartLevelProviders = List.of(takeProfitLevel);
    }

    @Override
    public Strategy create()
    {
        var entryRule = new OverIndicatorRule(rsi, rsiLevel60);

        var exitRule = new OverMainChartLevelRule(closePrice, takeProfitLevel)
                .or(new UnderIndicatorRule(rsi, rsiLevel20));

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
        return closePrice.getValue(entryIndex).doubleValue() * 1.03; // 3% rise
    }
}