package root.domain.indicator.price_action;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;

public class BarTypeIndicator extends AbstractPriceActionIndicator
{
    private final BarType barType;

    public BarTypeIndicator(BarType barType, BarSeries series)
    {
        super(series);
        this.barType = barType;
    }

    @Override
    protected Boolean calculate(int index)
    {
        Bar currentBar = getBar(index);
        return barType.conforms(currentBar);
    }
}
