package root.application;

import lombok.RequiredArgsConstructor;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.Trade;
import org.ta4j.core.num.Num;
import root.application.model.Tick;
import root.application.model.TradeVisualization;
import root.domain.strategy.StrategyBuilder;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class TradeVisualizationBuilder
{
    private final int nTicksBeforeTrade;
    private final int nTicksAfterTrade;

    public List<TradeVisualization> build(List<Trade> trades, BarSeries series, StrategyBuilder strategyBuilder)
    {
        return trades.stream()
                .map(trade -> buildTradeVisualization(trade, series, strategyBuilder))
                .collect(toList());
    }

    private TradeVisualization buildTradeVisualization(Trade trade, BarSeries series, StrategyBuilder strategyBuilder)
    {
        var ticksBeforeTrade = getTicksBeforeTrade(trade, series, strategyBuilder);
        var ticksDuringTrade = getTicksDuringTrade(trade, series, strategyBuilder);
        var ticksAfterTrade = getTicksAfterTrade(trade, series, strategyBuilder);
        var ticks = Stream.of(ticksBeforeTrade, ticksDuringTrade, ticksAfterTrade)
                .flatMap(Collection::stream)
                .collect(toList());
        return TradeVisualization.builder()
                .ticks(ticks)
                .entryIndex(trade.getEntry().getIndex())
                .exitIndex(trade.getExit().getIndex())
                .profit(trade.getProfit().doubleValue())
                .build();
    }

    private List<Tick> getTicksBeforeTrade(Trade trade, BarSeries series, StrategyBuilder strategyBuilder)
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
            var tick = buildTick(i, bars, strategyBuilder);
            ticks.add(tick);
        }
        return ticks;
    }

    private List<Tick> getTicksAfterTrade(Trade trade, BarSeries series, StrategyBuilder strategyBuilder)
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
            var tick = buildTick(i, bars, strategyBuilder);
            ticks.add(tick);
        }
        return ticks;
    }

    private List<Tick> getTicksDuringTrade(Trade trade, BarSeries series, StrategyBuilder strategyBuilder)
    {
        var ticks = new ArrayList<Tick>();
        var entryOrder = trade.getEntry();
        var exitOrder = trade.getExit();
        var entryOrderIndex = entryOrder.getIndex();
        var exitOrderIndex = exitOrder.getIndex();
        var bars = series.getBarData();
        for (int i = entryOrderIndex; i <= exitOrderIndex; i++)
        {
            var tick = buildTick(i, bars, strategyBuilder);
            if (i == entryOrderIndex)
            {
                tick.setSignal(entryOrder.getType());
            }
            else if (i == exitOrderIndex)
            {
                tick.setSignal(exitOrder.getType());
            }
            ticks.add(tick);
        }
        return ticks;
    }

    private Tick buildTick(int index, List<Bar> bars, StrategyBuilder strategyBuilder)
    {
        var bar = bars.get(index);
        var indicators = getIndicators(index, strategyBuilder);
        return Tick.builder()
                .open(bar.getOpenPrice().doubleValue())
                .high(bar.getHighPrice().doubleValue())
                .low(bar.getLowPrice().doubleValue())
                .close(bar.getClosePrice().doubleValue())
                .volume(bar.getVolume().doubleValue())
                .timestamp(bar.getEndTime().toInstant().toEpochMilli())
                .indicators(indicators)
                .build();
    }

    private Map<String, Double> getIndicators(int index, StrategyBuilder strategyBuilder)
    {
        var indicatorNameToValueMap = new LinkedHashMap<String, Double>();
        var nameToIndicatorMap = strategyBuilder.getNumIndicators().orElseGet(Map::of);
        for (Map.Entry<String, Indicator<Num>> entry : nameToIndicatorMap.entrySet())
        {
            var indicatorName = entry.getKey();
            var indicator = entry.getValue();
            var indicatorValue = getIndicatorValue(indicator, index);
            indicatorNameToValueMap.put(indicatorName, indicatorValue);
        }
        return indicatorNameToValueMap;
    }

    private Double getIndicatorValue(Indicator<Num> indicator, int index)
    {
        var indicatorValue = indicator.getValue(index);
        return indicatorValue.isNaN() ? null : indicatorValue.doubleValue();
    }
}
