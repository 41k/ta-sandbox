package root.domain.strategyexecutor;

import org.ta4j.core.BarSeries;
import org.ta4j.core.TradingRecord;
import root.domain.strategy.StrategyFactory;

import java.util.List;
import java.util.Map;

public interface StrategiesGroupExecutor
{
    Map<StrategyFactory, TradingRecord> execute(List<StrategyFactory> strategiesFactories, BarSeries series);
}
