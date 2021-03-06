package root.domain.strategy;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Strategy;
import root.domain.indicator.NumberIndicator;
import root.domain.level.MainChartLevelProvider;

import java.util.List;
import java.util.Optional;

public abstract class AbstractStrategyFactory implements StrategyFactory
{
    protected final String strategyId;
    protected final BarSeries series;

    public AbstractStrategyFactory(String strategyId, BarSeries series)
    {
        checkInput(strategyId, series);
        this.strategyId = strategyId;
        this.series = series;
    }

    @Override
    public abstract Strategy create();

    @Override
    public String getStrategyId()
    {
        return strategyId;
    }

    @Override
    public List<NumberIndicator> getNumberIndicators()
    {
        return List.of();
    }

    @Override
    public List<MainChartLevelProvider> getMainChartLevelProviders()
    {
        return List.of();
    }

    @Override
    public Optional<Integer> getUnstablePeriodLength()
    {
        return Optional.empty();
    }

    private void checkInput(String strategyId, BarSeries series)
    {
        if (strategyId == null || strategyId.isBlank() || series == null)
        {
            throw new IllegalArgumentException();
        }
    }
}