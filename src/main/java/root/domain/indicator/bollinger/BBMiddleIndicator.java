package root.domain.indicator.bollinger;

import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;
import root.domain.indicator.MainChartNumIndicator;

public class BBMiddleIndicator extends org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator implements MainChartNumIndicator
{
    private static final String INDICATOR_NAME = "BBM";

    public BBMiddleIndicator(Indicator<Num> indicator)
    {
        super(indicator);
    }

    @Override
    public String getName()
    {
        return INDICATOR_NAME;
    }
}
