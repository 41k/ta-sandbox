package root.domain.indicator;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.CrossIndicator;
import root.domain.indicator.bar.AbstractBarIndicator;
import root.domain.indicator.bar.SequentialStrongBullishIndicator;
import root.domain.indicator.sri.ResistanceIndicator;

/**
 * Bullish bar after Resistance Level breakthrough by Sequential Strong Bullish bar.
 * Indicates potential Bullish trend.
 */
public class ResistanceBreakthroughIndicator extends AbstractBarIndicator
{
    private final SequentialStrongBullishIndicator sequentialStrongBullishIndicator;
    private final CrossIndicator crossIndicator;

    public ResistanceBreakthroughIndicator(BarSeries series, ResistanceIndicator resistanceIndicator)
    {
        super(series);
        this.sequentialStrongBullishIndicator = new SequentialStrongBullishIndicator(series, 2);
        this.crossIndicator = new CrossIndicator(resistanceIndicator, new ClosePriceIndicator(series));
    }

    @Override
    protected Boolean calculate(int index)
    {
        Bar currentBar = getBar(index);
        Boolean previousBarIsSequentialStrongBullish = sequentialStrongBullishIndicator.getValue(index - 1);
        Boolean previousBarCrossedUpResistanceLevel = crossIndicator.getValue(index - 1);
        return currentBar.isBullish() &&
                previousBarIsSequentialStrongBullish &&
                previousBarCrossedUpResistanceLevel;
    }
}
