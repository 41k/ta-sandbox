package root.domain.indicator.sri;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.PrecisionNum;

import java.util.List;

import static java.util.Collections.max;
import static java.util.Collections.min;
import static org.ta4j.core.num.NaN.NaN;
import static root.domain.indicator.sri.SRZonesCalculator.calculateZones;
import static root.domain.indicator.sri.Zone.Type.SUPPORT;

public abstract class AbstractSRI extends CachedIndicator<Num>
{
    private final SRISettings sriSettings;

    AbstractSRI(BarSeries series, SRISettings sriSettings)
    {
        super(series);
        this.sriSettings = sriSettings;
    }

    @Override
    protected Num calculate(int index)
    {
        if (isNotAcceptable(index))
        {
            return NaN;
        }
        return findClosestNotBrokenLevel(index);
    }

    private boolean isNotAcceptable(int index)
    {
        try
        {
            getBarSeries().getBar(index);
            return index < sriSettings.getCalculationWindowSize();
        }
        catch (IndexOutOfBoundsException e)
        {
            return true;
        }
    }

    protected List<Zone> getSRZones(int index)
    {
        SRZoneSettings settings = SRZoneSettings.builder()
                .series(getSubSeries(index))
                .zoneHeight(PrecisionNum.valueOf(sriSettings.getZoneHeight()))
                .calculationWindowSize(sriSettings.getCalculationWindowSize())
                .segmentSize(sriSettings.getSegmentSize())
                .minStrength(sriSettings.getMinZoneStrength())
                .build();
        return calculateZones(settings);
    }

    private List<Bar> getSubSeries(int index)
    {
        int startIndex = index - sriSettings.getCalculationWindowSize();
        int endIndex = index + 1;
        return getBarSeries().getBarData().subList(startIndex, endIndex);
    }

    protected Num getClosePrice(int index)
    {
        return getBarSeries().getBar(index).getClosePrice();
    }

    protected Num calculateLevel(Zone zone)
    {
        List<Num> values = zone.getValues();
        return zone.getType() == SUPPORT ? min(values) : max(values);
    }

    protected abstract Num findClosestNotBrokenLevel(int index);
}
