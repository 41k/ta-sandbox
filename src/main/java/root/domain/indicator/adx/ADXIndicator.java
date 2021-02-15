package root.domain.indicator.adx;

import org.ta4j.core.BarSeries;
import root.domain.indicator.AdditionalChartNumIndicator;

import static java.lang.String.format;

public class ADXIndicator extends org.ta4j.core.indicators.adx.ADXIndicator implements AdditionalChartNumIndicator
{
    private static final String ADX_INDICATOR_NAME_FORMAT = "ADX(%d, %d)";

    private final String name;

    public ADXIndicator(BarSeries series, int diBarCount, int adxBarCount)
    {
        super(series, diBarCount, adxBarCount);
        this.name = format(ADX_INDICATOR_NAME_FORMAT, diBarCount, adxBarCount);
    }

    public String getName()
    {
        return name;
    }
}
