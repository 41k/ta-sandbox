package root.domain.indicator.bar;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.num.Num;

public class BullishPinBarIndicator extends AbstractBarIndicator
{
    private final Num bodyMinSize;
    private final Num bodyFactor;
    private final Num noseFactor;

    public BullishPinBarIndicator(BarSeries series, double bodyMinSize, double bodyFactor, double noseFactor)
    {
        super(series);
        this.bodyMinSize = series.numOf(bodyMinSize);
        this.bodyFactor = series.numOf(bodyFactor);
        this.noseFactor = series.numOf(noseFactor);
    }

    @Override
    protected Boolean calculate(int index)
    {
        Bar bar = getBar(index);
        if (bar.isBullish())
        {
            return calculateForBullishBar(bar);
        }
        else
        {
            return calculateForBearishBar(bar);
        }
    }

    private Boolean calculateForBullishBar(Bar bar)
    {
        Num wickSize = bar.getOpenPrice().minus(bar.getLowPrice());
        Num bodySize = bar.getClosePrice().minus(bar.getOpenPrice());
        Num noseSize = bar.getHighPrice().minus(bar.getClosePrice());
        return isPinBar(wickSize, bodySize, noseSize);
    }

    private Boolean calculateForBearishBar(Bar bar)
    {
        Num wickSize = bar.getClosePrice().minus(bar.getLowPrice());
        Num bodySize = bar.getOpenPrice().minus(bar.getClosePrice());
        Num noseSize = bar.getHighPrice().minus(bar.getOpenPrice());
        return isPinBar(wickSize, bodySize, noseSize);
    }

    private Boolean isPinBar(Num wickSize, Num bodySize, Num noseSize)
    {
        return bodySize.isGreaterThanOrEqual(bodyMinSize) &&
                wickSize.isGreaterThanOrEqual(bodySize.multipliedBy(bodyFactor)) &&
                wickSize.isGreaterThanOrEqual(noseSize.multipliedBy(noseFactor));
    }
}
