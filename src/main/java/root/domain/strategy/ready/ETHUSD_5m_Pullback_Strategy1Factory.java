package root.domain.strategy.ready;

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

import static root.domain.indicator.NumberIndicators.ema;

public class ETHUSD_5m_Pullback_Strategy1Factory extends AbstractStrategyFactory
{
    private final ClosePriceIndicator closePrice;
    private final NumberIndicator ema50;
    private final NumberIndicator ema100;
    private final NumberIndicator ema200;
    private final NumberIndicator ema400;
    private final List<NumberIndicator> numberIndicators;

    private final MainChartLevelProvider takeProfitLevel;
    private final List<MainChartLevelProvider> mainChartLevelProviders;

    public ETHUSD_5m_Pullback_Strategy1Factory(String strategyId, BarSeries series)
    {
        super(strategyId, series);
        this.closePrice = new ClosePriceIndicator(series);

        this.ema50 = ema(closePrice, 50);
        this.ema100 = ema(closePrice, 100);
        this.ema200 = ema(closePrice, 200);
        this.ema400 = ema(closePrice, 400);
        this.numberIndicators = List.of(ema50, ema100, ema200, ema400);

        this.takeProfitLevel = new MainChartLevelProvider("TP",
                entryIndex -> closePrice.getValue(entryIndex).doubleValue() + 30);

        this.mainChartLevelProviders = List.of(takeProfitLevel);
    }

    @Override
    public Strategy create()
    {
        var entryRule = new OverIndicatorRule(ema50, ema100)
                .and(new OverIndicatorRule(ema100, ema200))
                .and(new OverIndicatorRule(ema200, ema400))
                .and(new UnderIndicatorRule(closePrice, ema400));

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
}