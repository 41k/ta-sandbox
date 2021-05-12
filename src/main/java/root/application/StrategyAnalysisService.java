package root.application;

import lombok.RequiredArgsConstructor;
import org.ta4j.core.BarSeriesManager;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.cost.LinearTransactionCostModel;
import org.ta4j.core.cost.ZeroCostModel;
import root.domain.report.StrategyAnalysisReport;
import root.domain.report.StrategyAnalysisReportBuilder;
import root.domain.strategy.mess.*;

import static org.ta4j.core.Order.OrderType.BUY;

@RequiredArgsConstructor
public class StrategyAnalysisService
{
    private final BarProvider barProvider;

    public StrategyAnalysisReport analyse()
    {
        var fromTimestamp = 1610196540000L;
        var toTimestamp = 1628588839000L;
//        var symbol = "LTC_USD";
//        var symbol = "XRP_USD";
//        var symbol = "BTC_USD";
            var symbol = "ETH_USD";
//        var interval = "ONE_MINUTE";
//        var interval = "FIVE_MINUTES";
//        var interval = "FIFTEEN_MINUTES";
//        var interval = "THIRTY_MINUTES";
//         var interval = "ONE_HOUR";
//        var interval = "FOUR_HOURS";
        var interval = "ONE_DAY";
        var series = new BaseBarSeries(barProvider.getBars(symbol, interval, fromTimestamp, toTimestamp));
//        var strategyFactory = new MacdStrategy1Factory("MACD", series);
//        var strategyFactory = new SAR_WR_Strategy1Factory("SAR-WR", series);
//        var strategyFactory = new ETHUSD_5m_UpTrend_Strategy1Factory("NEW", series);
//        var strategyFactory = new ETHUSD_5m_BB_Strategy2Factory("BB2", series);
//        var strategyFactory = new ETHUSD_5m_DownTrend_Strategy1Factory("DT", series);
//        var strategyFactory = new ETHUSD_15m_EMA_PA_Strategy1Factory("EMA-PA", series);
//        var strategyFactory = new ETHUSD_5m_Pullback_Strategy1Factory("Pullback", series);
//        var strategyFactory = new ETHUSD_5m_OnlyTP_UpTrend_Strategy1Factory("Only TP in UpTrend", series);
//        var strategyFactory = new ETHUSD_5m_OnlyTP_DownTrend_Strategy1Factory("Only TP in DownTrend", series);
//        var strategyFactory = new SL_TP_StrategyFactory("SL&TP", series);
//        var strategyFactory = new MasterCandleStrategyFactory("Master candle", series);
        var strategyFactory = new NewStrategy4Factory("New", series);
        var strategy = strategyFactory.create();
//        var seriesManager = new BarSeriesManager(series);
//        var tradingRecord = seriesManager.run(strategy);
        var seriesManager = new BarSeriesManager(series, new LinearTransactionCostModel(0.002), new ZeroCostModel());
//        var tradingRecord = seriesManager.run(strategy, BUY, series.numOf(0.08d));
        var tradingRecord = seriesManager.run(strategy, BUY, series.numOf(1d));
        var trades = tradingRecord.getTrades();
        var strategyReportBuilder = new StrategyAnalysisReportBuilder();
        return strategyReportBuilder.build(trades, series, strategyFactory);
    }
}
