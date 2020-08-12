package root.application;

import lombok.RequiredArgsConstructor;
import org.ta4j.core.*;
import root.domain.strategy.BullishReboundStrategy;
import root.domain.strategy.ResistanceBreakthroughStrategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class TradeVisualizationService
{
    private static final int N_TICKS_BEFORE_TRADE = 10;
    private static final int N_TICKS_AFTER_TRADE = 5;

    private final BarProvider barProvider;

    public List<TradeVisualization> getTrades()
    {
        var bars = barProvider.getMinuteBars();
        var series = new BaseBarSeries(bars);
        //var strategy = BullishReboundStrategy.buildStrategy(series);
        var strategy = ResistanceBreakthroughStrategy.buildStrategy(series);
        var seriesManager = new BarSeriesManager(series);
        var tradingRecord = seriesManager.run(strategy);
        var trades = tradingRecord.getTrades();
        return trades.stream()
                .map(trade -> buildTradeVisualization(trade, series))
                .collect(toList());
    }

    private TradeVisualization buildTradeVisualization(Trade trade, BarSeries series)
    {
        var ticksBeforeTrade = getTicksBeforeTrade(trade, series);
        var ticksDuringTrade = getTicksDuringTrade(trade, series);
        var ticksAfterTrade = getTicksAfterTrade(trade, series);
        var ticks = Stream.of(ticksBeforeTrade, ticksDuringTrade, ticksAfterTrade)
                .flatMap(Collection::stream)
                .collect(toList());
        return TradeVisualization.builder()
                .ticks(ticks)
                .profit(trade.getProfit().doubleValue())
                .build();
    }

    private List<Tick> getTicksBeforeTrade(Trade trade, BarSeries series)
    {
        var entryIndex = trade.getEntry().getIndex();
        var seriesBeginIndex = series.getBeginIndex();
        if (entryIndex == seriesBeginIndex)
        {
            return List.of();
        }
        var bars = series.getBarData();
        if (entryIndex + 1 < N_TICKS_BEFORE_TRADE)
        {
            return bars.stream().map(this::buildTick).limit(entryIndex).collect(toList());
        }
        else
        {
            var endIndex = entryIndex;
            var startIndex = entryIndex - N_TICKS_BEFORE_TRADE;
            return bars.subList(startIndex, endIndex).stream().map(this::buildTick).collect(toList());
        }
    }

    private List<Tick> getTicksAfterTrade(Trade trade, BarSeries series)
    {
        var exitIndex = trade.getExit().getIndex();
        var seriesEndIndex = series.getEndIndex();
        if (exitIndex == seriesEndIndex)
        {
            return List.of();
        }
        var bars = series.getBarData();
        if (exitIndex >= seriesEndIndex - N_TICKS_AFTER_TRADE)
        {
            var startIndex = exitIndex + 1;
            var endIndex = seriesEndIndex + 1;
            return bars.subList(startIndex, endIndex).stream().map(this::buildTick).collect(toList());
        }
        else
        {
            var startIndex = exitIndex + 1;
            var endIndex = startIndex + N_TICKS_AFTER_TRADE;
            return bars.subList(startIndex, endIndex).stream().map(this::buildTick).collect(toList());
        }
    }

    private List<Tick> getTicksDuringTrade(Trade trade, BarSeries series)
    {
        var ticks = new ArrayList<Tick>();
        var entry = trade.getEntry();
        var exit = trade.getExit();
        var startIndex = entry.getIndex();
        var endIndex = exit.getIndex() + 1;
        var bars = series.getBarData().subList(startIndex, endIndex);
        var entryIndex = 0;
        var exitIndex = bars.size() - 1;
        for (int i = 0; i < bars.size(); i++)
        {
            var tick = buildTick(bars.get(i));
            if (i == entryIndex)
            {
                tick.setSignal(entry.getType());
            }
            else if (i == exitIndex)
            {
                tick.setSignal(exit.getType());
            }
            ticks.add(tick);
        }
        return ticks;
    }

    private Tick buildTick(Bar bar)
    {
        return Tick.builder()
                .open(bar.getOpenPrice().doubleValue())
                .high(bar.getHighPrice().doubleValue())
                .low(bar.getLowPrice().doubleValue())
                .close(bar.getClosePrice().doubleValue())
                .volume(bar.getVolume().doubleValue())
                .timestamp(bar.getEndTime().toInstant().toEpochMilli())
                .build();
    }
}
