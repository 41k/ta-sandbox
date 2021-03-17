package root.application;

import org.ta4j.core.Bar;

import java.util.List;

public interface BarProvider
{
    List<Bar> getBars(String symbol, String interval, long fromTimestamp, long toTimestamp);
}
