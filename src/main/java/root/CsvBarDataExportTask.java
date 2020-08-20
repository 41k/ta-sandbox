package root;

import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.ta4j.core.Bar;
import root.application.BarProvider;

import java.io.File;
import java.io.FileWriter;

import static java.lang.String.format;

//@Component
@RequiredArgsConstructor
public class CsvBarDataExportTask implements CommandLineRunner
{
    private static final String FILE_NAME_FORMAT = "C:/Users/41k/41k/dev/projects/crypto-trade/data/%d.csv";

    private final BarProvider barProvider;

    @Override
    public void run(String... args)
    {
        export();
    }

    @SneakyThrows
    private void export()
    {
        var csvFileWriter = initCsvFileWriter();
        barProvider.getMinuteBars().stream()
                .map(this::convertBarToOHLCVTArray)
                .forEach(csvFileWriter::writeNext);
        csvFileWriter.close();
    }

    @SneakyThrows
    private CSVWriter initCsvFileWriter()
    {
        var file = new File(format(FILE_NAME_FORMAT, System.nanoTime()));
        file.createNewFile();
        return new CSVWriter(new FileWriter(file));
    }

    private String[] convertBarToOHLCVTArray(Bar bar)
    {
        return new String[]{
                bar.getOpenPrice().toString(),
                bar.getHighPrice().toString(),
                bar.getLowPrice().toString(),
                bar.getClosePrice().toString(),
                bar.getVolume().toString(),
                String.valueOf(bar.getEndTime().toInstant().toEpochMilli())
        };
    }
}
