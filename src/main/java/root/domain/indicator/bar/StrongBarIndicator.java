package root.domain.indicator.bar;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.num.Num;

import java.util.Set;

import static java.lang.Boolean.FALSE;

public class StrongBarIndicator extends AbstractBarIndicator
{
    private final BarType barType;
    private final Set<BarType> typesOfPreviousBars;
    private final int nPreviousBars;

    public StrongBarIndicator(BarType barType, Set<BarType> typesOfPreviousBars, int nPreviousBarsToOvercome, BarSeries series)
    {
        super(series);
        this.barType = barType;
        this.typesOfPreviousBars = typesOfPreviousBars;
        this.nPreviousBars = nPreviousBarsToOvercome;
    }

    @Override
    protected Boolean calculate(int index)
    {
        if (isNotAcceptableIndex(index, nPreviousBars))
        {
            return FALSE;
        }
        Bar currentBar = getBar(index);
        boolean currentBarHasAllowedType = barType.conforms(currentBar);
        boolean previousBarsHaveAllowedTypes = doesPreviousBarsHaveAllowedTypes(typesOfPreviousBars, index, nPreviousBars);
        Num currentBarSize = calculateBarSize(currentBar);
        Num previousBarsTotalSize = calculateTotalSizeOfNPreviousBars(index, nPreviousBars);
        return currentBarHasAllowedType &&
                previousBarsHaveAllowedTypes &&
                currentBarSize.isGreaterThan(previousBarsTotalSize);
    }
}
