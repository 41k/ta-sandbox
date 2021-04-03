package root.application;

import lombok.RequiredArgsConstructor;
import org.ta4j.core.BaseBarSeries;
import root.domain.report.StrategiesGroupAnalysisReport;
import root.domain.report.StrategiesGroupAnalysisReportBuilder;
import root.domain.strategy.StrategyFactory;
import root.domain.strategy.mess.NewStrategy3Factory;
import root.domain.strategy.ready.ETHUSD_5m_BB_Strategy2Factory;
import root.domain.strategyexecutor.SequentialStrategiesGroupExecutor;

import java.util.List;

@RequiredArgsConstructor
public class StrategiesGroupAnalysisService
{
    private final BarProvider barProvider;

    public StrategiesGroupAnalysisReport analyse()
    {
        var series = new BaseBarSeries(barProvider.getBars("ETH_USD", "FIVE_MINUTES", 1610196540000L, 1617964979000L));
        var strategiesFactories = List.<StrategyFactory>of(
                new ETHUSD_5m_BB_Strategy2Factory("BB PA", series),
                new NewStrategy3Factory("New", series)
        );
        var strategiesGroupExecutionResult = new SequentialStrategiesGroupExecutor().execute(strategiesFactories, series);
        return new StrategiesGroupAnalysisReportBuilder().build(strategiesGroupExecutionResult, series);
    }
}
