package root.domain.indicator;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.volume.OnBalanceVolumeIndicator;

public class OBVIndicator extends OnBalanceVolumeIndicator implements AdditionalChartNumIndicator
{
    private static final String OBV_INDICATOR_NAME = "OBV";

    public OBVIndicator(BarSeries series)
    {
        super(series);
    }

    public String getName()
    {
        return OBV_INDICATOR_NAME;
    }
}
