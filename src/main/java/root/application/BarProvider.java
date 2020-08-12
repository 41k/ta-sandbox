package root.application;

import org.ta4j.core.Bar;

import java.util.List;

public interface BarProvider
{
    List<Bar> getMinuteBars(String symbol);

    List<Bar> getMinuteBars();
}
