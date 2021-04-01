package root.application;

import lombok.RequiredArgsConstructor;
import org.ta4j.core.BarSeriesManager;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.cost.LinearTransactionCostModel;
import org.ta4j.core.cost.ZeroCostModel;
import root.domain.report.StrategyAnalysisReportBuilder;
import root.domain.report.StrategyAnalysisReport;
import root.domain.strategy.level.SL_TP_StrategyFactory;
import root.domain.strategy.mess.NewStrategyFactory;
import root.domain.strategy.ready.*;
import root.domain.strategy.rsi.RsiStrategy1Factory;
import root.domain.strategy.sma.SmaStrategy3BFactory;

import static org.ta4j.core.Order.OrderType.BUY;

@RequiredArgsConstructor
public class StrategyAnalysisService
{
    private final BarProvider barProvider;

    public StrategyAnalysisReport analyse()
    {
        var bars = barProvider.getBars();
        var series = new BaseBarSeries(bars);
        //var strategyFactory = new SmaStrategy3BFactory("SMA", series, 7, 25, 100);
        //var strategyFactory = new RsiStrategy1Factory("RSI", series);
        //var strategyFactory = new SAR_WR_Strategy1Factory("SAR-WR", series);
        //var strategyFactory = new ETHUSD_5m_UpTrend_Strategy1Factory("NEW", series);
        //var strategyFactory = new ETHUSD_5m_UpTrend_Strategy1Factory("NEW", series);
        //var strategyFactory = new NewStrategyFactory("NEW", series);
        var strategyFactory = new SL_TP_StrategyFactory("SL-TP", series);
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
