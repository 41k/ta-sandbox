function formSegments(series, segmentSize) {
    var segments = [];
    var tickIndexToProcess = 0;
    var seriesSize = series.length;
    while (tickIndexToProcess < seriesSize) {
        var segment = [];
        for (var i = tickIndexToProcess; i < seriesSize; i++) {
            segment.push(series[i]);
            tickIndexToProcess = i + 1;
            if (tickIndexToProcess % segmentSize === 0 || tickIndexToProcess === seriesSize) {
                segments.push(segment);
                break;
            }
        }
    }
    return segments;
}