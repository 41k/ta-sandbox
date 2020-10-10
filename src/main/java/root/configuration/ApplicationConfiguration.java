package root.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import root.application.BarProvider;
import root.application.SeriesVisualizationService;
import root.application.StrategiesGroupAnalysisService;
import root.application.StrategyAnalysisService;
import root.infrastructure.CsvBarProvider;

@Configuration
public class ApplicationConfiguration
{
    @Bean
    public BarProvider barProvider()
    {
        //return new BinanceExchangeBarProvider();
        return new CsvBarProvider();
    }

    @Bean
    public SeriesVisualizationService tickVisualizationService(BarProvider barProvider)
    {
        return new SeriesVisualizationService(barProvider);
    }

    @Bean
    public StrategyAnalysisService strategyAnalysisService(BarProvider barProvider)
    {
        return new StrategyAnalysisService(barProvider);
    }

    @Bean
    public StrategiesGroupAnalysisService strategiesGroupAnalysisService(BarProvider barProvider)
    {
        return new StrategiesGroupAnalysisService(barProvider);
    }
}
