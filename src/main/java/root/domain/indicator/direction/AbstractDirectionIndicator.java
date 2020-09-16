package root.domain.indicator.direction;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.Math.max;

public abstract class AbstractDirectionIndicator extends CachedIndicator<Boolean>
{
    private final Indicator<Num> indicator;
    private final int n;

    public AbstractDirectionIndicator(Indicator<Num> indicator, int n)
    {
        super(indicator);
        this.indicator = indicator;
        this.n = n;
    }

    @Override
    protected Boolean calculate(int index)
    {
        if (n < 2)
        {
            return FALSE;
        }
        Num previousValue = indicator.getValue(index);
        for (int i = index - 1; i >= max(0, index - n); i--)
        {
            Num currentValue = indicator.getValue(i);
            if (directionIsSatisfied(previousValue, currentValue))
            {
                previousValue = currentValue;
                continue;
            }
            return FALSE;
        }
        return TRUE;
    }

    protected abstract boolean directionIsSatisfied(Num previousValue, Num currentValue);
}
