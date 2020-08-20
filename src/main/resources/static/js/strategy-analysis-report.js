var request = new XMLHttpRequest();
request.open('GET', 'http://localhost:8881/api/strategy/analysis-report');
request.onreadystatechange = () => { if (request.readyState === 4) processResponse() };
request.send();

function processResponse() {
    var report = JSON.parse(request.responseText);
    drawReport(report);
    drawTrades(report);
}

function drawReport(report) {
    document.getElementById("total-profit").textContent = report.totalProfit;
    document.getElementById("n-trades").textContent = report.trades.length;
    document.getElementById("n-up-trades").textContent = report.nprofitableTrades;
    document.getElementById("n-down-trades").textContent = report.nunprofitableTrades;
    document.getElementById("rr").textContent = report.riskRewardRation;
}

function drawTrades(report) {
    var trades = report.trades;
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
    addLineIndicators(options, trade);
    addBarSeries(options, trade);
    addSignals(options, trade);
    return options;
}

function createCommonOptions(trade) {
    var profit = trade.profit;
    var profitText = (profit < 0 ? 'DOWN' : 'UP') + '[' + profit + ']';
    var entryIndexText = trade.entryIndex ? 'EntryIndex[' + trade.entryIndex + ']' : '';
    var exitIndexText = trade.exitIndex ? 'ExitIndex[' + trade.exitIndex + ']' : '';
    var delimiter = ' --- ';
    var titleText = profitText + delimiter + entryIndexText + delimiter + exitIndexText;
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

function addBarSeries(options, trade) {
    var series = {
        name: 'Close Price',
        type: 'candlestick',
        data: []
    };
    trade.ticks.forEach(tick => {
        series.data.push({
            x: new Date(tick.timestamp),
            y: [tick.open, tick.high, tick.low, tick.close]
        });
    });
    options.series.push(series);
    options.stroke.width.push(1);
}

function addLineIndicators(options, trade) {
    var ticks = trade.ticks;
    var indicatorNameToSeriesMap = {};
    for (const indicator of Object.entries(ticks[0].indicators)) {
        var indicatorName = indicator[0];
        var series = {
            name: indicatorName,
            type: 'line',
            data: []
        };
        indicatorNameToSeriesMap[indicatorName] = series;
    }
    ticks.forEach(tick => {
        for (const indicator of Object.entries(tick.indicators)) {
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