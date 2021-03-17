package root.domain.indicator.helpers;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;

public class PreviousBooleanIndicator extends CachedIndicator<Boolean>
{
    private final int n;
    private Indicator<Boolean> indicator;

    public PreviousBooleanIndicator(Indicator<Boolean> indicator)
    {
        this(indicator, 1);
    }

    public PreviousBooleanIndicator(Indicator<Boolean> indicator, int n)
    {
        super(indicator);
        this.n = n;
        this.indicator = indicator;
    }

    protected Boolean calculate(int index)
    {
        int previousIndex = Math.max(0, (index - n));
        return this.indicator.getValue(previousIndex);
    }
}
