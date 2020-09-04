var seriesSegmentSize = 100;

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

function drawChart(segment, index) {
    var options = createOptions(segment, index);
    var chartSelector = createChartWrapper(index);
    new ApexCharts(document.querySelector(chartSelector), options).render();
}

function createOptions(segment, index) {
    var options = createCommonOptions(segment, index);
    addLineIndicators(segment, options);
    addBarSeries(segment, options);
    return options;
}

function createCommonOptions(segment, index) {
    var segmentStartIndex = (index * seriesSegmentSize);
    var segmentEndIndex = segmentStartIndex + segment.length - 1;
    var titleText = 'Tick indexes: ' + segmentStartIndex + ' - ' + segmentEndIndex;
    return {
        series: [],
        annotations: {
            xaxis: []
        },
        chart: {
            height: 550,
            type: 'line'
        },
        title: {
            text: titleText,
            align: 'left'
        },
        stroke: {
            width: []
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
            opposite: true,
            labels: {
                formatter: (val) => val.toFixed(2)
            }
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

function addBarSeries(segment, options) {
    var series = {
        name: 'Close Price',
        type: 'candlestick',
        data: []
    };
    segment.forEach(tick => {
        series.data.push({
            x: new Date(tick.timestamp),
            y: [tick.open, tick.high, tick.low, tick.close]
        });
    });
    options.series.push(series);
    options.stroke.width.push(1);
}

function addLineIndicators(segment, options) {
    var indicatorNameToSeriesMap = {};
    for (const indicator of Object.entries(segment[0].priceChartNumIndicators)) {
        var indicatorName = indicator[0];
        var series = {
            name: indicatorName,
            type: 'line',
            data: []
        };
        indicatorNameToSeriesMap[indicatorName] = series;
    }
    segment.forEach(tick => {
        for (const indicator of Object.entries(tick.priceChartNumIndicators)) {
            var indicatorName = indicator[0];
            var indicatorValue = indicator[1];
            indicatorNameToSeriesMap[indicatorName].data.push({
                x: new Date(tick.timestamp),
                y: indicatorValue
            });
        }
    });
    for (const entry of Object.entries(indicatorNameToSeriesMap)) {
        options.series.push(entry[1]);
        options.stroke.width.push(2);
    }
}

function createChartWrapper(index) {
    var chartDivId = "chart-" + index;
    var chartDivClass = "chart-box";
    var chartDiv = document.createElement("div");
    chartDiv.setAttribute("id", chartDivId);
    chartDiv.setAttribute("class", chartDivClass);
    document.getElementById("charts").appendChild(chartDiv);
    return "#" + chartDivId;
}