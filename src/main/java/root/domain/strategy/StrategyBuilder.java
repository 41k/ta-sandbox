package root.domain.strategy;

import org.ta4j.core.Indicator;
import org.ta4j.core.Strategy;
import org.ta4j.core.num.Num;

import java.util.Map;
import java.util.Optional;

public interface StrategyBuilder
{
    Strategy build();
    Optional<Map<String, Indicator<Num>>> getNumIndicators();
}
