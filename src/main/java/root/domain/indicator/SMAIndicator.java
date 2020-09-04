package root.domain.indicator;

import org.ta4j.core.num.Num;

import static java.lang.String.format;

public class SMAIndicator extends org.ta4j.core.indicators.SMAIndicator implements Indicator<Num>
{
    private static final String SMA_INDICATOR_NAME_FORMAT = "SMA(%d)";

    private final String name;

    public SMAIndicator(org.ta4j.core.Indicator<Num> indicator, int length)
    {
        super(indicator, length);
        this.name = format(SMA_INDICATOR_NAME_FORMAT, length);
    }

    public String getName()
    {
        return name;
    }
}
