package root.application.service;

import lombok.RequiredArgsConstructor;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import root.application.BarProvider;
import root.application.model.Tick;
import root.domain.indicator.RSIIndicator;
import root.domain.indicator.RSILevelIndicator;
import root.domain.indicator.SMAIndicator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@RequiredArgsConstructor
public class SeriesVisualizationService
{
    private static final String SHORT_SMA_INDICATOR = "SMA(7)";
    private static final String MEDIUM_SMA_INDICATOR = "SMA(25)";
    private static final String LONG_SMA_INDICATOR = "SMA(100)";

    private static final String RSI_INDICATOR = "RSI(12)";
    private static final String RSI_LEVEL_70_INDICATOR = "RSI-level(70)";
    private static final String RSI_LEVEL_50_INDICATOR = "RSI-level(50)";
    private static final String RSI_LEVEL_30_INDICATOR = "RSI-level(30)";

    private final BarProvider barProvider;

    public List<Tick> getSeries()
    {
        var bars = barProvider.getMinuteBars();
        var series = new BaseBarSeries(bars);
        var closePriceIndicator = new ClosePriceIndicator(series);

        var longSmaIndicator = new SMAIndicator(closePriceIndicator, 100);
        var mediumSmaIndicator = new SMAIndicator(closePriceIndicator, 25);
        var shortSmaIndicator = new SMAIndicator(closePriceIndicator, 7);

        var rsiIndicator = new RSIIndicator(closePriceIndicator, 12);
        var rsiLevel70Indicator = new RSILevelIndicator(series, series.numOf(70));
        var rsiLevel50Indicator = new RSILevelIndicator(series, series.numOf(50));
        var rsiLevel30Indicator = new RSILevelIndicator(series, series.numOf(30));

        var seriesVisualization = new ArrayList<Tick>();
        for (int i = series.getBeginIndex(); i <= series.getEndIndex(); i++)
        {
            var mainChartNumIndicators = new LinkedHashMap<String, Double>();
            mainChartNumIndicators.put(SHORT_SMA_INDICATOR, getIndicatorValue(shortSmaIndicator, i));
            mainChartNumIndicators.put(MEDIUM_SMA_INDICATOR, getIndicatorValue(mediumSmaIndicator, i));
            mainChartNumIndicators.put(LONG_SMA_INDICATOR, getIndicatorValue(longSmaIndicator, i));

            var additionalChartNumIndicators = new LinkedHashMap<String, Double>();
            additionalChartNumIndicators.put(RSI_INDICATOR, getIndicatorValue(rsiIndicator, i));
            additionalChartNumIndicators.put(RSI_LEVEL_70_INDICATOR, getIndicatorValue(rsiLevel70Indicator, i));
            additionalChartNumIndicators.put(RSI_LEVEL_50_INDICATOR, getIndicatorValue(rsiLevel50Indicator, i));
            additionalChartNumIndicators.put(RSI_LEVEL_30_INDICATOR, getIndicatorValue(rsiLevel30Indicator, i));

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
