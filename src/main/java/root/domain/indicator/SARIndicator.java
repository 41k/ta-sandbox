package root.domain.indicator;

import org.ta4j.core.BarSeries;

public class SARIndicator extends org.ta4j.core.indicators.ParabolicSarIndicator implements MainChartNumIndicator
{
    private static final String NAME = "SAR";

    public SARIndicator(BarSeries series)
    {
        super(series);
    }

    @Override
    public String getName()
    {
        return NAME;
    }
}
