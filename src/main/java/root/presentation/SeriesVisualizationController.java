package root.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import root.application.Tick;
import root.application.SeriesVisualizationService;

import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class SeriesVisualizationController
{
    private final SeriesVisualizationService seriesVisualizationService;

    @GetMapping("api/series")
    public List<Tick> getSeries()
    {
        return seriesVisualizationService.getSeries();
    }
}
