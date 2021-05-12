package root.domain.indicator.pivot_points;

import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.helpers.DifferenceIndicator;
import org.ta4j.core.indicators.helpers.HighestValueIndicator;
import org.ta4j.core.indicators.helpers.LowestValueIndicator;
import org.ta4j.core.indicators.helpers.PriceIndicator;
import org.ta4j.core.num.Num;
import root.domain.indicator.TrendType;

import java.util.Map;
import java.util.function.Function;

import static org.ta4j.core.num.NaN.NaN;
import static root.domain.indicator.TrendType.BEARISH;
import static root.domain.indicator.TrendType.BULLISH;

// Warning: Do not use this indicator with CrossedUpIndicatorRule/CrossedDownIndicatorRule
public class FibonacciRetracementLevelIndicator extends CachedIndicator<Num>
{
    private final Num level;
    private final TrendType trendType;
    private final PriceIndicator priceIndicator;
    private final int windowSize;

    private final HighestValueIndicator highestPriceIndicator;
    private final LowestValueIndicator lowestPriceIndicator;
    private final DifferenceIndicator priceDifferenceIndicator;

    private final Map<TrendType, Function<Integer, Num>> trendTypeToLevelCalculatorMap = Map.of(
            BULLISH, this::calculateForBullishTrend,
            BEARISH, this::calculateForBearishTrend
    );

    private final Map<TrendType, Function<Integer, Boolean>> trendTypeToVerifierMap = Map.of(
            BULLISH, this::isBullishTrend,
            BEARISH, this::isBearishTrend
    );

    public FibonacciRetracementLevelIndicator(double level, TrendType trendType, PriceIndicator priceIndicator, int windowSize)
    {
        super(priceIndicator);

        this.level = numOf(level);
        this.trendType = trendType;
        this.priceIndicator = priceIndicator;
        this.windowSize = windowSize;

        this.highestPriceIndicator = new HighestValueIndicator(priceIndicator, windowSize);
        this.lowestPriceIndicator = new LowestValueIndicator(priceIndicator, windowSize);
        this.priceDifferenceIndicator = new DifferenceIndicator(highestPriceIndicator, lowestPriceIndicator);
    }

    @Override
    protected Num calculate(int index)
    {
        if (trendIsNotSatisfied(index))
        {
            return NaN;
        }
        var levelValueCalculator = trendTypeToLevelCalculatorMap.get(trendType);
        return levelValueCalculator.apply(index);
    }

    private boolean trendIsNotSatisfied(int index)
    {
        var trendVerifier = trendTypeToVerifierMap.get(trendType);
        return !trendVerifier.apply(index);
    }

    private Boolean isBullishTrend(Integer index)
    {
        var highestPriceIndex = getHighestPriceIndex(index);
        var lowestPriceIndex = getLowestPriceIndex(index);
        return lowestPriceIndex < highestPriceIndex;
    }

    private Boolean isBearishTrend(Integer index)
    {
        var highestPriceIndex = getHighestPriceIndex(index);
        var lowestPriceIndex = getLowestPriceIndex(index);
        return highestPriceIndex < lowestPriceIndex;
    }

    private int getHighestPriceIndex(int index)
    {
        var highestPrice = highestPriceIndicator.getValue(index);
        return getBarIndexByPrice(highestPrice, index);
    }

    private int getLowestPriceIndex(int index)
    {
        var lowestPrice = lowestPriceIndicator.getValue(index);
        return getBarIndexByPrice(lowestPrice, index);
    }

    private Num calculateForBullishTrend(Integer index)
    {
        var highestPrice = highestPriceIndicator.getValue(index);
        var priceDifference = priceDifferenceIndicator.getValue(index);
        return highestPrice.minus(priceDifference.multipliedBy(level));
    }

    private Num calculateForBearishTrend(Integer index)
    {
        var lowestPrice = lowestPriceIndicator.getValue(index);
        var priceDifference = priceDifferenceIndicator.getValue(index);
        return lowestPrice.plus(priceDifference.multipliedBy(level));
    }

    private int getBarIndexByPrice(Num price, int index)
    {
        for (var i = 0; i <= windowSize; i++)
        {
            var currentIndex = index - i;
            var currentPrice = priceIndicator.getValue(currentIndex);
            if (price.isEqual(currentPrice))
            {
                return currentIndex;
            }
        }
        throw new IllegalStateException();
    }
}
