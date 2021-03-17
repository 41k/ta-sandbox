package root.application;

import lombok.RequiredArgsConstructor;
import org.ta4j.core.BarSeriesManager;
import org.ta4j.core.BaseBarSeries;
import root.domain.report.StrategyAnalysisReport;
import root.domain.report.StrategyAnalysisReportBuilder;
import root.domain.strategy.level.SL_TP_StrategyFactory;
import root.domain.strategy.macd.MacdStrategy1Factory;
import root.domain.strategy.ready.ETHUSD_15m_EMA_PA_Strategy1Factory;
import root.domain.strategy.ready.ETHUSD_5m_BB_Strategy2Factory;
import root.domain.strategy.ready.ETHUSD_5m_UpTrend_Strategy1Factory;
import root.domain.strategy.ready.SAR_WR_Strategy1Factory;

@RequiredArgsConstructor
public class StrategyAnalysisService
{
    private final BarProvider barProvider;

    public StrategyAnalysisReport analyse()
    {
        var fromTimestamp = 1610196540000L;
        var toTimestamp = 1616581994000L;
        var symbol = "ETH_USD";
//        var interval = "ONE_MINUTE";
        var interval = "FIVE_MINUTES";
//        var interval = "FIFTEEN_MINUTES";
//        var interval = "THIRTY_MINUTES";
//        var interval = "ONE_HOUR";
        var series = new BaseBarSeries(barProvider.getBars(symbol, interval, fromTimestamp, toTimestamp));
//        var strategyFactory = new MacdStrategy1Factory("MACD", series);
//        var strategyFactory = new SAR_WR_Strategy1Factory("SAR-WR", series);
        var strategyFactory = new ETHUSD_5m_UpTrend_Strategy1Factory("NEW", series);
//        var strategyFactory = new ETHUSD_5m_BB_Strategy2Factory("BB2", series);
//        var strategyFactory = new ETHUSD_5m_DownTrend_Strategy1Factory("DT", series);
//        var strategyFactory = new ETHUSD_15m_EMA_PA_Strategy1Factory("EMA-PA", series);
//        var strategyFactory = new SL_TP_StrategyFactory("SL&TP", series);
//        var strategyFactory = new MasterCandleStrategyFactory("Master candle", series);
        var strategy = strategyFactory.create();
        var seriesManager = new BarSeriesManager(series);
        var tradingRecord = seriesManager.run(strategy);
//        var seriesManager = new BarSeriesManager(series, new LinearTransactionCostModel(0.002), new ZeroCostModel());
//        var tradingRecord = seriesManager.run(strategy, BUY, series.numOf(0.08d));
        var trades = tradingRecord.getTrades();
        var strategyReportBuilder = new StrategyAnalysisReportBuilder();
        return strategyReportBuilder.build(trades, series, strategyFactory);
    }
}
