package root.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import root.application.BarProvider;
import root.application.TradeVisualizationBuilder;
import root.application.service.SeriesVisualizationService;
import root.application.service.StrategyAnalysisService;
import root.infrastructure.BinanceExchangeBarProvider;
import root.infrastructure.CsvBarProvider;

@Configuration
public class ApplicationConfiguration
{
    private static final int N_TICKS_BEFORE_TRADE = 40;
    private static final int N_TICKS_AFTER_TRADE = 20;

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
    public StrategyAnalysisService strategyAnalysisService(
            BarProvider barProvider, TradeVisualizationBuilder tradeVisualizationBuilder)
    {
        return new StrategyAnalysisService(barProvider, tradeVisualizationBuilder);
    }

    @Bean
    public TradeVisualizationBuilder tradeVisualizationBuilder()
    {
        return new TradeVisualizationBuilder(N_TICKS_BEFORE_TRADE, N_TICKS_AFTER_TRADE);
    }
}
