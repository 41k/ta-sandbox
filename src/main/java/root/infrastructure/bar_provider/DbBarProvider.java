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
    public List<Bar> getBars(String symbol, String interval, long fromTimestamp, long toTimestamp)
    {
        return barRepository.getBarsInTimeRange(symbol, interval, fromTimestamp, toTimestamp)
                .stream()
                .map(BarDbEntry::toDomainObject)
                .collect(toList());
    }
}
