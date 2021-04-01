package root.domain.strategy.level;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.HighestValueIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import root.domain.indicator.Indicator;
import root.domain.indicator.wri.WRIndicator;
import root.domain.indicator.wri.WRLevelIndicator;
import root.domain.level.MainChartLevelProvider;
import root.domain.rule.StopLossLevelRule;
import root.domain.rule.TakeProfitLevelRule;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;

public class SL_TP_StrategyFactory extends AbstractStrategyFactory
{
    private final ClosePriceIndicator closePrice;
    private final HighPriceIndicator highPrice;
    private final WRIndicator wr;
    private final WRLevelIndicator wrLevelMinus10;
    private final WRLevelIndicator wrLevelMinus90;
    private final List<Indicator<Num>> numIndicators;
    private final MainChartLevelProvider stopLossLevelProvider;
    private final MainChartLevelProvider takeProfitLevelProvider;
    private final List<MainChartLevelProvider> mainChartLevelProviders;

    public SL_TP_StrategyFactory(String strategyId, BarSeries series)
    {
        super(strategyId, series);
        this.closePrice = new ClosePriceIndicator(series);
        this.highPrice = new HighPriceIndicator(series);
        this.wr = new WRIndicator(series, 14);
        this.wrLevelMinus10 = new WRLevelIndicator(series, series.numOf(-10));
        this.wrLevelMinus90 = new WRLevelIndicator(series, series.numOf(-90));
        this.numIndicators = List.of(wr, wrLevelMinus10, wrLevelMinus90);
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
    public List<Indicator<Num>> getNumIndicators()
    {
        return numIndicators;
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