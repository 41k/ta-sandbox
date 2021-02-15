package root.domain.indicator.bar;

import org.ta4j.core.BarSeries;

import java.util.Set;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static root.domain.indicator.bar.BarType.BEARISH;
import static root.domain.indicator.bar.BarType.BULLISH;

public class StrongBearishSubSeriesIndicator extends AbstractBarIndicator
{
    private final int barCount;
    private final int barStrength;
    private final SameBarTypeSubSeriesIndicator bearishSubSeriesIndicator;

    public StrongBearishSubSeriesIndicator(int barCount, int barStrength, BarSeries series)
    {
        super(series);
        this.barCount = barCount;
        this.barStrength = barStrength;
        this.bearishSubSeriesIndicator = new SameBarTypeSubSeriesIndicator(BEARISH, barCount, series);
    }

    @Override
    protected Boolean calculate(int index)
    {
        if (!bearishSubSeriesIndicator.calculate(index))
        {
            return FALSE;
        }
        var totalSizeOfNBarsBeforeStrongBearishSubSeries = calculateTotalSizeOfNPreviousBars(index - (barCount - 1), barStrength);
        for (int i = 0; i < barCount; i++)
        {
            var bar = getBar(index - i);
            var barBodySize = bar.getOpenPrice().minus(bar.getClosePrice());
            if (barBodySize.isLessThan(totalSizeOfNBarsBeforeStrongBearishSubSeries))
            {
                return FALSE;
            }
        }
        return TRUE;
    }
}
