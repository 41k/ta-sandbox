package root.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import root.domain.indicator.sri.ResistanceIndicator;
import root.domain.indicator.sri.SRISettings;
import root.domain.indicator.sri.SupportIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class SeriesVisualizationService
{
    private static final String CLOSE_PRICE_INDICATOR = "price";
    private static final String SUPPORT_INDICATOR = "support";
    private static final String RESISTANCE_INDICATOR = "resistance";

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

        var seriesVisualization = new ArrayList<Tick>();
        for (int i = series.getBeginIndex(); i <= series.getEndIndex(); i++)
        {
            var indicators = new HashMap<String, Double>();
            indicators.put(CLOSE_PRICE_INDICATOR, getIndicatorValue(closePriceIndicator, i));
            indicators.put(SUPPORT_INDICATOR, getIndicatorValue(supportIndicator, i));
            indicators.put(RESISTANCE_INDICATOR, getIndicatorValue(resistanceIndicator, i));
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
