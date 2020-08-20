package root.domain.indicator.bar;

import org.ta4j.core.BarSeries;

import java.util.Set;

import static java.lang.Boolean.FALSE;

public class SameBarTypeSubSeriesIndicator extends AbstractBarIndicator
{
    private final BarType barType;
    private final int nBars;

    public SameBarTypeSubSeriesIndicator(BarType barType, int nBars, BarSeries series)
    {
        super(series);
        this.barType = barType;
        this.nBars = nBars;
    }

    @Override
    protected Boolean calculate(int index)
    {
        if (isNotAcceptableIndex(index, nBars))
        {
            return FALSE;
        }
        int subSeriesStartIndex = index - nBars + 1;
        int subSeriesEndIndex = index;
        return doesSubSeriesHasAllowedBarTypes(Set.of(barType), subSeriesStartIndex, subSeriesEndIndex);
    }
}
