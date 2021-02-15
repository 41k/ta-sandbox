package root.application;

import lombok.RequiredArgsConstructor;
import org.ta4j.core.BaseBarSeries;
import root.domain.report.StrategiesGroupAnalysisReport;
import root.domain.report.StrategiesGroupAnalysisReportBuilder;
import root.domain.strategy.StrategyFactory;
import root.domain.strategy.ready.ETHUSD_5m_DownTrend_Strategy1Factory;
import root.domain.strategy.ready.ETHUSD_5m_UpTrend_Strategy1Factory;
import root.domain.strategy.ready.SMA3_DownTrend_Strategy1Factory;
import root.domain.strategy.ready.SMA3_UpTrend_Strategy1Factory;
import root.domain.strategy.rsi.RsiStrategy1Factory;
import root.domain.strategy.sma.SmaStrategy3CFactory;
import root.domain.strategy.sma.SmaStrategy5AFactory;
import root.domain.strategyexecutor.SequentialStrategiesGroupExecutor;

import java.util.List;

@RequiredArgsConstructor
public class StrategiesGroupAnalysisService
{
    private final BarProvider barProvider;

    public StrategiesGroupAnalysisReport analyse()
    {
        var bars = barProvider.getBars();
        var series = new BaseBarSeries(bars);
        var strategiesFactories = List.<StrategyFactory>of(
                new ETHUSD_5m_UpTrend_Strategy1Factory("UP-TREND", series),
                new ETHUSD_5m_DownTrend_Strategy1Factory("DOWN-TREND", series)
        );
        var strategiesGroupExecutionResult = new SequentialStrategiesGroupExecutor().execute(strategiesFactories, series);
        return new StrategiesGroupAnalysisReportBuilder().build(strategiesGroupExecutionResult, series);
    }
}
