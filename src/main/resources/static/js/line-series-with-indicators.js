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
    addPriceLine(segment, options);
    addXAxisTimeLabels(segment, options);
    return options;
}

function createCommonOptions(segment, index) {
    var segmentStartIndex = (index * seriesSegmentSize);
    var segmentEndIndex = segmentStartIndex + segment.length - 1;
    var titleText = 'Tick indexes: ' + segmentStartIndex + ' - ' + segmentEndIndex;
    return {
        chart: {
            height: 550,
            type: 'line'
        },
        series: [],
        dataLabels: {
            enabled: false
        },
        stroke: {
            curve: 'straight',
            width: 2
        },
        grid: {
            padding: {
                right: 30,
                left: 20
            }
        },
        title: {
            text: titleText,
            align: 'left'
        },
        labels: [],
        xaxis: {
            tooltip: {
                enabled: true,
                offsetY: 40,
                formatter: (timestamp) => new Date(timestamp).toUTCString()
            },
            type: 'datetime'
        },
        yaxis: {
            opposite: true,
            labels: {
                formatter: (val) => val.toFixed(2)
            }
        }
    };
}

function addLineIndicators(segment, options) {
    var indicatorNameToSeriesMap = {};
    for (const indicator of Object.entries(segment[0].mainChartNumIndicators)) {
        var indicatorName = indicator[0];
        var series = {
            name: indicatorName,
            data: []
        };
        indicatorNameToSeriesMap[indicatorName] = series;
    }
    segment.forEach(tick => {
        for (const indicator of Object.entries(tick.mainChartNumIndicators)) {
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
    }
}

function addPriceLine(segment, options) {
    var series = {
        name: 'Close Prise',
        data: []
    };
    segment.forEach(tick => {
        series.data.push({
            x: new Date(tick.timestamp),
            y: tick.close
        });
   });
   options.series.push(series);
}

function addXAxisTimeLabels(segment, options) {
    segment.forEach(tick => {
        options.labels.push(tick.timestamp);
    });
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