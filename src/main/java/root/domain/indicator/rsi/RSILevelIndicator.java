package root.domain.indicator.rsi;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.helpers.ConstantIndicator;
import org.ta4j.core.num.Num;
import root.domain.indicator.Indicator;

import static java.lang.String.format;

public class RSILevelIndicator extends ConstantIndicator<Num> implements Indicator<Num>
{
    private static final String RSI_LEVEL_INDICATOR_NAME_FORMAT = "RSI-level(%.0f)";

    private final String name;

    public RSILevelIndicator(BarSeries series, Num level)
    {
        super(series, level);
        this.name = format(RSI_LEVEL_INDICATOR_NAME_FORMAT, level.doubleValue());
    }

    public String getName()
    {
        return name;
    }
}
