package root.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import root.domain.report.StrategiesGroupAnalysisReport;
import root.application.SeriesVisualizationService;
import root.domain.report.StrategyAnalysisReport;
import root.application.StrategiesGroupAnalysisService;
import root.application.StrategyAnalysisService;
import root.domain.report.Tick;

import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("api")
public class ApplicationController
{
    private final StrategyAnalysisService strategyAnalysisService;
    private final StrategiesGroupAnalysisService strategiesGroupAnalysisService;
    private final SeriesVisualizationService seriesVisualizationService;

    @GetMapping("strategy-analysis-report")
    public StrategyAnalysisReport getStrategyAnalysisReport()
    {
        return strategyAnalysisService.analyse();
    }

    @GetMapping("strategies-group-analysis-report")
    public StrategiesGroupAnalysisReport getStrategiesGroupAnalysisReport()
    {
        return strategiesGroupAnalysisService.analyse();
    }

    @GetMapping("series")
    public List<Tick> getSeries()
    {
        return seriesVisualizationService.getSeries();
    }
}
