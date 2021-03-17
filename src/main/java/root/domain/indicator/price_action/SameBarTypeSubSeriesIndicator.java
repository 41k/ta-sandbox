package root.domain.indicator.price_action;

import org.ta4j.core.BarSeries;

import java.util.Set;

import static java.lang.Boolean.FALSE;

public class SameBarTypeSubSeriesIndicator extends AbstractPriceActionIndicator
{
    private final BarType barType;
    private final int subSeriesLength;
    private final int offset;

    public SameBarTypeSubSeriesIndicator(BarType barType, int subSeriesLength, BarSeries series)
    {
        this(barType, subSeriesLength, 0, series);
    }

    public SameBarTypeSubSeriesIndicator(BarType barType, int subSeriesLength, int offset, BarSeries series)
    {
        super(series);
        this.barType = barType;
        this.subSeriesLength = subSeriesLength;
        this.offset = offset;
    }

    @Override
    protected Boolean calculate(int index)
    {
        if (isNotAcceptableIndex(index, subSeriesLength))
        {
            return FALSE;
        }
        int subSeriesEndIndex = index - offset;
        int subSeriesStartIndex = subSeriesEndIndex - subSeriesLength + 1;
        return doesSubSeriesHasAllowedBarTypes(Set.of(barType), subSeriesStartIndex, subSeriesEndIndex);
    }
}
