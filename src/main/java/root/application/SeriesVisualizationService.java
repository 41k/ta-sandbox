package root.application;

import lombok.RequiredArgsConstructor;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.Num;
import root.application.BarProvider;
import root.domain.indicator.EMAIndicator;
import root.domain.indicator.SARIndicator;
import root.domain.indicator.adx.ADXIndicator;
import root.domain.indicator.adx.ADXLevelIndicator;
import root.domain.indicator.bollinger.BBLowerIndicator;
import root.domain.indicator.bollinger.BBMiddleIndicator;
import root.domain.indicator.bollinger.BBUpperIndicator;
import root.domain.indicator.macd.MACDDifferenceIndicator;
import root.domain.indicator.wri.WRIndicator;
import root.domain.indicator.wri.WRLevelIndicator;
import root.domain.report.Tick;
import root.domain.indicator.macd.MACDIndicator;
import root.domain.indicator.macd.MACDLevelIndicator;
import root.domain.indicator.macd.MACDSignalLineIndicator;
import root.domain.indicator.rsi.RSIIndicator;
import root.domain.indicator.rsi.RSILevelIndicator;
import root.domain.indicator.SMAIndicator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@RequiredArgsConstructor
public class SeriesVisualizationService
{
    private final BarProvider barProvider;

    public List<Tick> getSeries()
    {
        var bars = barProvider.getBars();
        var series = new BaseBarSeries(bars);
        var closePriceIndicator = new ClosePriceIndicator(series);

        var longSmaIndicator = new SMAIndicator(closePriceIndicator, 100);
        var mediumSmaIndicator = new SMAIndicator(closePriceIndicator, 25);
        var shortSmaIndicator = new SMAIndicator(closePriceIndicator, 7);
        var sma = new SMAIndicator(closePriceIndicator, 1000);

        var ema7Indicator = new EMAIndicator(closePriceIndicator, 7);
        var ema25Indicator = new EMAIndicator(closePriceIndicator, 25);
        var ema100Indicator = new EMAIndicator(closePriceIndicator, 100);
        var ema20Indicator = new EMAIndicator(closePriceIndicator, 20);
        var ema50Indicator = new EMAIndicator(closePriceIndicator, 50);

        var rsiIndicator = new RSIIndicator(closePriceIndicator, 12);
        var rsiLevel70Indicator = new RSILevelIndicator(series, series.numOf(70));
        var rsiLevel50Indicator = new RSILevelIndicator(series, series.numOf(50));
        var rsiLevel30Indicator = new RSILevelIndicator(series, series.numOf(30));

        var macdIndicator = new MACDIndicator(closePriceIndicator, 12, 26);
        var macdSignalLineIndicator = new MACDSignalLineIndicator(macdIndicator, 9);
        var macdDifferenceIndicator = new MACDDifferenceIndicator(macdIndicator, macdSignalLineIndicator);
        var macdLevel0Indicator = new MACDLevelIndicator(series, series.numOf(0));

        var sarIndicator = new SARIndicator(series);

        var wrIndicator = new WRIndicator(series, 40);
        var wrLevelMinus10 = new WRLevelIndicator(series, series.numOf(-10));
        var wrLevelMinus90 = new WRLevelIndicator(series, series.numOf(-90));

        var adx = new ADXIndicator(series, 14, 14);
        var adxLevel30 = new ADXLevelIndicator(series, series.numOf(30));

        var periodLength = 20;
        var bbm = new BBMiddleIndicator(new SMAIndicator(closePriceIndicator, periodLength));
        var standardDeviation = new StandardDeviationIndicator(closePriceIndicator, periodLength);
        var bbu = new BBUpperIndicator(bbm, standardDeviation, series.numOf(2));
        var bbl = new BBLowerIndicator(bbm, standardDeviation, series.numOf(2));

        var seriesVisualization = new ArrayList<Tick>();
        for (int i = series.getBeginIndex(); i <= series.getEndIndex(); i++)
        {
            var mainChartNumIndicators = new LinkedHashMap<String, Double>();
            mainChartNumIndicators.put(bbu.getName(), getIndicatorValue(bbu, i));
            mainChartNumIndicators.put(bbm.getName(), getIndicatorValue(bbm, i));
            mainChartNumIndicators.put(bbl.getName(), getIndicatorValue(bbl, i));
//            mainChartNumIndicators.put(sma.getName(), getIndicatorValue(sma, i));
//            mainChartNumIndicators.put(shortSmaIndicator.getName(), getIndicatorValue(shortSmaIndicator, i));
//            mainChartNumIndicators.put(mediumSmaIndicator.getName(), getIndicatorValue(mediumSmaIndicator, i));
//            mainChartNumIndicators.put(longSmaIndicator.getName(), getIndicatorValue(longSmaIndicator, i));
//            mainChartNumIndicators.put(ema20Indicator.getName(), getIndicatorValue(ema20Indicator, i));
//            mainChartNumIndicators.put(ema50Indicator.getName(), getIndicatorValue(ema50Indicator, i));
//            mainChartNumIndicators.put(ema25Indicator.getName(), getIndicatorValue(ema25Indicator, i));
//            mainChartNumIndicators.put(sarIndicator.getName(), getIndicatorValue(sarIndicator, i));
//            mainChartNumIndicators.put(ema7Indicator.getName(), getIndicatorValue(ema7Indicator, i));
//            mainChartNumIndicators.put(ema25Indicator.getName(), getIndicatorValue(ema25Indicator, i));
//            mainChartNumIndicators.put(ema100Indicator.getName(), getIndicatorValue(ema100Indicator, i));

            var additionalChartNumIndicators = new LinkedHashMap<String, Double>();
//            additionalChartNumIndicators.put(rsiIndicator.getName(), getIndicatorValue(rsiIndicator, i));
//            additionalChartNumIndicators.put(rsiLevel70Indicator.getName(), getIndicatorValue(rsiLevel70Indicator, i));
//            additionalChartNumIndicators.put(rsiLevel50Indicator.getName(), getIndicatorValue(rsiLevel50Indicator, i));
//            additionalChartNumIndicators.put(rsiLevel30Indicator.getName(), getIndicatorValue(rsiLevel30Indicator, i));
//            additionalChartNumIndicators.put(macdIndicator.getName(), getIndicatorValue(macdIndicator, i));
//            additionalChartNumIndicators.put(macdSignalLineIndicator.getName(), getIndicatorValue(macdSignalLineIndicator, i));
//            additionalChartNumIndicators.put(macdDifferenceIndicator.getName(), getIndicatorValue(macdDifferenceIndicator, i));
//            additionalChartNumIndicators.put(macdLevel0Indicator.getName(), getIndicatorValue(macdLevel0Indicator, i));
//            additionalChartNumIndicators.put(wrIndicator.getName(), getIndicatorValue(wrIndicator, i));
//            additionalChartNumIndicators.put(wrLevelMinus10.getName(), getIndicatorValue(wrLevelMinus10, i));
//            additionalChartNumIndicators.put(wrLevelMinus90.getName(), getIndicatorValue(wrLevelMinus90, i));
//            additionalChartNumIndicators.put(adx.getName(), getIndicatorValue(adx, i));
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
