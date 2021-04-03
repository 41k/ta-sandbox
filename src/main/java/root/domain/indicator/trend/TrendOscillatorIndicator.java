package root.domain.indicator.trend;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.ATRIndicator;
import org.ta4j.core.indicators.RecursiveCachedIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.Num;

/*
* Implementation is based on
* https://www.tradingview.com/script/29iYBNku-Follow-Line-Indicator/
*/
public class TrendOscillatorIndicator extends RecursiveCachedIndicator<Num>
{
    private final TrendLineIndicator trendLineIndicator;

    public TrendOscillatorIndicator(BarSeries series, boolean useAtrFilter)
    {
        super(series);
        this.trendLineIndicator = new TrendLineIndicator(series, useAtrFilter);
    }

    @Override
    protected Num calculate(int index)
    {
        var zero = numOf(0);
        if (index == 0)
        {
            return zero;
        }
        try
        {
            var previousTrendLineValue = trendLineIndicator.getValue(index - 1);
            var currentTrendLineValue = trendLineIndicator.getValue(index);
            var closePrice = trendLineIndicator.getClosePriceIndicator().getValue(index);
            var bbu = trendLineIndicator.getBbuIndicator().getValue(index);
            var bbl = trendLineIndicator.getBblIndicator().getValue(index);
            var previousTrendOscillatorValue = getValue(index - 1);
            if (closePrice.isGreaterThan(bbu))
            {
                return currentTrendLineValue.isLessThan(previousTrendLineValue) ?
                        previousTrendOscillatorValue :
                        numOf(1);
            }
            else if (closePrice.isLessThan(bbl))
            {
                return currentTrendLineValue.isGreaterThan(previousTrendLineValue) ?
                        previousTrendOscillatorValue :
                        numOf(-1);
            }
            else
            {
                return previousTrendOscillatorValue;
            }
        }
        catch (Exception e)
        {
            return zero;
        }
    }
}
