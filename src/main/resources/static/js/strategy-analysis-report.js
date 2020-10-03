var wrapperId = 'trades';
//var priceChartType = 'line';
var priceChartType = 'candlestick';

var request = new XMLHttpRequest();
request.open('GET', 'http://localhost:8881/api/strategy/analysis-report');
request.onreadystatechange = () => { if (request.readyState === 4) processResponse() };
request.send();

function processResponse() {
    var report = JSON.parse(request.responseText);
    drawReport(report);

    var chartRenderer = new ChartRenderer(wrapperId, priceChartType);
    var trades = report.trades;
    chartRenderer.renderTrades(trades);
}

function drawReport(report) {
    document.getElementById('total-profit').textContent = report.totalProfit;
    document.getElementById('n-trades').textContent = report.trades.length;
    document.getElementById('n-up-trades').textContent = report.nprofitableTrades;
    document.getElementById('n-down-trades').textContent = report.nunprofitableTrades;
    document.getElementById('rr').textContent = report.riskRewardRatio;
    document.getElementById('list-of-n-significant-ups').textContent = drawArray(report.listOfNSignificantUps);
    document.getElementById('list-of-n-significant-downs').textContent = drawArray(report.listOfNSignificantDowns);
}

function drawArray(array) {
    return '[' + array.join('  |  ') + ']';
}