package root.domain.indicator.direction;

import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

public class UpDirectionIndicator extends AbstractDirectionIndicator
{
    public UpDirectionIndicator(Indicator<Num> indicator, int n)
    {
        super(indicator, n);
    }

    protected boolean directionIsSatisfied(Num previousValue, Num currentValue)
    {
        return previousValue.isGreaterThan(currentValue);
    }
}
