package root.application;

import lombok.RequiredArgsConstructor;
import org.ta4j.core.BarSeriesManager;
import org.ta4j.core.BaseBarSeries;
import root.domain.analysis.StrategyAnalysisReportBuilder;
import root.domain.analysis.StrategyAnalysisReport;
import root.domain.strategy.macd.MacdStrategy1Factory;

@RequiredArgsConstructor
public class StrategyAnalysisService
{
    private final BarProvider barProvider;

    public StrategyAnalysisReport analyse()
    {
        var bars = barProvider.getMinuteBars();
        var series = new BaseBarSeries(bars);
        var strategyFactory = new MacdStrategy1Factory("MACD", series);
        //var strategyFactory = new SmaStrategy5AFactory("SMA", series, 7, 25, 100);
        //var strategyFactory = new RsiStrategy1Factory("RSI", series);
        var strategy = strategyFactory.create();
        var seriesManager = new BarSeriesManager(series);
        var tradingRecord = seriesManager.run(strategy);
        var trades = tradingRecord.getTrades();
        var strategyReportBuilder = new StrategyAnalysisReportBuilder();
        return strategyReportBuilder.build(trades, series, strategyFactory);
    }
}
