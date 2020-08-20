package root.application.service;

import lombok.RequiredArgsConstructor;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import root.application.BarProvider;
import root.application.model.Tick;
import root.domain.indicator.sri.ResistanceIndicator;
import root.domain.indicator.sri.SRISettings;
import root.domain.indicator.sri.SupportIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@RequiredArgsConstructor
public class SeriesVisualizationService
{
    private static final String CLOSE_PRICE_INDICATOR = "price";
    private static final String SUPPORT_INDICATOR = "support";
    private static final String RESISTANCE_INDICATOR = "resistance";
    private static final String LONG_SMA_INDICATOR = "SMA(100)";
    private static final String MEDIUM_SMA_INDICATOR = "SMA(25)";
    private static final String SHORT_SMA_INDICATOR = "SMA(7)";
    private static final String LONG_EMA_INDICATOR = "longEma";
    private static final String SHORT_EMA_INDICATOR = "shortEma";

    private static final SRISettings SRI_SETTINGS = SRISettings.builder()
            .calculationWindowSize(100).segmentSize(25).zoneHeight(10).build();

    private final BarProvider barProvider;

    public List<Tick> getSeries()
    {
        var bars = barProvider.getMinuteBars();
        var series = new BaseBarSeries(bars);
        var closePriceIndicator = new ClosePriceIndicator(series);

        var supportIndicator = new SupportIndicator(series, SRI_SETTINGS);
        var resistanceIndicator = new ResistanceIndicator(series, SRI_SETTINGS);

        var longSmaIndicator = new SMAIndicator(closePriceIndicator, 100);
        var mediumSmaIndicator = new SMAIndicator(closePriceIndicator, 25);
        var shortSmaIndicator = new SMAIndicator(closePriceIndicator, 7);

        var longEmaIndicator = new EMAIndicator(closePriceIndicator, 200);
        var shortEmaIndicator = new EMAIndicator(closePriceIndicator, 50);

        var seriesVisualization = new ArrayList<Tick>();
        for (int i = series.getBeginIndex(); i <= series.getEndIndex(); i++)
        {
            var indicators = new LinkedHashMap<String, Double>();
//            indicators.put(CLOSE_PRICE_INDICATOR, getIndicatorValue(closePriceIndicator, i));

//            indicators.put(SUPPORT_INDICATOR, getIndicatorValue(supportIndicator, i));
//            indicators.put(RESISTANCE_INDICATOR, getIndicatorValue(resistanceIndicator, i));

            indicators.put(SHORT_SMA_INDICATOR, getIndicatorValue(shortSmaIndicator, i));
            indicators.put(MEDIUM_SMA_INDICATOR, getIndicatorValue(mediumSmaIndicator, i));
            indicators.put(LONG_SMA_INDICATOR, getIndicatorValue(longSmaIndicator, i));

//            indicators.put(LONG_EMA_INDICATOR, getIndicatorValue(longEmaIndicator, i));
//            indicators.put(SHORT_EMA_INDICATOR, getIndicatorValue(shortEmaIndicator, i));

            var bar = series.getBar(i);
            var tick = Tick.builder()
                    .indicators(indicators)
                    .open(bar.getOpenPrice().doubleValue())
                    .high(bar.getHighPrice().doubleValue())
                    .low(bar.getLowPrice().doubleValue())
                    .close(bar.getClosePrice().doubleValue())
                    .volume(bar.getVolume().doubleValue())
                    .timestamp(bar.getEndTime().toInstant().toEpochMilli())
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
