var tradesOverviewWrapperId = 'trades-overview';
var allTradesVisualizationWrapperId = 'all-trades';
var tradeVisualizationPopupId = 'trade-visualization-popup';
var tradeVisualizationWrapperId = 'trade-visualization';
//var priceChartType = 'line';
var priceChartType = 'candlestick';
var chartRenderer = new TickSeriesChartRenderer(priceChartType);
var tradesStore = {};

var request = new XMLHttpRequest();
request.open('GET', 'http://localhost:8881/api/strategy-analysis-report');
request.onreadystatechange = () => { if (request.readyState === 4) processResponse() };
request.send();

function processResponse() {
    var report = JSON.parse(request.responseText);
    var trades = report.trades;
    initTradesStore(trades);
    drawReport(report);
    drawTradesTable(trades);
}

function initTradesStore(trades) {
    tradesStore = {};
    trades.forEach(trade => {
        var tradeId = formTradeId(trade);
        tradesStore[tradeId] = trade;
    });
}

function formTradeId(trade) {
    var delimiter = '-';
    return trade.strategyId + delimiter + trade.entryTimestamp + delimiter + trade.exitTimestamp;
}

function drawReport(report) {
    document.getElementById('strategy-id').innerHTML = report.strategyId;
    document.getElementById('total-profit').innerHTML = report.totalProfit;
    document.getElementById('avg-profit-per-trade').innerHTML = report.averageProfitPerTrade;
    document.getElementById('n-trades').innerHTML = report.trades.length;
    document.getElementById('n-up-trades').innerHTML = report.nprofitableTrades;
    document.getElementById('n-down-trades').innerHTML = report.nunprofitableTrades;
    document.getElementById('risk-reward-ratio').innerHTML = report.riskRewardRatio.toFixed(2);
}

function drawTradesTable(trades) {
    var tradesTable = createTradesTable(trades);
    document.getElementById(tradesOverviewWrapperId).appendChild(tradesTable);
    $(tradesTable).DataTable();
}

function createTradesTable(trades) {
    var table = createDomElement('table', 'table table-hover');
    createTradesTableBody(trades, table);
    createTradesTableHeader(table);
    return table;
}

function createTradesTableHeader(table) {
    var header = table.createTHead();
    var row = header.insertRow();
    row.insertCell().innerHTML = '<b>Entry Time</b>';
    row.insertCell().innerHTML = '<b>Exit Time</b>';
    row.insertCell().innerHTML = '<b>Result</b>';
    row.insertCell().innerHTML = '<b>Profit</b>';
    row.insertCell().innerHTML = '<b>Strategy Id</b>';
}

function createTradesTableBody(trades, table) {
    trades.forEach(trade => {
        var profit = trade.profit;
        var row = table.insertRow();
        row.onclick = createTradeRenderingFunction(trade);
        row.setAttribute('class',  profit > 0 ? 'success' : 'danger')
        row.insertCell().innerHTML = formDateTimeString(trade.entryTimestamp, 'yyyy-mm-dd HH:MM:ss');
        row.insertCell().innerHTML = formDateTimeString(trade.exitTimestamp, 'yyyy-mm-dd HH:MM:ss');
        row.insertCell().innerHTML = profit > 0 ? 'UP' : 'DOWN';
        row.insertCell().innerHTML = (profit > 0 ? '+' : '') + profit;
        row.insertCell().innerHTML = trade.strategyId;
    });
}

function createTradeRenderingFunction(trade) {
    var tradeId = formTradeId(trade);
    return function() {
        drawTrade(tradeId);
    };
}

function drawTrade(tradeId) {
    var trade = tradesStore[tradeId];
    chartRenderer.renderTrade(trade, tradeVisualizationWrapperId);
    showPopup(tradeVisualizationPopupId);
}

function drawAllTrades(button) {
    button.style.display = 'none';
    var trades = Object.values(tradesStore);
    chartRenderer.renderTrades(trades, allTradesVisualizationWrapperId);
}