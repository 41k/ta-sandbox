package root.domain.rule;

import org.ta4j.core.Rule;
import org.ta4j.core.TradingRecord;

public class SequenceBooleanRule implements Rule
{
    private final Rule rule;
    private final int barCount;

    public SequenceBooleanRule(Rule rule, int barCount)
    {
        this.rule = rule;
        this.barCount = barCount;
    }

    @Override
    public boolean isSatisfied(int index, TradingRecord tradingRecord)
    {
        if (index < barCount - 1)
        {
            return false;
        }
        var currentIndex = index;
        for (int c = barCount; c > 0; c--)
        {
            if (rule.isSatisfied(currentIndex, tradingRecord))
            {
                currentIndex--;
            }
            else
            {
                return false;
            }
        }
        return true;
    }
}
