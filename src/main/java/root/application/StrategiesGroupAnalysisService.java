package root.application;

import lombok.RequiredArgsConstructor;
import org.ta4j.core.BaseBarSeries;
import root.domain.analysis.StrategiesGroupAnalysisReport;
import root.domain.analysis.StrategiesGroupAnalysisReportBuilder;
import root.domain.strategy.StrategyFactory;
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
        var bars = barProvider.getMinuteBars();
        var series = new BaseBarSeries(bars);
        var strategiesFactories = List.<StrategyFactory>of(
                new RsiStrategy1Factory("RSI", series),
                new SmaStrategy3CFactory("SMA-3C", series, 7, 25, 100),
                new SmaStrategy5AFactory("SMA-5A", series, 7, 25, 100)
        );
        var strategiesGroupExecutionResult = new SequentialStrategiesGroupExecutor().execute(strategiesFactories, series);
        return new StrategiesGroupAnalysisReportBuilder().build(strategiesGroupExecutionResult, series);
    }
}
