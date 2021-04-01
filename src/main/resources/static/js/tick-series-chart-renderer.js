function TickSeriesChartRenderer(priceChartType) {

    var priceChartType = priceChartType;
    var dateTimeFormat = 'yyyy-mm-dd HH:MM:ss';
    var timeFormat = 'HH:MM:ss';
    var additionalChartIndicatorTypes = ['Shadow', 'ADX', 'RSI', 'MACD', 'OBV', 'WR'];

    this.renderTickSeries = function(tickSeries, seriesSegmentSize, wrapperId) {
        var tickSeriesSegments = formSeriesSegments(tickSeries, seriesSegmentSize);
        tickSeriesSegments.forEach(segment => renderTickSeriesSegment(segment, wrapperId));
    }

    this.renderTrades = function(trades, wrapperId) {
        trades.forEach(trade => this.renderTrade(trade, wrapperId));
    }

    this.renderTrade = function(trade, wrapperId) {
        var ticks = trade.ticks;
        var chartsGroupWrapper = createTradeChartsGroupWrapper(trade, wrapperId);
        renderMainChart(ticks, chartsGroupWrapper);
        renderAdditionalCharts(ticks, chartsGroupWrapper);
    }

    var renderTickSeriesSegment = function(ticks, wrapperId) {
        var chartsGroupWrapper = createTickSeriesSegmentChartsGroupWrapper(ticks, wrapperId);
        renderMainChart(ticks, chartsGroupWrapper);
        renderAdditionalCharts(ticks, chartsGroupWrapper);
    }

    var createTickSeriesSegmentChartsGroupWrapper = function(ticks, wrapperId) {
        var segmentPanelHeader = createSegmentPanelHeader(ticks);
        var segmentPanelBody = createDomElement('div', 'panel-body');
        var segmentPanel = createDomElementWithChildren('div', [segmentPanelHeader, segmentPanelBody], 'm-t-30 panel panel-primary');
        document.getElementById(wrapperId).appendChild(segmentPanel);
        return segmentPanelBody;
    }

    var createSegmentPanelHeader = function(segment) {
        var segmentStartTimestamp = segment[0].timestamp;
        var segmentEndTimestamp = segment[segment.length - 1].timestamp;
        var segmentStartTime = formDateTimeString(segmentStartTimestamp, dateTimeFormat);
        var segmentEndTime = formDateTimeString(segmentEndTimestamp, dateTimeFormat);
        var labelText = 'From [' + segmentStartTime + '] To [' + segmentEndTime + ']';
        var h3 = createDomElementWithInnerHTML('h3', labelText, 'panel-title')
        var panelHeader = createDomElementWithChildren('div', [h3], 'panel-heading');
        return panelHeader;
    }

    var createTradeChartsGroupWrapper = function(trade, wrapperId) {
        var tradePanelHeader = createTradePanelHeader(trade);
        var tradePanelBody = createDomElement('div', 'panel-body');
        var panelTypeClass = trade.profit > 0 ? 'panel-success' : 'panel-danger';
        var panelClasses = 'm-t-30 panel ' + panelTypeClass;
        var tradePanel = createDomElementWithChildren('div', [tradePanelHeader, tradePanelBody], panelClasses);
        document.getElementById(wrapperId).appendChild(tradePanel);
        return tradePanelBody;
    }

    var createTradePanelHeader = function(trade) {
        var profit = trade.profit;
        var isProfitableTrade = profit > 0;
        var profitText = (isProfitableTrade ? 'UP' : 'DOWN') + '[' + profit + ']';
        var entryTime = formDateTimeString(trade.entryTimestamp, timeFormat);
        var exitTime = formDateTimeString(trade.exitTimestamp, timeFormat);
        var tradeTimeRange = 'From [' + entryTime + '] To [' + exitTime + ']';
        var strategyId = trade.strategyId;
        var delimiter = ' --- ';
        var labelText = profitText + delimiter + tradeTimeRange + delimiter + strategyId;
        var h3 = createDomElementWithInnerHTML('h3', labelText, 'panel-title')
        var panelHeader = createDomElementWithChildren('div', [h3], 'panel-heading');
        return panelHeader;
    }

    var renderMainChart = function(ticks, chartsGroupWrapper) {
        var options = createMainChartOptions(ticks);
        var mainChartWrapper = createDomElement('div');
        chartsGroupWrapper.appendChild(mainChartWrapper);
        new ApexCharts(mainChartWrapper, options).render();
    }

    var createMainChartOptions = function(ticks) {
        var options = createCommonOptionsForMainChart();
        addLineIndicators(options, ticks);
        addPriceSeries(options, ticks);
        addSignals(options, ticks);
        addLevels(options, ticks);
        return options;
    }

    var createCommonOptionsForMainChart = function() {
        var chartTitle = 'MAIN';
        return {
            series: [],
            annotations: {
                yaxis: [],
                xaxis: []
            },
            chart: {
                height: 350,
                type: 'line',
                toolbar: {
                    show: false
                },
                events: {
                    markerClick: function(event, chartContext, { seriesIndex, dataPointIndex, config}) {
                        logBarParams(chartContext, seriesIndex, dataPointIndex);
                    }
                }
            },
            title: {
                text: chartTitle,
                align: 'center'
            },
            stroke: {
                width: []
            },
            xaxis: {
                tooltip: {
                    enabled: true,
                    offsetY: 40,
                    formatter: (timestamp) => timestamp
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
            },
            tooltip: {
                x: {
                    show: true,
                    format: 'yyyy-MM-dd HH:mm'
                }
            }
        };
    }

    var logBarParams = function(chartContext, seriesIndex, dataPointIndex) {
        var ohlc = chartContext.opts.series[seriesIndex].data[dataPointIndex].y;
        var openPrice = ohlc[0];
        var highPrice = ohlc[1];
        var lowPrice = ohlc[2];
        var closePrice = ohlc[3];
        var isBullishBar = closePrice > openPrice;
        var bodySize = (isBullishBar ? (closePrice - openPrice) : (openPrice - closePrice)).toFixed(2);
        var lowerShadowSize = (isBullishBar ? (openPrice - lowPrice) : (closePrice - lowPrice)).toFixed(2);
        var upperShadowSize = (isBullishBar ? (highPrice - closePrice) : (highPrice - openPrice)).toFixed(2);
        console.log('------------------- [' + dataPointIndex + ']');
        console.log('o: ' + openPrice + ' -- h: ' + highPrice + ' -- l: ' + lowPrice + ' -- c: ' + closePrice)
        console.log('body: ' + bodySize + ' -- lower shadow: ' + lowerShadowSize + ' -- upper shadow: ' + upperShadowSize);
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
                    color: '#fff',
                    background: colorHex
                }
            }
        }
    }

    var addLevels = function(options, ticks) {
        ticks.forEach(tick => {
            var levels = tick.mainChartLevels;
            if (levels) {
                levels.forEach(level => {
                    options.annotations.yaxis.push(createLevel(level));
                });
            }
        });
    }

    var createLevel = function(level) {
        var colorHex = '#1ab394';
        return {
            y: level.value,
            strokeDashArray: 0,
            borderColor: colorHex,
            label: {
                borderColor: colorHex,
                style: {
                    color: '#fff',
                    background: colorHex,
                },
                text: level.name,
                position: 'left',
                textAnchor: 'start',
            }
        }
    }

    var renderAdditionalCharts = function(ticks, chartsGroupWrapper) {
        var indicatorNameToSeriesMap = formAdditionalChartNumIndicatorNameToSeriesMap(ticks);
        additionalChartIndicatorTypes.forEach(indicatorType => {
            renderAdditionalChart(indicatorType, indicatorNameToSeriesMap, ticks, chartsGroupWrapper);
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

    var renderAdditionalChart = function(indicatorType, indicatorNameToSeriesMap, ticks, chartsGroupWrapper) {
        var indicatorTypeSeries = getSeriesForIndicatorType(indicatorType, indicatorNameToSeriesMap);
        if (Object.keys(indicatorTypeSeries).length === 0) {
            return;
        }
        var options = createCommonOptionsForAdditionalChart(indicatorType);
        addSeries(options, indicatorTypeSeries);
        addSignals(options, ticks);
        addXAxisTimeLabels(options, ticks);
        var additionalChartWrapper = createDomElement('div');
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

    var createCommonOptionsForAdditionalChart = function(chartTitle) {
        return {
            chart: {
                height: 250,
                type: 'line',
                toolbar: {
                    show: false
                }
            },
            series: [],
            annotations: {
                xaxis: []
            },
            dataLabels: {
                enabled: false
            },
            stroke: {
                curve: 'straight',
                width: 2
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
                    formatter: (timestamp) => formDateTimeString(timestamp, timeFormat)
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