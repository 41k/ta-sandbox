package root.domain.strategy.price_action;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.helpers.PreviousValueIndicator;
import org.ta4j.core.trading.rules.BooleanIndicatorRule;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import root.domain.indicator.helpers.PreviousBooleanIndicator;
import root.domain.indicator.price_action.MasterCandleIndicator;
import root.domain.level.MainChartLevelProvider;
import root.domain.rule.UnderMainChartLevelRule;
import root.domain.rule.OverMainChartLevelRule;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;

public class MasterCandleStrategyFactory extends AbstractStrategyFactory
{
    private final HighPriceIndicator highPrice;
    private final LowPriceIndicator lowPrice;
    private final ClosePriceIndicator closePrice;
    private final MasterCandleIndicator masterCandle5;
    private final MasterCandleIndicator masterCandle6;
    private final MasterCandleIndicator masterCandle7;

    private final MainChartLevelProvider stopLossLevelProvider;
    private final MainChartLevelProvider takeProfitLevelProvider;
    private final List<MainChartLevelProvider> mainChartLevelProviders;

    public MasterCandleStrategyFactory(String strategyId, BarSeries series)
    {
        super(strategyId, series);
        this.highPrice = new HighPriceIndicator(series);
        this.lowPrice = new LowPriceIndicator(series);
        this.closePrice = new ClosePriceIndicator(series);
        var masterCandleMinBodySize = 15;
        this.masterCandle5 = new MasterCandleIndicator(5, masterCandleMinBodySize, series);
        this.masterCandle6 = new MasterCandleIndicator(6, masterCandleMinBodySize, series);
        this.masterCandle7 = new MasterCandleIndicator(7, masterCandleMinBodySize, series);
        this.takeProfitLevelProvider = new MainChartLevelProvider("TP", this::calculateTakeProfitLevel);
        this.stopLossLevelProvider = new MainChartLevelProvider("SL", this::calculateStopLossLevel);
        this.mainChartLevelProviders = List.of(takeProfitLevelProvider, stopLossLevelProvider);
    }

    @Override
    public Strategy create()
    {
        var masterCandleBullishBreakoutOn5thBar =
                new OverIndicatorRule(closePrice, new PreviousValueIndicator(highPrice, 5))
                .and(new BooleanIndicatorRule(new PreviousBooleanIndicator(masterCandle5)));
        var masterCandleBullishBreakoutOn6thBar =
                new OverIndicatorRule(closePrice, new PreviousValueIndicator(highPrice, 6))
                .and(new BooleanIndicatorRule(new PreviousBooleanIndicator(masterCandle6)));
        var masterCandleBullishBreakoutOn7thBar =
                new OverIndicatorRule(closePrice, new PreviousValueIndicator(highPrice, 7))
                .and(new BooleanIndicatorRule(new PreviousBooleanIndicator(masterCandle7)));

        var entryRule = masterCandleBullishBreakoutOn5thBar
                .or(masterCandleBullishBreakoutOn6thBar)
                .or(masterCandleBullishBreakoutOn7thBar);

        var exitRule = new OverMainChartLevelRule(closePrice, takeProfitLevelProvider)
                .or(new UnderMainChartLevelRule(lowPrice, stopLossLevelProvider));

        return new BaseStrategy(strategyId, entryRule, exitRule);
    }

    @Override
    public List<MainChartLevelProvider> getMainChartLevelProviders()
    {
        return mainChartLevelProviders;
    }

    private Double calculateTakeProfitLevel(Integer entryIndex)
    {
        var entryBar = series.getBar(entryIndex);
        var masterCandle = getMasterCandle(entryIndex);
        var masterCandleBodySize = masterCandle.isBullish() ?
                masterCandle.getClosePrice().minus(masterCandle.getOpenPrice()) :
                masterCandle.getOpenPrice().minus(masterCandle.getClosePrice());
        return entryBar.getHighPrice().plus(masterCandleBodySize).doubleValue();
    }

    private Double calculateStopLossLevel(Integer entryIndex)
    {
        return getMasterCandle(entryIndex).getLowPrice().doubleValue();
    }

    private Bar getMasterCandle(Integer entryIndex)
    {
        var masterCandleIndex = findMasterCandleIndex(entryIndex);
        return series.getBar(masterCandleIndex);
    }

    private int findMasterCandleIndex(Integer entryIndex)
    {
        if (new PreviousBooleanIndicator(masterCandle5).getValue(entryIndex))
        {
            return entryIndex - 5;
        }
        if (new PreviousBooleanIndicator(masterCandle6).getValue(entryIndex))
        {
            return entryIndex - 6;
        }
        if (new PreviousBooleanIndicator(masterCandle7).getValue(entryIndex))
        {
            return entryIndex - 7;
        }
        throw new IllegalStateException();
    }
}