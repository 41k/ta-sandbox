package root.domain.strategy;

import org.ta4j.core.Strategy;
import org.ta4j.core.num.Num;
import root.domain.indicator.Indicator;

import java.util.List;

public interface StrategyFactory
{
    Strategy create();

    String getStrategyId();

    default List<Indicator<Num>> getNumIndicators()
    {
        return List.of();
    }
}
