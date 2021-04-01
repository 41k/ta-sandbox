package root.domain.strategy;

import org.ta4j.core.Strategy;
import org.ta4j.core.num.Num;
import root.domain.indicator.Indicator;
import root.domain.level.MainChartLevelProvider;

import java.util.List;
import java.util.Optional;

public interface StrategyFactory
{
    Strategy create();

    String getStrategyId();

    List<Indicator<Num>> getNumIndicators();

    List<MainChartLevelProvider> getMainChartLevelProviders();

    Optional<Integer> getUnstablePeriodLength();
}