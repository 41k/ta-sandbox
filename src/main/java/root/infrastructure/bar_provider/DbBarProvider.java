package root.infrastructure.bar_provider;

import lombok.RequiredArgsConstructor;
import org.ta4j.core.Bar;
import root.application.BarProvider;
import root.infrastructure.persistence.BarDbEntry;
import root.infrastructure.persistence.BarRepository;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
public class DbBarProvider implements BarProvider
{
    private final BarRepository barRepository;

    @Override
    public List<Bar> getBars()
    {
        var symbol = "ETH_USD";
        //var symbol = "BTC_USD";
        //var interval = "ONE_MINUTE";
        var interval = "FIVE_MINUTES";
        //var interval = "FIFTEEN_MINUTES";
        //var interval = "THIRTY_MINUTES";
        //var interval = "ONE_HOUR";
        var fromTimestamp = 1610196540000L;
        var toTimestamp = 1615360990000L;
        return barRepository.getBarsInTimeRange(symbol, interval, fromTimestamp, toTimestamp)
                .stream()
                .map(BarDbEntry::toDomainObject)
                .collect(toList());
    }
}
