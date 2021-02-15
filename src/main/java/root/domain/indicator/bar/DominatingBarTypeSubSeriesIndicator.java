package root.domain.indicator.bar;

import org.ta4j.core.BarSeries;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class DominatingBarTypeSubSeriesIndicator extends AbstractBarIndicator
{
    private final BarType dominatingBarType;
    private final int subSeriesLength;
    private final int nBarsOfAnotherType;

    public DominatingBarTypeSubSeriesIndicator(BarType dominatingBarType, int subSeriesLength, int nBarsOfAnotherType, BarSeries series)
    {
        super(series);
        this.dominatingBarType = dominatingBarType;
        this.subSeriesLength = subSeriesLength;
        this.nBarsOfAnotherType = nBarsOfAnotherType;
    }

    @Override
    protected Boolean calculate(int index)
    {
        var nPreviousBars = subSeriesLength - 1;
        if (isNotAcceptableIndex(index, nPreviousBars))
        {
            return FALSE;
        }
        var anotherBarTypeCounter = nBarsOfAnotherType;
        for (int i = 0; i < subSeriesLength; i++)
        {
            var bar = getBar(index - i);
            if (!dominatingBarType.conforms(bar))
            {
                anotherBarTypeCounter = anotherBarTypeCounter - 1;
                if (anotherBarTypeCounter < 0)
                {
                    return FALSE;
                }
            }
        }
        return TRUE;
    }
}
