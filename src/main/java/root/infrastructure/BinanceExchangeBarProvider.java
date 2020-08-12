package root.infrastructure;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import root.application.BarProvider;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class BinanceExchangeBarProvider implements BarProvider
{
    private static final String DEFAULT_SYMBOL = "BTCUSDT";
    private static final Duration MINUTE = Duration.ofMinutes(1);

    private final BinanceApiRestClient client;

    public BinanceExchangeBarProvider()
    {
        var factory = BinanceApiClientFactory.newInstance();
        this.client = factory.newRestClient();
    }

    public List<Bar> getMinuteBars()
    {
        return getMinuteBars(DEFAULT_SYMBOL);
    }

    public List<Bar> getMinuteBars(String symbol)
    {
        return client.getCandlestickBars(symbol, CandlestickInterval.ONE_MINUTE)
                .stream()
                .map(candlestick -> buildBar(candlestick, MINUTE))
                .collect(toList());
    }

    private Bar buildBar(Candlestick candlestick, Duration duration)
    {
        var barTime = Instant.ofEpochMilli(candlestick.getCloseTime());
        var zonedBarTime = ZonedDateTime.ofInstant(barTime, ZoneId.systemDefault());
        return new BaseBar(duration, zonedBarTime,
                candlestick.getOpen(),
                candlestick.getHigh(),
                candlestick.getLow(),
                candlestick.getClose(),
                candlestick.getVolume());
    }
}
