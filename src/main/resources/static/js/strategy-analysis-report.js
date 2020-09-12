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
    document.getElementById("rr").textContent = report.riskRewardRatio;
}

function drawTrades(report) {
    var trades = report.trades;
    for (var i = 0; i < trades.length; i++) {
        var trade = trades[i];
        drawTrade(trade, i);
    }
}

function drawTrade(trade, index) {
    var tradeWrapper = createTradeWrapper(trade, index);
    var chartsGroupName = 'group-' + index;
    drawMainChart(trade, index, tradeWrapper, chartsGroupName);
    drawAdditionalCharts(trade, index, tradeWrapper, chartsGroupName);
}

function createTradeWrapper(trade, index) {
    var tradeWrapperId = 'trade-' + index;
    var tradeWrapperClass = 'trade-wrapper';
    var tradeWrapper = document.createElement('div');
    tradeWrapper.setAttribute('id', tradeWrapperId);
    tradeWrapper.setAttribute('class', tradeWrapperClass);
    addTradeLabel(tradeWrapper, trade);
    document.getElementById('trades').appendChild(tradeWrapper);
    return tradeWrapper;
}

function addTradeLabel(tradeWrapper, trade) {
    var tradeLabel = document.createElement('p');
    var profit = trade.profit;
    var isProfitableTrade = profit > 0;
    var labelClass = isProfitableTrade ? 'up' : 'down';
    tradeLabel.setAttribute('class', labelClass);
    var profitText = (isProfitableTrade ? 'UP' : 'DOWN') + '[' + profit + ']';
    var fromToIndexesText = '[' + trade.entryIndex + ':' + trade.exitIndex + ']';
    var strategyId = trade.strategyId;
    var delimiter = ' --- ';
    var labelText = profitText + delimiter + fromToIndexesText + delimiter + strategyId;
    tradeLabel.textContent = labelText;
    tradeWrapper.appendChild(tradeLabel);
}

function drawMainChart(trade, index, tradeWrapper, chartsGroupName) {
    var options = createMainChartOptions(trade, chartsGroupName);
    var mainChartWrapperId = 'MAIN-chart-' + index;
    var mainChartWrapper = createChartWrapper(mainChartWrapperId);
    tradeWrapper.appendChild(mainChartWrapper);
    new ApexCharts(mainChartWrapper, options).render();
}

function createMainChartOptions(trade, chartsGroupName) {
    var options = createCommonOptionsForMainChart(chartsGroupName);
    addLineIndicators(options, trade);
    addBarSeries(options, trade);
    addSignals(options, trade);
    return options;
}

function createCommonOptionsForMainChart(chartsGroupName) {
    return {
        series: [],
        annotations: {
            xaxis: []
        },
        chart: {
            height: 350,
            type: 'line',
            group: chartsGroupName,
            toolbar: {
                show: false
            }
        },
        title: {
            text: 'MAIN',
            align: 'center'
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
                formatter: (val) => val.toFixed(2),
                minWidth: 50
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

function createChartWrapper(chartWrapperId) {
    var chartWrapper = document.createElement("div");
    chartWrapper.setAttribute("id", chartWrapperId);
    return chartWrapper;
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
    for (const indicator of Object.entries(ticks[0].mainChartNumIndicators)) {
        var indicatorName = indicator[0];
        var series = {
            name: indicatorName,
            type: 'line',
            data: []
        };
        indicatorNameToSeriesMap[indicatorName] = series;
    }
    ticks.forEach(tick => {
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

function drawAdditionalCharts(trade, index, tradeWrapper, chartsGroupName) {
    var indicatorTypes = ['RSI'];
    var indicatorNameToSeriesMap = formAdditionalChartNumIndicatorNameToSeriesMap(trade);
    indicatorTypes.forEach(indicatorType => {
        drawAdditionalChart(indicatorType, indicatorNameToSeriesMap, trade, index, tradeWrapper, chartsGroupName);
    });
}

function formAdditionalChartNumIndicatorNameToSeriesMap(trade) {
    var ticks = trade.ticks;
    var indicatorNameToSeriesMap = {};
    for (const indicator of Object.entries(ticks[0].additionalChartNumIndicators)) {
        var indicatorName = indicator[0];
        var series = {
            name: indicatorName,
            data: []
        };
        indicatorNameToSeriesMap[indicatorName] = series;
    }
    ticks.forEach(tick => {
        for (const indicator of Object.entries(tick.additionalChartNumIndicators)) {
            var indicatorName = indicator[0];
            var indicatorValue = indicator[1];
            indicatorNameToSeriesMap[indicatorName].data.push({
                x: new Date(tick.timestamp),
                y: indicatorValue
            });
        }
    });
    return indicatorNameToSeriesMap;
}

function drawAdditionalChart(indicatorType, indicatorToSeriesMap, trade, index, tradeWrapper, chartsGroupName) {
    var indicatorTypeSeries = getSeriesForIndicatorType(indicatorType, indicatorToSeriesMap);
    if (Object.keys(indicatorTypeSeries).length === 0) {
        return;
    }
    var options = createCommonOptionsForAdditionalChart(chartsGroupName, indicatorType);
    addSeries(options, indicatorTypeSeries);
    addXAxisTimeLabels(trade, options);
    var additionalChartWrapperId = indicatorType + '-chart-' + index;
    var additionalChartWrapper = createChartWrapper(additionalChartWrapperId);
    tradeWrapper.appendChild(additionalChartWrapper);
    new ApexCharts(additionalChartWrapper, options).render();
}

function getSeriesForIndicatorType(indicatorType, indicatorToSeriesMap) {
    var indicatorTypeSeries = [];
    for (const entry of Object.entries(indicatorToSeriesMap)) {
        var indicatorName = entry[0];
        var series = entry[1];
        if (indicatorName.startsWith(indicatorType)) {
            indicatorTypeSeries.push(series);
        }
    }
    return indicatorTypeSeries;
}

function createCommonOptionsForAdditionalChart(chartsGroupName, chartTitle) {
    return {
        chart: {
            height: 250,
            type: 'line',
            group: chartsGroupName,
            toolbar: {
                show: false
            }
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
            text: chartTitle,
            align: 'center'
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
                formatter: (val) => val.toFixed(2),
                minWidth: 40
            }
        }
    };
}

function addSeries(options, indicatorTypeSeries) {
    indicatorTypeSeries.forEach(series => {
        options.series.push(series);
    });
}

function addXAxisTimeLabels(trade, options) {
    trade.ticks.forEach(tick => {
        options.labels.push(tick.timestamp);
    });
}