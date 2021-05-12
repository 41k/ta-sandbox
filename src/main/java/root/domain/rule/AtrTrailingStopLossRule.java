package root.domain.rule;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Rule;
import org.ta4j.core.Trade;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.indicators.ATRIndicator;
import org.ta4j.core.indicators.helpers.HighestValueIndicator;
import org.ta4j.core.indicators.helpers.LowestValueIndicator;
import org.ta4j.core.indicators.helpers.PriceIndicator;
import org.ta4j.core.num.Num;

public class AtrTrailingStopLossRule implements Rule
{
    private final PriceIndicator priceIndicator;
    private final ATRIndicator atrIndicator;
    private Num atrMultiplier;

    public AtrTrailingStopLossRule(PriceIndicator priceIndicator, int atrLength, double atrMultiplier, BarSeries series)
    {
        this.priceIndicator = priceIndicator;
        this.atrIndicator = new ATRIndicator(series, atrLength);
        this.atrMultiplier = series.numOf(atrMultiplier);
    }

    @Override
    public boolean isSatisfied(int currentIndex, TradingRecord tradingRecord)
    {
        boolean satisfied = false;
        if (tradingRecord != null)
        {
            Trade currentTrade = tradingRecord.getCurrentTrade();
            if (currentTrade.isOpened())
            {
                Num currentPrice = priceIndicator.getValue(currentIndex);
                int entryIndex = currentTrade.getEntry().getIndex();
                if (currentTrade.getEntry().isBuy())
                {
                    satisfied = isBuySatisfied(currentPrice, currentIndex, entryIndex);
                }
                else
                {
                    satisfied = isSellSatisfied(currentPrice, currentIndex, entryIndex);
                }
            }
        }
        return satisfied;
    }

    private boolean isBuySatisfied(Num currentPrice, int currentIndex, int entryIndex)
    {
        int inTradeBarCount = getInTradeBarCount(currentIndex, entryIndex);
        HighestValueIndicator highestPriceIndicator = new HighestValueIndicator(priceIndicator, inTradeBarCount);
        Num highestPrice = highestPriceIndicator.getValue(currentIndex);
        Num atr = getAtrValue(currentIndex, inTradeBarCount);
        Num lossThreshold = highestPrice.minus(atr);
        return currentPrice.isLessThanOrEqual(lossThreshold);
    }

    private boolean isSellSatisfied(Num currentPrice, int currentIndex, int entryIndex)
    {
        int inTradeBarCount = getInTradeBarCount(currentIndex, entryIndex);
        LowestValueIndicator lowestPriceIndicator = new LowestValueIndicator(priceIndicator, inTradeBarCount);
        Num lowestPrice = lowestPriceIndicator.getValue(currentIndex);
        Num atr = getAtrValue(currentIndex, inTradeBarCount);
        Num lossThreshold = lowestPrice.plus(atr);
        return currentPrice.isGreaterThanOrEqual(lossThreshold);
    }

    private int getInTradeBarCount(int currentIndex, int entryIndex)
    {
        return currentIndex - entryIndex + 1;
    }

    private Num getAtrValue(int currentIndex, int inTradeBarCount)
    {
        HighestValueIndicator highestAtrIndicator = new HighestValueIndicator(atrIndicator, inTradeBarCount);
        Num highestAtr = highestAtrIndicator.getValue(currentIndex);
        return highestAtr.multipliedBy(atrMultiplier);
    }
}
