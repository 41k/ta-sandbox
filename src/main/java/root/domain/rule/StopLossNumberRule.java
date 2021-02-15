package root.domain.rule;

import org.ta4j.core.Order;
import org.ta4j.core.Rule;
import org.ta4j.core.Trade;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

public class StopLossNumberRule  implements Rule
{
    private final ClosePriceIndicator closePrice;
    private final Num lossNumber;

    public StopLossNumberRule(ClosePriceIndicator closePrice, Number lossNumber)
    {
        this(closePrice, closePrice.numOf(lossNumber));
    }

    public StopLossNumberRule(ClosePriceIndicator closePrice, Num lossNumber)
    {
        this.closePrice = closePrice;
        this.lossNumber = lossNumber;
    }

    @Override
    public boolean isSatisfied(int index, TradingRecord tradingRecord)
    {
        if (tradingRecord == null)
        {
            return false;
        }
        Trade currentTrade = tradingRecord.getCurrentTrade();
        if (!currentTrade.isOpened())
        {
            return  false;
        }
        Order entryOrder = currentTrade.getEntry();
        Num entryPrice = entryOrder.getNetPrice();
        Num currentPrice = closePrice.getValue(index);
        if (entryOrder.isBuy())
        {
            return isBuyStopSatisfied(entryPrice, currentPrice);
        }
        else
        {
            return isSellStopSatisfied(entryPrice, currentPrice);
        }
    }

    private boolean isSellStopSatisfied(Num entryPrice, Num currentPrice)
    {
        Num threshold = entryPrice.plus(lossNumber);
        return currentPrice.isGreaterThanOrEqual(threshold);
    }

    private boolean isBuyStopSatisfied(Num entryPrice, Num currentPrice)
    {
        Num threshold = entryPrice.minus(lossNumber);
        return currentPrice.isLessThanOrEqual(threshold);
    }
}
