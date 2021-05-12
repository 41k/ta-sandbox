package root.domain.strategy.mess;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.HighestValueIndicator;
import org.ta4j.core.indicators.helpers.PreviousValueIndicator;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import root.domain.level.MainChartLevelProvider;
import root.domain.rule.OverMainChartLevelRule;
import root.domain.rule.UnderMainChartLevelRule;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;

// [!]
// ETH 1d
// --
// Strategy name in NT = HighestHighBreakout
// --
// According to NT Optimization testing the best TP:SL is 7%:10% for ETH, also 6%:12% works well
// --
// OverIndicatorRule in entry rule allows us to make more trades during up trend than CrossedUpIndicatorRule
public class NewStrategy4Factory extends AbstractStrategyFactory
{
    private final HighPriceIndicator highPrice;
    private final ClosePriceIndicator closePrice;

    private final MainChartLevelProvider takeProfitLevel;
    private final MainChartLevelProvider stopLossLevel;
    private final List<MainChartLevelProvider> mainChartLevelProviders;

    public NewStrategy4Factory(String strategyId, BarSeries series)
    {
        super(strategyId, series);

        this.highPrice = new HighPriceIndicator(series);
        this.closePrice = new ClosePriceIndicator(series);

        this.takeProfitLevel = new MainChartLevelProvider("TP", this::calculateTakeProfitLevel);
        this.stopLossLevel = new MainChartLevelProvider("SL", this::calculateStopLossLevel);
        this.mainChartLevelProviders = List.of(takeProfitLevel, stopLossLevel);
    }

    @Override
    public Strategy create()
    {
        var highestHigh = new PreviousValueIndicator(new HighestValueIndicator(highPrice, 30));
//        var entryRule = new CrossedUpIndicatorRule(closePrice, highestHigh);
        var entryRule = new OverIndicatorRule(closePrice, highestHigh);
        var exitRule = new OverMainChartLevelRule(closePrice, takeProfitLevel)
                .or(new UnderMainChartLevelRule(closePrice, stopLossLevel));
        return new BaseStrategy(strategyId, entryRule, exitRule, 31);
    }

    @Override
    public List<MainChartLevelProvider> getMainChartLevelProviders()
    {
        return mainChartLevelProviders;
    }

    private Double calculateTakeProfitLevel(Integer entryIndex)
    {
        var entryOrderPrice = closePrice.getValue(entryIndex).doubleValue();
        return entryOrderPrice + (entryOrderPrice * 0.07); // 7% rise
    }

    private Double calculateStopLossLevel(Integer entryIndex)
    {
        var entryOrderPrice = closePrice.getValue(entryIndex).doubleValue();
        return entryOrderPrice - (entryOrderPrice * 0.10); // 10% drop
    }
}