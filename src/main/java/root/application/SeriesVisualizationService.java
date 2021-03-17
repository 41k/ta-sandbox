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

@RequiredArgsConstructor
public class SeriesVisualizationService
{
    private final BarProvider barProvider;

    public List<Tick> getSeries()
    {
        var series = new BaseBarSeries(barProvider.getBars("ETH_USD", "FIVE_MINUTES", 1610196540000L, 1616581994000L));
        var closePriceIndicator = new ClosePriceIndicator(series);

        var seriesVisualization = new ArrayList<Tick>();
        for (int i = series.getBeginIndex(); i <= series.getEndIndex(); i++)
        {
            var mainChartNumIndicators = new LinkedHashMap<String, Double>();
//            mainChartNumIndicators.put(bbu.getName(), getIndicatorValue(bbu, i));

            var additionalChartNumIndicators = new LinkedHashMap<String, Double>();
//            additionalChartNumIndicators.put(adxLevel30.getName(), getIndicatorValue(adxLevel30, i));

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
