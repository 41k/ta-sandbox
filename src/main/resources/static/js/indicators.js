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
    var changeableOptions = createChangeableOptions();
    segment.forEach(tick => processTick(tick, changeableOptions));
    options.series = [
        changeableOptions.resistanceIndicator,
        changeableOptions.closePriceIndicator,
        changeableOptions.supportIndicator
    ];
    options.labels = changeableOptions.xAxisTimeLabels;
    return options;
}

function createCommonOptions(segment, index) {
    var segmentStartIndex = (index * seriesSegmentSize) + 1;
    var segmentEndIndex = segmentStartIndex + segment.length - 1;
    var titleText = segmentStartIndex + ' - ' + segmentEndIndex;
    return {
        chart: {
            height: 550,
            type: 'line'
        },
        dataLabels: {
            enabled: false
        },
        stroke: {
            curve: 'straight'
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
        xaxis: {
            tooltip: {
                enabled: true,
                offsetY: 40,
                formatter: (timestamp) => new Date(timestamp).toUTCString()
            },
            type: 'datetime'
        },
        yaxis: {
            opposite: true
        },
        colors: ['#ED5565', '#3097DE', '#1AB394']
    };
}

function createChangeableOptions(segment, index) {
    return {
        closePriceIndicator: {
            name: "Price",
            data: []
        },
        supportIndicator: {
            name: "Support",
            data: []
        },
        resistanceIndicator: {
            name: "Resistance",
            data: []
        },
        xAxisTimeLabels: []
    }
}

function processTick(tick, options) {
    var indicators = tick.indicators;
    options.closePriceIndicator.data.push(indicators.price);
    options.supportIndicator.data.push(indicators.support);
    options.resistanceIndicator.data.push(indicators.resistance);
    options.xAxisTimeLabels.push(tick.timestamp);
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