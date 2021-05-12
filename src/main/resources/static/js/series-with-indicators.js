var wrapperId = 'charts';
//var priceChartType = 'line';
var priceChartType = 'candlestick';
var chartRenderer = new TickSeriesChartRenderer(priceChartType);
var seriesSegmentSize = 200;

var request = new XMLHttpRequest();
request.open('GET', 'http://localhost:8881/api/series');
request.onreadystatechange = () => { if (request.readyState === 4) processResponse() };
request.send();

function processResponse() {
    var series = JSON.parse(request.responseText);
    chartRenderer.renderTickSeries(series, seriesSegmentSize, wrapperId);
}