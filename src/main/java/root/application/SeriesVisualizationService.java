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

import static root.domain.indicator.NumberIndicators.*;

@RequiredArgsConstructor
public class SeriesVisualizationService
{
    private final BarProvider barProvider;

    public List<Tick> getSeries()
    {
        var fromTimestamp = 1610196540000L;
        var toTimestamp = 1617964979000L;
        var symbol = "ETH_USD";
//        var interval = "ONE_MINUTE";
        var interval = "FIVE_MINUTES";
//        var interval = "FIFTEEN_MINUTES";
//        var interval = "THIRTY_MINUTES";
//        var interval = "ONE_HOUR";
        var series = new BaseBarSeries(barProvider.getBars(symbol, interval, fromTimestamp, toTimestamp));
        var closePrice = new ClosePriceIndicator(series);

        var ema50 = ema(closePrice, 50);
        var ema100 = ema(closePrice, 100);
        var ema200 = ema(closePrice, 200);
        var ema400 = ema(closePrice, 400);
        var trendLine = trendLine(series, true);
        var trendOscillator = trendOscillator(series, true);

        var seriesVisualization = new ArrayList<Tick>();
        for (int i = series.getBeginIndex(); i <= series.getEndIndex(); i++)
        {
            var mainChartNumIndicators = new LinkedHashMap<String, Double>();
//            mainChartNumIndicators.put(bbu.getName(), getIndicatorValue(bbu, i));
//            mainChartNumIndicators.put(trendLine.getName(), getIndicatorValue(trendLine, i));
            mainChartNumIndicators.put(ema50.getName(), getIndicatorValue(ema50, i));
            mainChartNumIndicators.put(ema100.getName(), getIndicatorValue(ema100, i));
            mainChartNumIndicators.put(ema200.getName(), getIndicatorValue(ema200, i));
            mainChartNumIndicators.put(ema400.getName(), getIndicatorValue(ema400, i));

            var additionalChartNumIndicators = new LinkedHashMap<String, Double>();
//            additionalChartNumIndicators.put(adxLevel30.getName(), getIndicatorValue(adxLevel30, i));
//            additionalChartNumIndicators.put(trendOscillator.getName(), getIndicatorValue(trendOscillator, i));

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
