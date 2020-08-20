package root.domain.strategy;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.adx.ADXIndicator;
import org.ta4j.core.indicators.adx.MinusDIIndicator;
import org.ta4j.core.indicators.adx.PlusDIIndicator;
import org.ta4j.core.indicators.candles.ThreeWhiteSoldiersIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.PreviousValueIndicator;
import org.ta4j.core.num.PrecisionNum;
import org.ta4j.core.trading.rules.*;
import root.domain.indicator.bar.StrongBarIndicator;

import java.util.Set;

import static root.domain.indicator.bar.BarType.BEARISH;
import static root.domain.indicator.bar.BarType.BULLISH;

public class TWSStrategy
{
    public static Strategy buildStrategy(BarSeries series)
    {
        ThreeWhiteSoldiersIndicator twsIndicator = new ThreeWhiteSoldiersIndicator(series, 20, PrecisionNum.valueOf(1));
        ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(series);
        RSIIndicator rsiIndicator = new RSIIndicator(closePriceIndicator, 2);
        PreviousValueIndicator previous2RsiIndicator = new PreviousValueIndicator(rsiIndicator, 2);

        Rule entryRule = new BooleanIndicatorRule(twsIndicator)
                .and(new UnderIndicatorRule(previous2RsiIndicator, 30));

        Rule exitRule = new StopLossRule(closePriceIndicator, PrecisionNum.valueOf(0.0001))
                .or(new StopGainRule(closePriceIndicator, PrecisionNum.valueOf(0.0001)));

        return new BaseStrategy(entryRule, exitRule);
    }
}
