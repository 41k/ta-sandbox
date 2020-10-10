package root.domain.report;

import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.TradingRecord;
import root.domain.strategy.StrategyFactory;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class StrategiesGroupAnalysisReportBuilder
{
    public StrategiesGroupAnalysisReport build(Map<StrategyFactory, TradingRecord> strategiesGroupExecutionResult, BaseBarSeries series)
    {
        var strategiesReports = getStrategiesReports(strategiesGroupExecutionResult, series);
        var totalProfit = calculateTotalProfit(strategiesReports);
        var nTrades = calculateNumberOfTrades(strategiesReports);
        var nProfitableTrades = calculateNumberOfProfitableTrades(strategiesReports);
        var nUnprofitableTrades = calculateNumberOfUnprofitableTrades(strategiesReports);
        var riskRewardRatio = nUnprofitableTrades / (double) nProfitableTrades;
        var tradesIntersectionsReport = new TradesIntersectionsReportBuilder().build(strategiesReports);
        var reportBeginTimestamp = getReportBeginTimestamp(series);
        var reportEndTimestamp = getReportEndTimestamp(series);
        return StrategiesGroupAnalysisReport.builder()
                .strategiesReports(strategiesReports)
                .totalProfit(totalProfit)
                .nTrades(nTrades)
                .nProfitableTrades(nProfitableTrades)
                .nUnprofitableTrades(nUnprofitableTrades)
                .riskRewardRatio(riskRewardRatio)
                .tradesIntersectionsReport(tradesIntersectionsReport)
                .beginTimestamp(reportBeginTimestamp)
                .endTimestamp(reportEndTimestamp)
                .build();
    }

    private List<StrategyAnalysisReport> getStrategiesReports(Map<StrategyFactory, TradingRecord> strategiesGroupExecutionResult, BaseBarSeries series)
    {
        var strategyAnalysisReportBuilder = new StrategyAnalysisReportBuilder();
        return strategiesGroupExecutionResult.entrySet().stream()
                .map(strategyExecutionResult -> {
                    var tradingRecord = strategyExecutionResult.getValue();
                    var trades = tradingRecord.getTrades();
                    var strategyFactory = strategyExecutionResult.getKey();
                    return strategyAnalysisReportBuilder.build(trades, series, strategyFactory);
                })
                .collect(toList());
    }

    private Double calculateTotalProfit(List<StrategyAnalysisReport> strategiesReports)
    {
        return strategiesReports.stream().mapToDouble(StrategyAnalysisReport::getTotalProfit).sum();
    }

    private Long calculateNumberOfTrades(List<StrategyAnalysisReport> strategiesReports)
    {
        return strategiesReports.stream()
                .map(StrategyAnalysisReport::getTrades)
                .mapToLong(List::size)
                .sum();
    }

    private Long calculateNumberOfProfitableTrades(List<StrategyAnalysisReport> strategiesReports)
    {
        return strategiesReports.stream().mapToLong(StrategyAnalysisReport::getNProfitableTrades).sum();
    }

    private Long calculateNumberOfUnprofitableTrades(List<StrategyAnalysisReport> strategiesReports)
    {
        return strategiesReports.stream().mapToLong(StrategyAnalysisReport::getNUnprofitableTrades).sum();
    }

    private Long getReportBeginTimestamp(BaseBarSeries series)
    {
        var firstBar = series.getFirstBar();
        return firstBar.getBeginTime().toInstant().toEpochMilli();
    }

    private Long getReportEndTimestamp(BaseBarSeries series)
    {
        var lastBar = series.getLastBar();
        return lastBar.getEndTime().toInstant().toEpochMilli();
    }
}
