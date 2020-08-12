package root.domain.indicator.bar;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.PrecisionNum;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Boolean.TRUE;

public abstract class AbstractBarIndicator extends CachedIndicator<Boolean>
{
    public AbstractBarIndicator(BarSeries series)
    {
        super(series);
    }

    protected boolean isNotAcceptableIndex(int index, int nPreviousBars)
    {
        try
        {
            getBar(index);
            return (index - nPreviousBars) < getBarSeries().getBeginIndex();
        }
        catch (IndexOutOfBoundsException e)
        {
            return TRUE;
        }
    }

    protected Bar getBar(int index)
    {
        return getBarSeries().getBar(index);
    }

    protected Stream<Bar> getNPreviousBars(int currentBarIndex, int nPreviousBars)
    {
        int startIndex = currentBarIndex - nPreviousBars;
        int endIndex = currentBarIndex;
        return IntStream.range(startIndex, endIndex).mapToObj(this::getBar);
    }

    protected Num calculateTotalSizeOfNPreviousBars(int currentBarIndex, int nPreviousBars)
    {
        return getNPreviousBars(currentBarIndex, nPreviousBars)
                .map(this::calculateBarSize)
                .map(Num::abs)
                .reduce(PrecisionNum.valueOf(0), Num::plus);
    }

    protected Num calculateBarSize(Bar bar)
    {
        return bar.getClosePrice().minus(bar.getOpenPrice());
    }
}
