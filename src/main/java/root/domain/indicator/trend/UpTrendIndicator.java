package root.domain.indicator.trend;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.num.Num;

public class UpTrendIndicator extends CachedIndicator<Boolean>
{
    private final Indicator<Num> indicator;
    private final SMAIndicator smaIndicator;

    public UpTrendIndicator(Indicator<Num> indicator, int n)
    {
        super(indicator);
        this.indicator = indicator;
        this.smaIndicator = new SMAIndicator(indicator, n);
    }

    @Override
    protected Boolean calculate(int index)
    {
        Num currentValue = indicator.getValue(index);
        Num lastNAverageValue = smaIndicator.getValue(index);
        return currentValue.isGreaterThan(lastNAverageValue);
    }
}
