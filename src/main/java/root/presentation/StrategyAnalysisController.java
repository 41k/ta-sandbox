package root.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import root.application.model.StrategyAnalysisReport;
import root.application.service.StrategyAnalysisService;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class StrategyAnalysisController
{
    private final StrategyAnalysisService strategyAnalysisService;

    @GetMapping("api/strategy/analysis-report")
    public StrategyAnalysisReport getStrategyAnalysisReport()
    {
        return strategyAnalysisService.analyse();
    }
}
