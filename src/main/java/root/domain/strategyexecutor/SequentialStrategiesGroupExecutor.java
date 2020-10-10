package root.domain.strategyexecutor;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BarSeriesManager;
import org.ta4j.core.TradingRecord;
import root.domain.strategy.StrategyFactory;

import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class SequentialStrategiesGroupExecutor implements StrategiesGroupExecutor
{
    @Override
    public Map<StrategyFactory, TradingRecord> execute(List<StrategyFactory> strategiesFactories, BarSeries series)
    {
        var seriesManager = new BarSeriesManager(series);
        return strategiesFactories.stream().collect(toMap(
                identity(),
                strategyFactory -> seriesManager.run(strategyFactory.create())
        ));
    }
}
