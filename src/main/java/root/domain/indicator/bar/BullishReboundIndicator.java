package root.domain.indicator.bar;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;

/**
 * Bullish bar comes after Reversal Strong Bullish bar.
 * Indicates potential Bullish trend.
 */
public class BullishReboundIndicator extends AbstractBarIndicator
{
    private final ReversalStrongBullishIndicator reversalStrongBullishIndicator;

    public BullishReboundIndicator(BarSeries series)
    {
        super(series);
        this.reversalStrongBullishIndicator = new ReversalStrongBullishIndicator(series, 2);
    }

    @Override
    protected Boolean calculate(int index)
    {
        Bar currentBar = getBar(index);
        boolean previousBarIsReversalStrongBullish = reversalStrongBullishIndicator.getValue(index - 1);
        return currentBar.isBullish() && previousBarIsReversalStrongBullish;
    }
}
