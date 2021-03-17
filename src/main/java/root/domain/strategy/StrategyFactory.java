package root.domain.strategy;

import org.ta4j.core.Strategy;
import root.domain.indicator.NumberIndicator;
import root.domain.level.MainChartLevelProvider;

import java.util.List;
import java.util.Optional;

public interface StrategyFactory
{
    Strategy create();

    String getStrategyId();

    List<NumberIndicator> getNumberIndicators();

    List<MainChartLevelProvider> getMainChartLevelProviders();

    Optional<Integer> getUnstablePeriodLength();
}
