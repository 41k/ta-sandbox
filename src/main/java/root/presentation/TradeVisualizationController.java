package root.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import root.application.TradeVisualization;
import root.application.TradeVisualizationService;

import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class TradeVisualizationController
{
    private final TradeVisualizationService tradeVisualizationService;

    @GetMapping("api/trades")
    public List<TradeVisualization> getTrades()
    {
        return tradeVisualizationService.getTrades();
    }
}
