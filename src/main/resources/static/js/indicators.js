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
        //changeableOptions.resistanceIndicator,
        changeableOptions.closePriceIndicator,
        //changeableOptions.supportIndicator,
        changeableOptions.shortSmaIndicator,
        changeableOptions.mediumSmaIndicator,
        changeableOptions.longSmaIndicator,
        //changeableOptions.longEmaIndicator,
        //changeableOptions.shortEmaIndicator
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
        colors: ['#000000', '#ED5565', '#1AB394', '#3097DE']
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
        longSmaIndicator: {
            name: "Long SMA",
            data: []
        },
        mediumSmaIndicator: {
            name: "Medium SMA",
            data: []
        },
        shortSmaIndicator: {
            name: "Short SMA",
            data: []
        },
        longEmaIndicator: {
            name: "Long EMA",
            data: []
        },
        shortEmaIndicator: {
            name: "Short EMA",
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
    options.shortSmaIndicator.data.push(indicators.shortSma);
    options.mediumSmaIndicator.data.push(indicators.mediumSma);
    options.longSmaIndicator.data.push(indicators.longSma);
    options.longEmaIndicator.data.push(indicators.longEma);
    options.shortEmaIndicator.data.push(indicators.shortEma);
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