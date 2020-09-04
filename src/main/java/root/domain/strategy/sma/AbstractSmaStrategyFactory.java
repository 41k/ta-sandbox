package root.domain.strategy.sma;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import root.domain.indicator.Indicator;
import root.domain.indicator.SMAIndicator;
import root.domain.strategy.AbstractStrategyFactory;

import java.util.List;

public abstract class AbstractSmaStrategyFactory extends AbstractStrategyFactory
{
    protected final ClosePriceIndicator closePriceIndicator;
    protected final SMAIndicator shortSmaIndicator;
    protected final SMAIndicator mediumSmaIndicator;
    protected final SMAIndicator longSmaIndicator;
    protected final List<Indicator<Num>> numIndicators;

    AbstractSmaStrategyFactory(String strategyId, BarSeries series, int shortSmaLength, int mediumSmaLength, int longSmaLength)
    {
        super(strategyId, series);
        checkInput(shortSmaLength, mediumSmaLength, longSmaLength);
        this.closePriceIndicator = new ClosePriceIndicator(series);
        this.shortSmaIndicator = new SMAIndicator(closePriceIndicator, shortSmaLength);
        this.mediumSmaIndicator = new SMAIndicator(closePriceIndicator, mediumSmaLength);
        this.longSmaIndicator = new SMAIndicator(closePriceIndicator, longSmaLength);
        this.numIndicators = List.of(shortSmaIndicator, mediumSmaIndicator, longSmaIndicator);
    }

    @Override
    public abstract Strategy create();

    @Override
    public List<Indicator<Num>> getNumIndicators()
    {
        return numIndicators;
    }

    private void checkInput(int shortSmaLength, int mediumSmaLength, int longSmaLength)
    {
        if (shortSmaLength >= mediumSmaLength || mediumSmaLength >= longSmaLength)
        {
            throw new IllegalArgumentException();
        }
    }
}
