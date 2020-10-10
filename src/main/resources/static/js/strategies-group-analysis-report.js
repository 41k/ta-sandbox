var strategiesGroupReportWrapperId = 'strategies-group-report';
var tradesIntersectionsReportWrapperId = 'trades-intersections-report';
var tradesTimelineChartWrapperId = 'trades-timeline-chart';

var request = new XMLHttpRequest();
request.open('GET', 'http://localhost:8881/api/strategies-group-analysis-report');
request.onreadystatechange = () => { if (request.readyState === 4) processResponse() };
request.send();

function processResponse() {
    var strategiesGroupAnalysisReport = JSON.parse(request.responseText);
    drawMainReport(strategiesGroupAnalysisReport);
    drawTradesIntersectionsReport(strategiesGroupAnalysisReport);
    drawTradesTimelineChart(strategiesGroupAnalysisReport);
}

function drawMainReport(strategiesGroupAnalysisReport) {
    var reportTable = createMainReportTable(strategiesGroupAnalysisReport);
    document.getElementById(strategiesGroupReportWrapperId).appendChild(reportTable);
}

function createMainReportTable(strategiesGroupAnalysisReport) {
    var table = createDomElement('table', 'table table-hover');
    createMainReportTableBody(strategiesGroupAnalysisReport, table);
    createMainReportTableHeader(table);
    return table;
}

function createMainReportTableHeader(table) {
    var header = table.createTHead();
    var row = header.insertRow();
    row.insertCell().innerHTML = '<b>Strategy Id</b>';
    row.insertCell().innerHTML = '<b>Total profit</b>';
    row.insertCell().innerHTML = '<b>N trades</b>';
    row.insertCell().innerHTML = '<b>N UPs</b>';
    row.insertCell().innerHTML = '<b>N DOWNs</b>';
    row.insertCell().innerHTML = '<b>Risk/Reward ratio</b>';
}

function createMainReportTableBody(strategiesGroupAnalysisReport, table) {
    var strategiesReports = strategiesGroupAnalysisReport.strategiesReports;
    strategiesReports.forEach(strategyReport => {
        var row = table.insertRow();
        row.insertCell().innerHTML = strategyReport.strategyId;
        row.insertCell().innerHTML = strategyReport.totalProfit;
        row.insertCell().innerHTML = strategyReport.trades.length;
        row.insertCell().innerHTML = strategyReport.nprofitableTrades;
        row.insertCell().innerHTML = strategyReport.nunprofitableTrades;
        row.insertCell().innerHTML = strategyReport.riskRewardRatio.toFixed(2);
    });
    var totalRow = table.insertRow();
    totalRow.setAttribute('class', 'success');
    totalRow.insertCell().innerHTML = '<b>Total:</b>';
    totalRow.insertCell().innerHTML = strategiesGroupAnalysisReport.totalProfit;
    totalRow.insertCell().innerHTML = strategiesGroupAnalysisReport.ntrades;
    totalRow.insertCell().innerHTML = strategiesGroupAnalysisReport.nprofitableTrades;
    totalRow.insertCell().innerHTML = strategiesGroupAnalysisReport.nunprofitableTrades;
    totalRow.insertCell().innerHTML = strategiesGroupAnalysisReport.riskRewardRatio.toFixed(2);
}

function drawTradesIntersectionsReport(strategiesGroupAnalysisReport) {
    var tradesIntersectionsReport = strategiesGroupAnalysisReport.tradesIntersectionsReport;
    var reportTable = createTradesIntersectionsReportTable(tradesIntersectionsReport);
    document.getElementById(tradesIntersectionsReportWrapperId).appendChild(reportTable);
}

function createTradesIntersectionsReportTable(tradesIntersectionsReport) {
    var table = createDomElement('table', 'table table-hover');
    createTradesIntersectionsReportTableBody(tradesIntersectionsReport, table);
    createTradesIntersectionsReportTableHeader(table);
    return table;
}

function createTradesIntersectionsReportTableHeader(table) {
    var header = table.createTHead();
    var row = header.insertRow();
    row.insertCell().innerHTML = '<b>Strategies Pair</b>';
    row.insertCell().innerHTML = '<b>Trades Intersections Count</b>';
}

function createTradesIntersectionsReportTableBody(tradesIntersectionsReport, table) {
    for (const entry of Object.entries(tradesIntersectionsReport)) {
        var row = table.insertRow();
        row.insertCell().innerHTML = entry[0];
        row.insertCell().innerHTML = entry[1].length;
    }
}

function drawTradesTimelineChart(strategiesGroupAnalysisReport) {
    var options = tradesTimelineChartOptions(strategiesGroupAnalysisReport);
    var chartOverflowXWrapper = createChartOverflowXWrapper(strategiesGroupAnalysisReport);
    document.getElementById(tradesTimelineChartWrapperId).appendChild(chartOverflowXWrapper);
    new ApexCharts(chartOverflowXWrapper, options).render();
}

function tradesTimelineChartOptions(strategiesGroupAnalysisReport) {
    var options = createTimelineChartCommonOptions();
    addTrades(strategiesGroupAnalysisReport, options);
    addIntersectionPoints(strategiesGroupAnalysisReport, options);
    return options;
}

function createTimelineChartCommonOptions() {
    return {
        series: [
            {
                data: []
            }
        ],
        annotations: {
            xaxis: []
        },
        chart: {
            height: 350,
            type: 'rangeBar',
            toolbar: {
                show: false
            }
        },
        plotOptions: {
            bar: {
                horizontal: true,
                distributed: true
            }
        },
        xaxis: {
            type: 'datetime'
        }
    };
}

function addTrades(strategiesGroupAnalysisReport, options) {
    var strategiesReports = strategiesGroupAnalysisReport.strategiesReports;
    var xAxisMinValue = Number.MAX_SAFE_INTEGER;
    strategiesReports.forEach(strategyReport => {
        var trades = strategyReport.trades;
        addTradesToTimelineChart(trades, options);
        var firstTradeEntryTimestamp = trades[0].entryTimestamp;
        if (firstTradeEntryTimestamp < xAxisMinValue) {
            xAxisMinValue = firstTradeEntryTimestamp;
        }
    });
    options.xaxis.min = xAxisMinValue;
}

function addTradesToTimelineChart(trades, options) {
    trades.forEach(trade => {
        var tradeTimelineItem = {
            x: trade.strategyId,
            y: [
                trade.entryTimestamp,
                trade.exitTimestamp
            ],
            fillColor: trade.profit > 0 ? '#1ab394' : '#ed5565'
        };
        options.series[0].data.push(tradeTimelineItem);
    });
}

function addIntersectionPoints(strategiesGroupAnalysisReport, options) {
    var intersectionsReport = strategiesGroupAnalysisReport.tradesIntersectionsReport;
    Object.values(intersectionsReport).forEach(reportEntry => {
        reportEntry.forEach(intersectionTimestamp => {
            options.annotations.xaxis.push({
                x: intersectionTimestamp,
                strokeDashArray: 0,
                borderColor: '#775dd0',
                yAxisIndex: 0,
                label: {
                    show: false
                }
            });
        });
    });
}

function createChartOverflowXWrapper(strategiesGroupAnalysisReport) {
    var baseTimelineWidthInMillis = 7200000;
    var baseTimelineWidthInPixels = 1000;
    var reportWidthInMillis = strategiesGroupAnalysisReport.endTimestamp - strategiesGroupAnalysisReport.beginTimestamp;
    var reportWidthInPixels = (reportWidthInMillis / baseTimelineWidthInMillis) * baseTimelineWidthInPixels;
    var wrapper = createDomElement('div');
    wrapper.style.width = Math.round(reportWidthInPixels) + 'px';
    return wrapper;
}