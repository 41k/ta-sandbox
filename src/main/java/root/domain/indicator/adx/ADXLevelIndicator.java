package root.domain.indicator.adx;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.helpers.ConstantIndicator;
import org.ta4j.core.num.Num;
import root.domain.indicator.AdditionalChartNumIndicator;

import static java.lang.String.format;

public class ADXLevelIndicator extends ConstantIndicator<Num> implements AdditionalChartNumIndicator
{
    private static final String RSI_LEVEL_INDICATOR_NAME_FORMAT = "ADX-level(%.0f)";

    private final String name;

    public ADXLevelIndicator(BarSeries series, Num level)
    {
        super(series, level);
        this.name = format(RSI_LEVEL_INDICATOR_NAME_FORMAT, level.doubleValue());
    }

    public String getName()
    {
        return name;
    }
}
