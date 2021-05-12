package root.domain.rule;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.TradingRecord;
import root.domain.indicator.price_action.BarType;

public class WaitForNBarsOfTypeRule implements Rule
{
    private final int barCount;
    private final BarType barType;
    private final BarSeries series;

    public WaitForNBarsOfTypeRule(int barCount, BarType barType, BarSeries series)
    {
        this.barCount = barCount;
        this.barType = barType;
        this.series = series;
    }

    @Override
    public boolean isSatisfied(int currentIndex, TradingRecord tradingRecord)
    {
        if (tradingRecord == null)
        {
            return false;
        }
        var currentTrade = tradingRecord.getCurrentTrade();
        if (!currentTrade.isOpened())
        {
            return  false;
        }
        var entryIndex = currentTrade.getEntry().getIndex();
        if (currentIndex - entryIndex < barCount)
        {
            return  false;
        }
        return series.getSubSeries(entryIndex, currentIndex - 1).getBarData()
                .stream().filter(barType::conforms).count() >= barCount;
    }
}
