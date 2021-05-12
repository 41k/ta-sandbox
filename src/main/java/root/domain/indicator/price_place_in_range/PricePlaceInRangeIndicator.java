package root.domain.indicator.price_place_in_range;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.AbstractIndicator;
import org.ta4j.core.indicators.helpers.*;
import org.ta4j.core.num.Num;

import static org.ta4j.core.num.NaN.NaN;

public class PricePlaceInRangeIndicator extends AbstractIndicator<Num>
{
    private final int rangeLength;
    private final HighestValueIndicator highestPriceIndicator;
    private final LowestValueIndicator lowestPriceIndicator;
    private final ClosePriceIndicator closePriceIndicator;

    public PricePlaceInRangeIndicator(int rangeLength, BarSeries series)
    {
        super(series);
        this.rangeLength = rangeLength;
        this.highestPriceIndicator = new HighestValueIndicator(new HighPriceIndicator(series), rangeLength);
        this.lowestPriceIndicator = new LowestValueIndicator(new LowPriceIndicator(series), rangeLength);
        this.closePriceIndicator = new ClosePriceIndicator(series);
    }

    @Override
    public Num getValue(int index)
    {
        if (getBarSeries().getBarCount() < rangeLength)
        {
            return NaN;
        }
        var closePrice = closePriceIndicator.getValue(index);
        var highestPrice = highestPriceIndicator.getValue(index);
        var lowestPrice = lowestPriceIndicator.getValue(index);
        return closePrice.minus(lowestPrice)
                .dividedBy(highestPrice.minus(lowestPrice))
                .multipliedBy(numOf(100));
    }
}
