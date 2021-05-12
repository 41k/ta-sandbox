package root.application;

import lombok.RequiredArgsConstructor;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import root.domain.report.Tick;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.ta4j.core.indicators.pivotpoints.FibonacciReversalIndicator.FibReversalTyp.SUPPORT;
import static org.ta4j.core.indicators.pivotpoints.TimeLevel.DAY;
import static root.domain.indicator.NumberIndicators.*;

@RequiredArgsConstructor
public class SeriesVisualizationService
{
    private final BarProvider barProvider;

    public List<Tick> getSeries()
    {
        var fromTimestamp = 1610196540000L;
        var toTimestamp = 1626028073000L;
        var symbol = "ETH_USD";
//        var interval = "ONE_MINUTE";
//        var interval = "FIVE_MINUTES";
//        var interval = "FIFTEEN_MINUTES";
//        var interval = "THIRTY_MINUTES";
        var interval = "ONE_HOUR";
//        var interval = "FOUR_HOURS";
//        var interval = "ONE_DAY";
        var series = new BaseBarSeries(barProvider.getBars(symbol, interval, fromTimestamp, toTimestamp));
        var closePrice = new ClosePriceIndicator(series);

        var ema9 = ema(closePrice, 9);
        var ema50 = ema(closePrice, 50);
        var ema100 = ema(closePrice, 100);
        var ema200 = ema(closePrice, 200);

        var trendLine = trendLine(series, true);
        var trendOscillator = trendOscillator(series, true);

        var fib_0 = fibinacci(0, SUPPORT, DAY, series);
        var fib_0_382 = fibinacci(0.382, SUPPORT, DAY, series);
        var fib_0_5 = fibinacci(0.5, SUPPORT, DAY, series);
        var fib_0_618 = fibinacci(0.618, SUPPORT, DAY, series);
        var fib_1 = fibinacci(1, SUPPORT, DAY, series);

        var rsi = rsi(closePrice, 7);
        var rsiLevel70 = rsiLevel(70, series);
        var rsiLevel60 = rsiLevel(60, series);
        var rsiLevel20 = rsiLevel(20, series);

        var periodLength = 20;
        var bbm = bollingerBandsMiddle(closePrice, periodLength);
        var bbu = bollingerBandsUpper(bbm, closePrice, periodLength);
        var bbl = bollingerBandsLower(bbm, closePrice, periodLength);

        var parabolicSAR = parabolicSAR(series, 0.002, 0.05);

        var atr = atr(7, series);

        var seriesVisualization = new ArrayList<Tick>();
        for (int i = series.getBeginIndex(); i <= series.getEndIndex(); i++)
        {
            var mainChartNumIndicators = new LinkedHashMap<String, Double>();
//            mainChartNumIndicators.put(parabolicSAR.getName(), getIndicatorValue(parabolicSAR, i));
//            mainChartNumIndicators.put(bbu.getName(), getIndicatorValue(bbu, i));
//            mainChartNumIndicators.put(bbm.getName(), getIndicatorValue(bbm, i));
//            mainChartNumIndicators.put(bbl.getName(), getIndicatorValue(bbl, i));
//            mainChartNumIndicators.put(trendLine.getName(), getIndicatorValue(trendLine, i));
//            mainChartNumIndicators.put(ema50.getName(), getIndicatorValue(ema50, i));
//            mainChartNumIndicators.put(ema100.getName(), getIndicatorValue(ema100, i));
//            mainChartNumIndicators.put(ema200.getName(), getIndicatorValue(ema200, i));
//            mainChartNumIndicators.put(ema9.getName(), getIndicatorValue(ema9, i));
//            mainChartNumIndicators.put(fib_0.getName(), getIndicatorValue(fib_0, i));
//            mainChartNumIndicators.put(fib_0_382.getName(), getIndicatorValue(fib_0_382, i));
//            mainChartNumIndicators.put(fib_0_5.getName(), getIndicatorValue(fib_0_5, i));
//            mainChartNumIndicators.put(fib_0_618.getName(), getIndicatorValue(fib_0_618, i));
//            mainChartNumIndicators.put(fib_1.getName(), getIndicatorValue(fib_1, i));

            var additionalChartNumIndicators = new LinkedHashMap<String, Double>();
//            additionalChartNumIndicators.put(atr.getName(), getIndicatorValue(atr, i));
//            additionalChartNumIndicators.put(adxLevel30.getName(), getIndicatorValue(adxLevel30, i));
//            additionalChartNumIndicators.put(trendOscillator.getName(), getIndicatorValue(trendOscillator, i));
            additionalChartNumIndicators.put(rsi.getName(), getIndicatorValue(rsi, i));
            additionalChartNumIndicators.put(rsiLevel70.getName(), getIndicatorValue(rsiLevel70, i));
            additionalChartNumIndicators.put(rsiLevel20.getName(), getIndicatorValue(rsiLevel20, i));

            var bar = series.getBar(i);
            var tick = Tick.builder()
                    .open(bar.getOpenPrice().doubleValue())
                    .high(bar.getHighPrice().doubleValue())
                    .low(bar.getLowPrice().doubleValue())
                    .close(bar.getClosePrice().doubleValue())
                    .volume(bar.getVolume().doubleValue())
                    .timestamp(bar.getEndTime().toInstant().toEpochMilli())
                    .mainChartNumIndicators(mainChartNumIndicators)
                    .additionalChartNumIndicators(additionalChartNumIndicators)
                    .build();
            seriesVisualization.add(tick);
        }
        return seriesVisualization;
    }

    private Double getIndicatorValue(Indicator<Num> indicator, int index)
    {
        var indicatorValue = indicator.getValue(index);
        return indicatorValue.isNaN() ? null : indicatorValue.doubleValue();
    }
}
