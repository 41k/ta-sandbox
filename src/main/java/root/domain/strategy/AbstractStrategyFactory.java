package root.domain.strategy;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Strategy;

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

    private void checkInput(String strategyId, BarSeries series)
    {
        if (strategyId == null || strategyId.isBlank() || series == null)
        {
            throw new IllegalArgumentException();
        }
    }
}
