package root.domain.indicator.direction;

import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

public class DownDirectionIndicator extends AbstractDirectionIndicator
{
    public DownDirectionIndicator(Indicator<Num> indicator, int n)
    {
        super(indicator, n);
    }

    protected boolean directionIsSatisfied(Num previousValue, Num currentValue)
    {
        return previousValue.isLessThan(currentValue);
    }
}
