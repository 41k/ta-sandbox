package root.domain.indicator.bar;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.num.Num;

import static java.lang.Boolean.FALSE;

/**
 * Bullish bar comes after sequence of N Bearish bars
 * and has size which is greater than total size of the sequence.
 */
public class ReversalStrongBullishIndicator extends AbstractBarIndicator
{
    private final int nPreviousBars;

    public ReversalStrongBullishIndicator(BarSeries series, int nPreviousBarsToOvercome)
    {
        super(series);
        this.nPreviousBars = nPreviousBarsToOvercome;
    }

    @Override
    protected Boolean calculate(int index)
    {
        if (isNotAcceptableIndex(index, nPreviousBars))
        {
            return FALSE;
        }
        return isReversalStrongBullishBarAt(index);
    }

    private boolean isReversalStrongBullishBarAt(int index)
    {
        Bar currentBar = getBar(index);
        boolean previousBarsAreBearish = getNPreviousBars(index, nPreviousBars).allMatch(Bar::isBearish);
        Num currentBarSize = calculateBarSize(currentBar);
        Num previousBarsTotalSize = calculateTotalSizeOfNPreviousBars(index, nPreviousBars);
        return currentBar.isBullish() &&
                previousBarsAreBearish &&
                currentBarSize.isGreaterThan(previousBarsTotalSize);
    }
}
