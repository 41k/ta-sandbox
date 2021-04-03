package root.domain.indicator.trend;

import lombok.Getter;
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
* Implementation of
* https://www.tradingview.com/script/29iYBNku-Follow-Line-Indicator/
*/
public class TrendLineIndicator extends RecursiveCachedIndicator<Num>
{
    private final boolean useAtrFilter;
    private final HighPriceIndicator highPriceIndicator;
    private final LowPriceIndicator lowPriceIndicator;
    @Getter
    private final ClosePriceIndicator closePriceIndicator;
    private final BollingerBandsMiddleIndicator bbmIndicator;
    @Getter
    private final BollingerBandsUpperIndicator bbuIndicator;
    @Getter
    private final BollingerBandsLowerIndicator bblIndicator;
    private final ATRIndicator atrIndicator;

    public TrendLineIndicator(BarSeries series, boolean useAtrFilter)
    {
        super(series);
        this.useAtrFilter = useAtrFilter;
        this.highPriceIndicator = new HighPriceIndicator(series);
        this.lowPriceIndicator = new LowPriceIndicator(series);
        this.closePriceIndicator = new ClosePriceIndicator(series);

        var bbLength = 21;
        var k = series.numOf(2);
        var standardDeviation = new StandardDeviationIndicator(closePriceIndicator, bbLength);
        this.bbmIndicator = new BollingerBandsMiddleIndicator(new SMAIndicator(closePriceIndicator, bbLength));
        this.bbuIndicator = new BollingerBandsUpperIndicator(bbmIndicator, standardDeviation, k);
        this.bblIndicator = new BollingerBandsLowerIndicator(bbmIndicator, standardDeviation, k);

        var atrLength = 5;
        this.atrIndicator = new ATRIndicator(series, atrLength);
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
            var previousTrendLineValue = getValue(index - 1);
            var highPrice = highPriceIndicator.getValue(index);
            var lowPrice = lowPriceIndicator.getValue(index);
            var closePrice = closePriceIndicator.getValue(index);
            var bbu = bbuIndicator.getValue(index);
            var bbl = bblIndicator.getValue(index);
            var atr = atrIndicator.getValue(index);
            if (closePrice.isGreaterThan(bbu))
            {
                var currentTrendLineValue = lowPrice.minus(useAtrFilter ? atr : zero);
                return currentTrendLineValue.isLessThan(previousTrendLineValue) ?
                        previousTrendLineValue :
                        currentTrendLineValue;
            }
            else if (closePrice.isLessThan(bbl))
            {
                var currentTrendLineValue = highPrice.plus(useAtrFilter ? atr : zero);
                return currentTrendLineValue.isGreaterThan(previousTrendLineValue) ?
                        previousTrendLineValue :
                        currentTrendLineValue;
            }
            else
            {
                return previousTrendLineValue;
            }
        }
        catch (Exception e)
        {
            return zero;
        }
    }
}
