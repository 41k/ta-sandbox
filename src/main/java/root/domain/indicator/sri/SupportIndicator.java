package root.domain.indicator.sri;

import org.ta4j.core.BarSeries;
import org.ta4j.core.num.Num;

import static org.ta4j.core.num.NaN.NaN;

public class SupportIndicator extends AbstractSRI
{
    public SupportIndicator(BarSeries series, SRISettings settings)
    {
        super(series, settings);
    }

    @Override
    protected Num findClosestNotBrokenLevel(int index)
    {
        Num closePrice = getClosePrice(index);
        return getSRZones(index).stream()
                .map(super::calculateLevel)
                .filter(level -> level.isLessThan(closePrice))
                .max(Num::compareTo)
                .orElse(NaN);
    }
}
