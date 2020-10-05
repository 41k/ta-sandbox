package root.application;

import lombok.RequiredArgsConstructor;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Trade;
import org.ta4j.core.num.Num;
import root.application.model.Tick;
import root.application.model.TradeVisualization;
import root.domain.indicator.EMAIndicator;
import root.domain.indicator.Indicator;
import root.domain.indicator.OBVIndicator;
import root.domain.indicator.SMAIndicator;
import root.domain.indicator.macd.MACDIndicator;
import root.domain.indicator.macd.MACDLevelIndicator;
import root.domain.indicator.macd.MACDSignalLineIndicator;
import root.domain.indicator.rsi.RSIIndicator;
import root.domain.indicator.rsi.RSILevelIndicator;
import root.domain.level.Level;
import root.domain.strategy.StrategyFactory;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class TradeVisualizationBuilder
{
    private static final HashSet<Class<? extends Indicator<Num>>> MAIN_CHART_NUM_INDICATOR_TYPES = new HashSet<>(List.of(
            SMAIndicator.class, EMAIndicator.class
    ));
    private static final HashSet<Class<? extends Indicator<Num>>> ADDITIONAL_CHART_NUM_INDICATOR_TYPES = new HashSet<>(List.of(
            RSIIndicator.class, RSILevelIndicator.class,
            MACDIndicator.class, MACDSignalLineIndicator.class, MACDLevelIndicator.class,
            OBVIndicator.class
    ));

    private final int nTicksBeforeTrade;
    private final int nTicksAfterTrade;

    public List<TradeVisualization> build(List<Trade> trades, BarSeries series, StrategyFactory strategyFactory)
    {
        return trades.stream()
                .map(trade -> buildTradeVisualization(trade, series, strategyFactory))
                .collect(toList());
    }

    private TradeVisualization buildTradeVisualization(Trade trade, BarSeries series, StrategyFactory strategyFactory)
    {
        var ticksBeforeTrade = getTicksBeforeTrade(trade, series, strategyFactory);
        var ticksDuringTrade = getTicksDuringTrade(trade, series, strategyFactory);
        var ticksAfterTrade = getTicksAfterTrade(trade, series, strategyFactory);
        var ticks = Stream.of(ticksBeforeTrade, ticksDuringTrade, ticksAfterTrade)
                .flatMap(Collection::stream)
                .collect(toList());
        var entryTimestamp = getEntryTimestamp(ticksDuringTrade);
        var exitTimestamp = getExitTimestamp(ticksDuringTrade);
        return TradeVisualization.builder()
                .ticks(ticks)
                .strategyId(strategyFactory.getStrategyId())
                .entryTimestamp(entryTimestamp)
                .exitTimestamp(exitTimestamp)
                .profit(trade.getProfit().doubleValue())
                .build();
    }

    private List<Tick> getTicksBeforeTrade(Trade trade, BarSeries series, StrategyFactory strategyFactory)
    {
        var entryOrderIndex = trade.getEntry().getIndex();
        var seriesBeginIndex = series.getBeginIndex();
        if (entryOrderIndex == seriesBeginIndex)
        {
            return List.of();
        }
        var ticks = new ArrayList<Tick>();
        var bars = series.getBarData();
        var startIndex = Math.max(0, entryOrderIndex - nTicksBeforeTrade - 1);
        for (int i = startIndex; i < entryOrderIndex; i++)
        {
            var tick = getTickBuilder(i, bars, strategyFactory).build();
            ticks.add(tick);
        }
        return ticks;
    }

    private List<Tick> getTicksAfterTrade(Trade trade, BarSeries series, StrategyFactory strategyFactory)
    {
        var exitOrderIndex = trade.getExit().getIndex();
        var seriesEndIndex = series.getEndIndex();
        if (exitOrderIndex == seriesEndIndex)
        {
            return List.of();
        }
        var ticks = new ArrayList<Tick>();
        var bars = series.getBarData();
        var startIndex = exitOrderIndex + 1;
        var endIndex = Math.min(exitOrderIndex + nTicksAfterTrade + 1, seriesEndIndex);
        for (int i = startIndex; i <= endIndex; i++)
        {
            var tick = getTickBuilder(i, bars, strategyFactory).build();
            ticks.add(tick);
        }
        return ticks;
    }

    private List<Tick> getTicksDuringTrade(Trade trade, BarSeries series, StrategyFactory strategyFactory)
    {
        var ticks = new ArrayList<Tick>();
        var entryOrder = trade.getEntry();
        var exitOrder = trade.getExit();
        var entryOrderIndex = entryOrder.getIndex();
        var exitOrderIndex = exitOrder.getIndex();
        var bars = series.getBarData();
        for (int i = entryOrderIndex; i <= exitOrderIndex; i++)
        {
            var tickBuilder = getTickBuilder(i, bars, strategyFactory);
            if (i == entryOrderIndex)
            {
                tickBuilder.signal(entryOrder.getType())
                        .levels(getEntryOrderRelatedLevels(i, strategyFactory));
            }
            else if (i == exitOrderIndex)
            {
                tickBuilder.signal(exitOrder.getType());
            }
            ticks.add(tickBuilder.build());
        }
        return ticks;
    }

    private List<Level> getEntryOrderRelatedLevels(int index, StrategyFactory strategyFactory)
    {
        return strategyFactory.getStopLossLevelProvider()
                .map(levelProvider -> levelProvider.getLevel(index))
                .map(List::of)
                .orElseGet(List::of);
    }

    private Tick.TickBuilder getTickBuilder(int index, List<Bar> bars, StrategyFactory strategyFactory)
    {
        var bar = bars.get(index);
        var mainChartNumIndicators = getMainChartNumIndicators(index, strategyFactory);
        var additionalChartNumIndicators = getAdditionalChartNumIndicators(index, strategyFactory);
        return Tick.builder()
                .open(bar.getOpenPrice().doubleValue())
                .high(bar.getHighPrice().doubleValue())
                .low(bar.getLowPrice().doubleValue())
                .close(bar.getClosePrice().doubleValue())
                .volume(bar.getVolume().doubleValue())
                .timestamp(bar.getEndTime().toInstant().toEpochMilli())
                .mainChartNumIndicators(mainChartNumIndicators)
                .additionalChartNumIndicators(additionalChartNumIndicators);
    }

    private Map<String, Double> getMainChartNumIndicators(int index, StrategyFactory strategyFactory)
    {
        return getNumberIndicators(index, strategyFactory, MAIN_CHART_NUM_INDICATOR_TYPES);
    }

    private Map<String, Double> getAdditionalChartNumIndicators(int index, StrategyFactory strategyFactory)
    {
        return getNumberIndicators(index, strategyFactory, ADDITIONAL_CHART_NUM_INDICATOR_TYPES);
    }

    private Map<String, Double> getNumberIndicators(int index,
                                              StrategyFactory strategyFactory,
                                              HashSet<Class<? extends Indicator<Num>>> indicatorTypes)
    {
        var indicatorNameToValueMap = new LinkedHashMap<String, Double>();
        strategyFactory.getNumIndicators()
                .stream()
                .filter(indicator -> indicatorTypes.contains(indicator.getClass()))
                .forEach(indicator -> {
                    var indicatorName = indicator.getName();
                    var indicatorValue = getIndicatorValue(indicator, index);
                    indicatorNameToValueMap.put(indicatorName, indicatorValue);
                });
        return indicatorNameToValueMap;
    }

    private Double getIndicatorValue(Indicator<Num> indicator, int index)
    {
        var indicatorValue = indicator.getValue(index);
        return indicatorValue.isNaN() ? null : indicatorValue.doubleValue();
    }

    private Long getEntryTimestamp(List<Tick> ticksDuringTrade)
    {
        var entryTick = ticksDuringTrade.get(0);
        return entryTick.getTimestamp();
    }

    private Long getExitTimestamp(List<Tick> ticksDuringTrade)
    {
        var lastIndex = ticksDuringTrade.size() - 1;
        var exitTick = ticksDuringTrade.get(lastIndex);
        return exitTick.getTimestamp();
    }
}
