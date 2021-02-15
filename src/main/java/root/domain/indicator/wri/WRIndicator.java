package root.domain.indicator.wri;

import org.ta4j.core.BarSeries;
import org.ta4j.core.num.Num;
import root.domain.indicator.AdditionalChartNumIndicator;

import static java.lang.String.format;

public class WRIndicator extends org.ta4j.core.indicators.WilliamsRIndicator implements AdditionalChartNumIndicator
{
    private static final String NAME_FORMAT = "WR(%d)";

    private final String name;

    public WRIndicator(BarSeries barSeries, int barCount)
    {
        super(barSeries, barCount);
        this.name = format(NAME_FORMAT, barCount);
    }

    public String getName()
    {
        return name;
    }
}
