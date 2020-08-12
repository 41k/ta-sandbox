var request = new XMLHttpRequest();
request.open('GET', 'http://localhost:8881/api/trades');
request.onreadystatechange = () => { if (request.readyState === 4) processResponse() };
request.send();

function processResponse() {
    var trades = JSON.parse(request.responseText);
    for (var i = 0; i < trades.length; i++) {
        drawChart(trades[i], i);
    }
}

function drawChart(trade, index) {
    var options = createOptions(trade);
    var chartSelector = createChartWrapper(index);
    new ApexCharts(document.querySelector(chartSelector), options).render();
}

function createOptions(trade) {
    var options = createCommonOptions(trade);
    addSeries(options, trade);
    addSignals(options, trade);
    return options;
}

function createCommonOptions(trade) {
    var profit = trade.profit;
    var titleText = (profit < 0 ? "DOWN" : "UP") + " [" + profit + "]";
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

function addSeries(options, trade) {
    var series = {
        data: []
    };
    trade.ticks.forEach(tick => {
        series.data.push({
            x: new Date(tick.timestamp),
            y: [tick.open, tick.high, tick.low, tick.close]
        });
    });
    options.series.push(series);
}

function addSignals(options, trade) {
    trade.ticks.forEach(tick => {
        if (tick.signal) {
            var signal = createSignal(tick);
            options.annotations.xaxis.push(signal);
        }
    });
}

function createSignal(tick) {
    var name = tick.signal;
    var timestamp = tick.timestamp;
    var colorHex = (name == 'BUY') ? '#ed5565' : '#1ab394';
    return {
        x: timestamp,
        strokeDashArray: 0,
        borderColor: colorHex,
        yAxisIndex: 0,
        label: {
            show: true,
            text: name,
            offsetX: 8,
            borderWidth: 0,
            style: {
                color: "#fff",
                background: colorHex
            }
        }
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