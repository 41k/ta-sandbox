package root.domain.strategy;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.adx.ADXIndicator;
import org.ta4j.core.indicators.adx.MinusDIIndicator;
import org.ta4j.core.indicators.adx.PlusDIIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.PrecisionNum;
import org.ta4j.core.trading.rules.*;
import root.domain.indicator.bar.SameBarTypeSubSeriesIndicator;
import root.domain.indicator.bar.StrongBarIndicator;

import java.util.Set;

import static root.domain.indicator.bar.BarType.BEARISH;
import static root.domain.indicator.bar.BarType.BULLISH;

public class ADXStrategy2
{
    public static Strategy buildStrategy(BarSeries series)
    {
        ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(series);
        SMAIndicator smaIndicator = new SMAIndicator(closePriceIndicator, 50);

        int adxBarCount = 14;
        ADXIndicator adxIndicator = new ADXIndicator(series, adxBarCount);
        OverIndicatorRule adxOver20Rule = new OverIndicatorRule(adxIndicator, 20);

        PlusDIIndicator plusDIIndicator = new PlusDIIndicator(series, adxBarCount);
        MinusDIIndicator minusDIIndicator = new MinusDIIndicator(series, adxBarCount);

        Rule plusDICrossedUpMinusDI = new CrossedUpIndicatorRule(plusDIIndicator, minusDIIndicator);
        OverIndicatorRule closePriceOverSma = new OverIndicatorRule(closePriceIndicator, smaIndicator);
        Rule entryRule = adxOver20Rule.and(plusDICrossedUpMinusDI).and(closePriceOverSma);

        Rule exitRule = new StopLossRule(closePriceIndicator, PrecisionNum.valueOf(0.0001))
                .or(new StopGainRule(closePriceIndicator, PrecisionNum.valueOf(0.0001)));

//        StrongBarIndicator bearishBarIsStrongerThanPreviousBullishBarIndicator = new StrongBarIndicator(BEARISH, Set.of(BULLISH), 1, series);
//        SameBarTypeSubSeriesIndicator bullishBarSequenceIndicator = new SameBarTypeSubSeriesIndicator(BEARISH, 2, series);
//        Rule exitRule = new StopLossRule(closePriceIndicator, PrecisionNum.valueOf(0.0001))
//                .or(new BooleanIndicatorRule(bearishBarIsStrongerThanPreviousBullishBarIndicator))
//                .or(new BooleanIndicatorRule(bullishBarSequenceIndicator));

        return new BaseStrategy(entryRule, exitRule, adxBarCount);
    }
}
