package root.domain.indicator.sri;

import org.apache.commons.collections4.ListUtils;
import org.ta4j.core.Bar;
import org.ta4j.core.num.Num;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.max;
import static java.util.Collections.min;
import static java.util.stream.Collectors.toList;
import static root.domain.indicator.sri.Zone.Type.RESISTANCE;
import static root.domain.indicator.sri.Zone.Type.SUPPORT;

class SRZonesCalculator
{
    static List<Zone> calculateZones(SRZoneSettings settings)
    {
        List<Zone> supportZones = calculateSupportZones(settings);
        List<Zone> resistanceZones = calculateResistanceZones(settings);
        return Stream.of(supportZones, resistanceZones).flatMap(Collection::stream).collect(toList());
    }

    private static List<Zone> calculateSupportZones(SRZoneSettings settings)
    {
        List<Zone> supportZones = new ArrayList<>();
        Num zoneHeight = settings.getZoneHeight();
        Integer minStrength = settings.getMinStrength();
        List<List<Num>> segments = formSegments(settings);
        List<Num> minValuesOfSegments = segments.stream().map(Collections::min).collect(toList());
        while (!minValuesOfSegments.isEmpty())
        {
            Num zoneLowerBound = min(minValuesOfSegments);
            Num zoneUpperBound = zoneLowerBound.plus(zoneHeight);
            List<Num> zoneValues = getValuesInRange(minValuesOfSegments, zoneLowerBound, zoneUpperBound);
            minValuesOfSegments.removeAll(zoneValues);
            Zone zone = new Zone(SUPPORT, zoneHeight, zoneValues);
            if (zone.getStrength() >= minStrength)
            {
                supportZones.add(zone);
            }
        }
        return supportZones;
    }

    private static List<Zone> calculateResistanceZones(SRZoneSettings settings)
    {
        List<Zone> resistanceZones = new ArrayList<>();
        Num zoneHeight = settings.getZoneHeight();
        Integer minStrength = settings.getMinStrength();
        List<List<Num>> segments = formSegments(settings);
        List<Num> maxValuesOfSegments = segments.stream().map(Collections::max).collect(toList());
        while (!maxValuesOfSegments.isEmpty())
        {
            Num zoneUpperBound = max(maxValuesOfSegments);
            Num zoneLowerBound = zoneUpperBound.minus(zoneHeight);
            List<Num> zoneValues = getValuesInRange(maxValuesOfSegments, zoneLowerBound, zoneUpperBound);
            maxValuesOfSegments.removeAll(zoneValues);
            Zone zone = new Zone(RESISTANCE, zoneHeight, zoneValues);
            if (zone.getStrength() >= minStrength)
            {
                resistanceZones.add(zone);
            }
        }
        return resistanceZones;
    }

    private static List<List<Num>> formSegments(SRZoneSettings settings)
    {
        List<Num> closePriceSeries = formClosePriceSeries(settings);
        Integer segmentSize = settings.getSegmentSize();
        return ListUtils.partition(closePriceSeries, segmentSize)
                .stream()
                .filter(segment -> segment.size() == segmentSize)
                .collect(toList());
    }

    private static List<Num> formClosePriceSeries(SRZoneSettings settings)
    {
        if (isNotValid(settings))
        {
            return List.of();
        }
        return settings.getSeries()
                .stream()
                .map(Bar::getClosePrice)
                .collect(toList());
    }

    private static List<Num> getValuesInRange(List<Num> values, Num lowerBound, Num upperBound)
    {
        return values.stream()
                .filter(value -> value.isGreaterThanOrEqual(lowerBound) && value.isLessThanOrEqual(upperBound))
                .collect(toList());
    }

    private static boolean isNotValid(SRZoneSettings settings)
    {
        List<Bar> series = settings.getSeries();
        Integer calculationWindowSize = settings.getCalculationWindowSize();
        return series.size() < calculationWindowSize;
    }

    private SRZonesCalculator()
    {
    }
}
