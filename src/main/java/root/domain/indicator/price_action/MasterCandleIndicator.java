package root.domain.indicator.price_action;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.candles.RealBodyIndicator;
import org.ta4j.core.num.Num;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class MasterCandleIndicator extends AbstractPriceActionIndicator
{
    private static final int MASTER_CANDLE_PATTERN_MIN_BAR_COUNT = 5;
    private static final int MASTER_CANDLE_PATTERN_MAX_BAR_COUNT = 7;

    private final int patternBarCount;
    private final Num bodyMinSize;

    public MasterCandleIndicator(int patternBarCount, double bodyMinSize, BarSeries series)
    {
        super(series);
        validate(patternBarCount);
        this.patternBarCount = patternBarCount;
        this.bodyMinSize = numOf(bodyMinSize);
    }

    @Override
    protected Boolean calculate(int index)
    {
        var nPreviousBars = patternBarCount - 1;
        if (isNotAcceptableIndex(index, nPreviousBars))
        {
            return FALSE;
        }
        var masterCandleIndex = index - patternBarCount + 1;
        var masterCandle = getBar(masterCandleIndex);
        var masterCandleBodySize = new RealBodyIndicator(getBarSeries()).getValue(masterCandleIndex).abs();
        if (masterCandleBodySize.isLessThan(bodyMinSize))
        {
            return FALSE;
        }
        var nBarsInsideMasterCandlePriceRange = patternBarCount - 1;
        for (var i = 1; i <= nBarsInsideMasterCandlePriceRange; i++)
        {
            var bar = getBar(masterCandleIndex + i);
            if (bar.getHighPrice().isLessThanOrEqual(masterCandle.getHighPrice()) &&
                bar.getLowPrice().isGreaterThanOrEqual(masterCandle.getLowPrice()))
            {
                continue;
            }
            return FALSE;
        }
        return TRUE;
    }

    private void validate(int patternBarCount)
    {
        if (patternBarCount < MASTER_CANDLE_PATTERN_MIN_BAR_COUNT ||
            patternBarCount > MASTER_CANDLE_PATTERN_MAX_BAR_COUNT)
        {
            throw new IllegalArgumentException();
        }
    }
}
