package root.domain.indicator.bar;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.num.Num;

import static java.lang.Boolean.FALSE;

/**
 * Bullish bar comes after sequence of N Bullish bars
 * and has size which is greater than total size of the sequence.
 */
public class SequentialStrongBullishIndicator extends AbstractBarIndicator
{
    private final int nPreviousBars;

    public SequentialStrongBullishIndicator(BarSeries series, int nPreviousBarsToOvercome)
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
        return isSequentialStrongBullishBarAt(index);
    }

    private boolean isSequentialStrongBullishBarAt(int index)
    {
        Bar currentBar = getBar(index);
        boolean previousBarsAreBullish = getNPreviousBars(index, nPreviousBars).allMatch(Bar::isBullish);
        Num currentBarSize = calculateBarSize(currentBar);
        Num previousBarsTotalSize = calculateTotalSizeOfNPreviousBars(index, nPreviousBars);
        return currentBar.isBullish() &&
                previousBarsAreBullish &&
                currentBarSize.isGreaterThan(previousBarsTotalSize);
    }
}
