package root.domain.strategy;

import org.ta4j.core.*;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.trading.rules.BooleanIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;
import root.domain.indicator.bar.StrongBarIndicator;
import root.domain.indicator.trend.DownTrendIndicator;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static root.domain.indicator.bar.BarType.BEARISH;
import static root.domain.indicator.bar.BarType.BULLISH;

/*
Given:
* SMA(7) - shortSma,
* SMA(25) - mediumSma,
* SMA(100) - longSma

Case #1
Buy rule:
    (shortSma < mediumSma) &&
    (shortSma and mediumSma in downTrend(7)) &&
    (strongBullishBar(2) crosses up shortSma)
Sell rule:
    (shortSma crosses up mediumSma)
*/
public class Sma3StrategyBuilder implements StrategyBuilder
{
    private static final String SMA_INDICATOR_NAME_FORMAT = "SMA(%d)";

    private final BarSeries series;
    private final ClosePriceIndicator closePriceIndicator;
    private final SMAIndicator shortSmaIndicator;
    private final SMAIndicator mediumSmaIndicator;
    private final SMAIndicator longSmaIndicator;
    private final Map<String, Indicator<Num>> numIndicators = new LinkedHashMap<>();

    public Sma3StrategyBuilder(BarSeries series, int shortSmaLength, int mediumSmaLength, int longSmaLength)
    {
        checkInput(series, shortSmaLength, mediumSmaLength, longSmaLength);
        this.series = series;
        this.closePriceIndicator = new ClosePriceIndicator(series);
        this.shortSmaIndicator = new SMAIndicator(closePriceIndicator, shortSmaLength);
        this.mediumSmaIndicator = new SMAIndicator(closePriceIndicator, mediumSmaLength);
        this.longSmaIndicator = new SMAIndicator(closePriceIndicator, longSmaLength);
        numIndicators.put(formSmaIndicatorName(shortSmaLength), shortSmaIndicator);
        numIndicators.put(formSmaIndicatorName(mediumSmaLength), mediumSmaIndicator);
        numIndicators.put(formSmaIndicatorName(longSmaLength), longSmaIndicator);
    }

    @Override
    public Strategy build()
    {
        DownTrendIndicator shortSmaDownTrendIndicator = new DownTrendIndicator(shortSmaIndicator, 7);
        DownTrendIndicator mediumSmaDownTrendIndicator = new DownTrendIndicator(mediumSmaIndicator, 7);
        StrongBarIndicator strongBullishBarIndicator = new StrongBarIndicator(BULLISH, Set.of(BEARISH, BULLISH), 2, series);

        Rule entryRule = new UnderIndicatorRule(shortSmaIndicator, mediumSmaIndicator)
                .and(new BooleanIndicatorRule(shortSmaDownTrendIndicator))
                .and(new BooleanIndicatorRule(mediumSmaDownTrendIndicator))
                .and(new CrossedUpIndicatorRule(closePriceIndicator, shortSmaIndicator))
                .and(new BooleanIndicatorRule(strongBullishBarIndicator));

        Rule exitRule = new CrossedUpIndicatorRule(shortSmaIndicator, mediumSmaIndicator);

        return new BaseStrategy(entryRule, exitRule);
    }

    @Override
    public Optional<Map<String, Indicator<Num>>> getNumIndicators()
    {
        return Optional.of(numIndicators);
    }

    private String formSmaIndicatorName(int smaLength)
    {
        return String.format(SMA_INDICATOR_NAME_FORMAT, smaLength);
    }

    private void checkInput(BarSeries series, int shortSmaLength, int mediumSmaLength, int longSmaLength)
    {
        if (series == null || shortSmaLength >= mediumSmaLength || mediumSmaLength >= longSmaLength)
        {
            throw new IllegalArgumentException();
        }
    }
}
