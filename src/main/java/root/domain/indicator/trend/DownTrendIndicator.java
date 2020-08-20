package root.domain.indicator.trend;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class DownTrendIndicator extends CachedIndicator<Boolean>
{
    private final Indicator<Num> indicator;
    private final int n;

    public DownTrendIndicator(Indicator<Num> indicator, int n)
    {
        super(indicator);
        this.indicator = indicator;
        this.n = n;
    }

    @Override
    protected Boolean calculate(int index)
    {
        Num value = indicator.getValue(index);
        for (int i = index - 1; i >= Math.max(0, index - n); i--)
        {
            Num currentValue = indicator.getValue(i);
            if (value.isGreaterThan(currentValue))
            {
                return FALSE;
            }
            value = currentValue;
        }
        return TRUE;
    }
}
