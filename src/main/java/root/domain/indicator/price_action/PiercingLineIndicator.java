package root.domain.indicator.price_action;

import org.ta4j.core.BarSeries;

import static java.lang.Boolean.FALSE;

public class PiercingLineIndicator extends AbstractPriceActionIndicator
{
    public PiercingLineIndicator(BarSeries series)
    {
        super(series);
    }

    @Override
    protected Boolean calculate(int index)
    {
        if (isNotAcceptableIndex(index, 1))
        {
            return FALSE;
        }
        var currentBar = getBar(index);
        var currentBarBodySize = currentBar.getClosePrice().minus(currentBar.getOpenPrice());
        var previousBar = getBar(index - 1);
        var previousBarBodySize = previousBar.getOpenPrice().minus(previousBar.getClosePrice());
        return currentBar.isBullish() && previousBar.isBearish() &&
                previousBar.getClosePrice().isEqual(currentBar.getOpenPrice()) &&
                previousBarBodySize.isGreaterThan(currentBarBodySize) &&
                currentBarBodySize.isGreaterThan(previousBarBodySize.dividedBy(numOf(2))) &&
                previousBar.getHighPrice().isGreaterThan(currentBar.getHighPrice()) &&
                previousBar.getLowPrice().isLessThan(currentBar.getLowPrice());
    }
}
