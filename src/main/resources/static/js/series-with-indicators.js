var wrapperId = 'charts';
var priceChartType = 'line';
//var priceChartType = 'candlestick';
var seriesSegmentSize = 100;

var request = new XMLHttpRequest();
request.open('GET', 'http://localhost:8881/api/series');
request.onreadystatechange = () => { if (request.readyState === 4) processResponse() };
request.send();

function processResponse() {
    var series = JSON.parse(request.responseText);
    var chartRenderer = new ChartRenderer(wrapperId, priceChartType);
    chartRenderer.renderTickSeries(series, seriesSegmentSize);
}