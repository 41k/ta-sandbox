package root.domain.rule;

import org.ta4j.core.indicators.helpers.PriceIndicator;
import org.ta4j.core.num.Num;
import root.domain.level.MainChartLevelProvider;

public class OverMainChartLevelRule extends AbstractMainChartLevelRule
{
    public OverMainChartLevelRule(PriceIndicator priceIndicator, MainChartLevelProvider levelProvider)
    {
        super(priceIndicator, levelProvider);
    }

    @Override
    protected boolean isSatisfied(int index, Num levelValue)
    {
        var currentPrice = priceIndicator.getValue(index);
        return currentPrice.isGreaterThan(levelValue);
    }
}
