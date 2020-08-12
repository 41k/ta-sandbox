package root.domain.indicator.sri;

import org.ta4j.core.BarSeries;
import org.ta4j.core.num.Num;

import static org.ta4j.core.num.NaN.NaN;

public class ResistanceIndicator extends AbstractSRI
{
    public ResistanceIndicator(BarSeries series, SRISettings settings)
    {
        super(series, settings);
    }

    @Override
    protected Num findClosestNotBrokenLevel(int index)
    {
        Num closePrice = getClosePrice(index);
        return getSRZones(index).stream()
                .map(super::calculateLevel)
                .filter(level -> level.isGreaterThan(closePrice))
                .min(Num::compareTo)
                .orElse(NaN);
    }
}
