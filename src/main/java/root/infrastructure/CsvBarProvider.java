package root.infrastructure;

import com.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import root.application.BarProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CsvBarProvider implements BarProvider
{
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public List<Bar> getMinuteBars()
    {
        return getMinuteBars("data/appleinc_bars_from_20130101_usd.csv");
    }

    @Override
    public List<Bar> getMinuteBars(String filename)
    {
        List<Bar> bars = new ArrayList<>();
        InputStream stream = CsvBarProvider.class.getClassLoader().getResourceAsStream(filename);
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(stream, StandardCharsets.UTF_8), ',', '"', 1))
        {
            String[] line;
            while ((line = csvReader.readNext()) != null)
            {
                Bar bar = buildBar(line, Duration.ofDays(1));
                bars.add(bar);
            }
        } catch (IOException ioe) {
            log.error("Unable to load bars from CSV", ioe);
        } catch (NumberFormatException nfe) {
            log.error("Error while parsing value", nfe);
        }
        return bars;
    }

    private Bar buildBar(String[] line, Duration duration)
    {
        ZonedDateTime date = LocalDate.parse(line[0], DATE_FORMAT).atStartOfDay(ZoneId.systemDefault());
        String open = line[1];
        String high = line[2];
        String low = line[3];
        String close = line[4];
        String volume = line[5];
        return new BaseBar(duration, date, open, high, low, close, volume);
    }
}
