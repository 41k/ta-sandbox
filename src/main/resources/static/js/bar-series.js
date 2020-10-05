var seriesSegmentSize = 250;

var request = new XMLHttpRequest();
request.open('GET', 'http://localhost:8881/api/series');
request.onreadystatechange = () => { if (request.readyState === 4) processResponse() };
request.send();

function processResponse() {
    var series = JSON.parse(request.responseText);
    var segments = formSegments(series, seriesSegmentSize);
    for (var i = 0; i < segments.length; i++) {
        drawChart(segments[i], i);
    }
}

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

function drawChart(segment, index) {
    var options = createOptions(segment, index);
    var chartSelector = createChartWrapper(index);
    new ApexCharts(document.querySelector(chartSelector), options).render();
}

function createOptions(segment, index) {
    var options = createCommonOptions(segment, index);
    addSeries(segment, options)
    return options;
}

function createCommonOptions(segment, index) {
    var segmentStartIndex = (index * seriesSegmentSize) + 1;
    var segmentEndIndex = segmentStartIndex + segment.length - 1;
    var titleText = segmentStartIndex + ' - ' + segmentEndIndex;
    return {
        series: [],
        annotations: {
            xaxis: []
        },
        chart: {
            height: 550,
            type: 'candlestick'
        },
        title: {
            text: titleText,
            align: 'left'
        },
        xaxis: {
            tooltip: {
                enabled: true,
                offsetY: 40,
                formatter: (timestamp) => new Date(timestamp).toUTCString()
            },
            type: 'datetime',
        },
        yaxis: {
            opposite: true
        },
        plotOptions: {
            candlestick: {
                colors: {
                    upward: '#1ab394',
                    downward: '#ed5565'
                }
            }
        }
    };
}

function addSeries(segment, options) {
    var series = {
        data: []
    };
    segment.forEach(tick => {
        series.data.push({
            x: new Date(tick.timestamp),
            y: [tick.open, tick.high, tick.low, tick.close]
        });
    });
    options.series.push(series);
}

function createChartWrapper(index) {
    var chartDivId = "chart-" + index;
    var chartDiv = document.createElement("div");
    chartDiv.setAttribute("id", chartDivId);
    document.getElementById("charts").appendChild(chartDiv);
    return "#" + chartDivId;
}