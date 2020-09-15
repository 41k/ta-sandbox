function ChartRenderer(wrapperId, priceChartType) {

    var wrapperId = wrapperId;
    var priceChartType = priceChartType;
    var additionalChartIndicatorTypes = ['RSI'];

    this.renderTickSeries = function(tickSeries, seriesSegmentSize) {
        var tickSeriesSegments = formSeriesSegments(tickSeries, seriesSegmentSize);
        for (var i = 0; i < tickSeriesSegments.length; i++) {
            var ticks = tickSeriesSegments[i];
            renderTickSeriesSegment(ticks, i, seriesSegmentSize);
        }
    }

    this.renderTrades = function(trades) {
        for (var i = 0; i < trades.length; i++) {
            var trade = trades[i];
            renderTrade(trade, i);
        }
    }

    var renderTickSeriesSegment = function(ticks, index, seriesSegmentSize) {
        var chartsGroupWrapper = createTickSeriesSegmentChartsGroupWrapper(ticks, index, seriesSegmentSize);
        var chartsGroupName = 'group-' + index;
        renderMainChart(ticks, index, chartsGroupWrapper, chartsGroupName);
        renderAdditionalCharts(ticks, index, chartsGroupWrapper, chartsGroupName);
    }

    var createTickSeriesSegmentChartsGroupWrapper = function(ticks, index, seriesSegmentSize) {
        var tickSeriesWrapperId = 'tick-series-' + index;
        var tickSeriesWrapperClass = 'tick-series-wrapper';
        var tickSeriesWrapper = document.createElement('div');
        tickSeriesWrapper.setAttribute('id', tickSeriesWrapperId);
        tickSeriesWrapper.setAttribute('class', tickSeriesWrapperClass);
        addTickSeriesLabel(ticks, index, seriesSegmentSize, tickSeriesWrapper);
        document.getElementById(wrapperId).appendChild(tickSeriesWrapper);
        return tickSeriesWrapper;
    }

    var addTickSeriesLabel = function(segment, index, seriesSegmentSize, tickSeriesWrapper) {
        var segmentStartIndex = (index * seriesSegmentSize);
        var segmentEndIndex = segmentStartIndex + segment.length - 1;
        var labelText = 'Tick indexes: ' + segmentStartIndex + ' - ' + segmentEndIndex;
        var tickSeriesLabel = document.createElement('p');
        tickSeriesLabel.textContent = labelText;
        tickSeriesWrapper.appendChild(tickSeriesLabel);
    }

    var renderTrade = function(trade, index) {
        var ticks = trade.ticks;
        var chartsGroupWrapper = createTradeChartsGroupWrapper(trade, index);
        var chartsGroupName = 'group-' + index;
        renderMainChart(ticks, index, chartsGroupWrapper, chartsGroupName);
        renderAdditionalCharts(ticks, index, chartsGroupWrapper, chartsGroupName);
    }

    var createTradeChartsGroupWrapper = function(trade, index) {
        var tradeWrapperId = 'trade-' + index;
        var tradeWrapperClass = 'trade-wrapper';
        var tradeWrapper = document.createElement('div');
        tradeWrapper.setAttribute('id', tradeWrapperId);
        tradeWrapper.setAttribute('class', tradeWrapperClass);
        addTradeLabel(tradeWrapper, trade);
        document.getElementById(wrapperId).appendChild(tradeWrapper);
        return tradeWrapper;
    }

    var addTradeLabel = function(tradeWrapper, trade) {
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

    var renderMainChart = function(ticks, index, chartsGroupWrapper, chartsGroupName) {
        var options = createMainChartOptions(ticks, chartsGroupName);
        var mainChartWrapperId = 'MAIN-chart-' + index;
        var mainChartWrapper = createChartWrapper(mainChartWrapperId);
        chartsGroupWrapper.appendChild(mainChartWrapper);
        new ApexCharts(mainChartWrapper, options).render();
    }

    var createMainChartOptions = function(ticks, chartsGroupName) {
        var options = createCommonOptionsForMainChart(chartsGroupName);
        addLineIndicators(options, ticks);
        addPriceSeries(options, ticks);
        addSignals(options, ticks);
        return options;
    }

    var createCommonOptionsForMainChart = function(chartsGroupName) {
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

    var createChartWrapper = function(chartWrapperId) {
        var chartWrapper = document.createElement("div");
        chartWrapper.setAttribute("id", chartWrapperId);
        return chartWrapper;
    }

    var addLineIndicators = function(options, ticks) {
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

    var addPriceSeries = function(options, ticks) {
        switch (priceChartType) {
            case 'candlestick':
                addBarSeries(options, ticks);
                break;
            case 'line':
                addPriceLine(options, ticks);
                break;
        }
    }

    var addBarSeries = function(options, ticks) {
        var series = {
            name: 'Close Price',
            type: 'candlestick',
            data: []
        };
        ticks.forEach(tick => {
            series.data.push({
                x: new Date(tick.timestamp),
                y: [tick.open, tick.high, tick.low, tick.close]
            });
        });
        options.series.push(series);
        options.stroke.width.push(1);
    }

    var addPriceLine = function(options, ticks) {
        var series = {
            name: 'Close Prise',
            data: []
        };
        ticks.forEach(tick => {
            series.data.push({
                x: new Date(tick.timestamp),
                y: tick.close
            });
       });
       options.series.push(series);
       options.stroke.width.push(4);
    }

    var addSignals = function(options, ticks) {
        ticks.forEach(tick => {
            if (tick.signal) {
                var signal = createSignal(tick);
                options.annotations.xaxis.push(signal);
            }
        });
    }

    var createSignal = function(tick) {
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

    var renderAdditionalCharts = function(ticks, index, chartsGroupWrapper, chartsGroupName) {
        var indicatorNameToSeriesMap = formAdditionalChartNumIndicatorNameToSeriesMap(ticks);
        additionalChartIndicatorTypes.forEach(indicatorType => {
            renderAdditionalChart(indicatorType, indicatorNameToSeriesMap, ticks, index, chartsGroupWrapper, chartsGroupName);
        });
    }

    var formAdditionalChartNumIndicatorNameToSeriesMap = function(ticks) {
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

    var renderAdditionalChart = function(indicatorType, indicatorNameToSeriesMap, ticks, index, chartsGroupWrapper, chartsGroupName) {
        var indicatorTypeSeries = getSeriesForIndicatorType(indicatorType, indicatorNameToSeriesMap);
        if (Object.keys(indicatorTypeSeries).length === 0) {
            return;
        }
        var options = createCommonOptionsForAdditionalChart(chartsGroupName, indicatorType);
        addSeries(options, indicatorTypeSeries);
        addXAxisTimeLabels(options, ticks);
        var additionalChartWrapperId = indicatorType + '-chart-' + index;
        var additionalChartWrapper = createChartWrapper(additionalChartWrapperId);
        chartsGroupWrapper.appendChild(additionalChartWrapper);
        new ApexCharts(additionalChartWrapper, options).render();
    }

    var getSeriesForIndicatorType = function(indicatorType, indicatorNameToSeriesMap) {
        var indicatorTypeSeries = [];
        for (const entry of Object.entries(indicatorNameToSeriesMap)) {
            var indicatorName = entry[0];
            var series = entry[1];
            if (indicatorName.startsWith(indicatorType)) {
                indicatorTypeSeries.push(series);
            }
        }
        return indicatorTypeSeries;
    }

    var createCommonOptionsForAdditionalChart = function(chartsGroupName, chartTitle) {
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

    var addSeries = function(options, indicatorTypeSeries) {
        indicatorTypeSeries.forEach(series => {
            options.series.push(series);
        });
    }

    var addXAxisTimeLabels = function(options, ticks) {
        ticks.forEach(tick => {
            options.labels.push(tick.timestamp);
        });
    }

    var formSeriesSegments = function(series, segmentSize) {
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
}