package root;

import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import root.domain.indicator.sri.ResistanceIndicator;
import root.domain.indicator.sri.SRISettings;
import root.domain.indicator.sri.SupportIndicator;
import root.infrastructure.BinanceExchangeBarProvider;

public class App
{
    public static void main(String[] args)
    {
        var barProvider = new BinanceExchangeBarProvider();
        var bars = barProvider.getMinuteBars();
        var series = new BaseBarSeries(bars);
        var closePriceIndicator = new ClosePriceIndicator(series);
        var sriSettings = SRISettings.builder().calculationWindowSize(100).segmentSize(20).zoneHeight(20).build();
        var supportIndicator = new SupportIndicator(series, sriSettings);
        var resistanceIndicator = new ResistanceIndicator(series, sriSettings);

        System.out.println();
        System.out.println();
        for (int i = 98; i <= series.getEndIndex(); i++)
        {
            System.out.println("[" + i + "]");
            System.out.println();
            System.out.println("Resistance: " + resistanceIndicator.getValue(i));
            System.out.println();
            System.out.println("Close price: " + closePriceIndicator.getValue(i));
            System.out.println();
            System.out.println("Support: " + supportIndicator.getValue(i));
            System.out.println("-----------------------------");
        }
    }
}
