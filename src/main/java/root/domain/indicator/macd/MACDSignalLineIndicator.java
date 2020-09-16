package root.domain.indicator.macd;

import org.ta4j.core.num.Num;
import root.domain.indicator.Indicator;

import static java.lang.String.format;

public class MACDSignalLineIndicator extends org.ta4j.core.indicators.EMAIndicator implements Indicator<Num>
{
    private static final String MACD_SIGNAL_LINE_INDICATOR_NAME_FORMAT = "MACD-signal(%d)";

    private final String name;

    public MACDSignalLineIndicator(MACDIndicator macdIndicator, int length)
    {
        super(macdIndicator, length);
        this.name = format(MACD_SIGNAL_LINE_INDICATOR_NAME_FORMAT, length);
    }

    public String getName()
    {
        return name;
    }
}
