package root.domain.strategy.level;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.HighestValueIndicator;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import root.domain.indicator.NumberIndicator;
import root.domain.level.MainChartLevelProvider;
import root.domain.rule.StopLossLevelRule;
import root.domain.rule.TakeProfitLevelRule;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;

import static root.domain.indicator.NumberIndicators.williamsR;
import static root.domain.indicator.NumberIndicators.williamsRLevel;

public class SL_TP_StrategyFactory extends AbstractStrategyFactory
{
    private final ClosePriceIndicator closePrice;
    private final HighPriceIndicator highPrice;

    private final NumberIndicator wr;
    private final NumberIndicator wrLevelMinus10;
    private final NumberIndicator wrLevelMinus90;
    private final List<NumberIndicator> numberIndicators;

    private final MainChartLevelProvider stopLossLevelProvider;
    private final MainChartLevelProvider takeProfitLevelProvider;
    private final List<MainChartLevelProvider> mainChartLevelProviders;

    public SL_TP_StrategyFactory(String strategyId, BarSeries series)
    {
        super(strategyId, series);
        this.closePrice = new ClosePriceIndicator(series);
        this.highPrice = new HighPriceIndicator(series);
        this.wr = williamsR(10, series);
        this.wrLevelMinus10 = williamsRLevel(-10, series);
        this.wrLevelMinus90 = williamsRLevel(-90, series);
        this.numberIndicators = List.of(wr, wrLevelMinus10, wrLevelMinus90);
        this.takeProfitLevelProvider = new MainChartLevelProvider("TP", this::calculateTakeProfitLevel);
        this.stopLossLevelProvider = new MainChartLevelProvider("SL", this::calculateStopLossLevel);
        this.mainChartLevelProviders = List.of(takeProfitLevelProvider, stopLossLevelProvider);
    }

    @Override
    public Strategy create()
    {
        Rule entryRule = new CrossedUpIndicatorRule(wr, wrLevelMinus90);

        Rule exitRule = new TakeProfitLevelRule(closePrice, takeProfitLevelProvider)
                .or(new StopLossLevelRule(closePrice, stopLossLevelProvider));

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

    private Double calculateStopLossLevel(Integer entryIndex)
    {
        return closePrice.getValue(entryIndex).doubleValue() - 10;
    }

    private Double calculateTakeProfitLevel(Integer entryIndex)
    {
        return new HighestValueIndicator(highPrice, 11).getValue(entryIndex).doubleValue();
    }
}