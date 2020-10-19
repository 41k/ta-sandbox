package root.domain.analysis;

import java.util.*;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class TradesIntersectionsReportBuilder
{
    private static final String TRADES_INTERSECTION_PAIR_KEY_DELIMITER = " / ";

    public Map<String, Set<Long>> build(List<StrategyAnalysisReport> strategiesReports)
    {
        var sortedTrades = strategiesReports.stream()
                .map(StrategyAnalysisReport::getTrades)
                .flatMap(Collection::stream)
                .sorted(comparing(TradeHistoryItem::getEntryTimestamp))
                .collect(toList());
        if (sortedTrades.size() < 2)
        {
            return Map.of();
        }
        var tradesIntersectionsReport = new HashMap<String, Set<Long>>();
        var strategyIdToLastTradeMap = new HashMap<String, TradeHistoryItem>();
        sortedTrades.forEach(currentTrade -> {
            strategyIdToLastTradeMap.values()
                    .stream()
                    .filter(previousTrade -> intersectionBetween(previousTrade, currentTrade))
                    .forEach(previousTrade -> addIntersectionToReport(previousTrade, currentTrade, tradesIntersectionsReport));
            strategyIdToLastTradeMap.put(currentTrade.getStrategyId(), currentTrade);
        });
        return tradesIntersectionsReport;
    }

    private boolean intersectionBetween(TradeHistoryItem previousTrade, TradeHistoryItem currentTrade)
    {
        if (previousTrade.getStrategyId().equals(currentTrade.getStrategyId()))
        {
            return false;
        }
        return previousTrade.getEntryTimestamp() <= currentTrade.getEntryTimestamp() &&
                currentTrade.getEntryTimestamp() <= previousTrade.getExitTimestamp();
    }

    private void addIntersectionToReport(TradeHistoryItem previousTrade, TradeHistoryItem currentTrade, HashMap<String, Set<Long>> tradesIntersectionsReport)
    {
        var intersectionsPairKey = formTradesIntersectionsPairKey(previousTrade, currentTrade);
        var intersectionTimestamps = tradesIntersectionsReport.getOrDefault(intersectionsPairKey, new HashSet<>());
        intersectionTimestamps.add(currentTrade.getEntryTimestamp());
        tradesIntersectionsReport.put(intersectionsPairKey, intersectionTimestamps);
    }

    private String formTradesIntersectionsPairKey(TradeHistoryItem trade1, TradeHistoryItem trade2)
    {
        return List.of(trade1, trade2)
                .stream()
                .map(TradeHistoryItem::getStrategyId)
                .sorted()
                .collect(joining(TRADES_INTERSECTION_PAIR_KEY_DELIMITER));
    }
}
