<!DOCTYPE html>
<html lang="en" >
<head>
  <meta charset="UTF-8">
  <title>Link statistics</title>
  <#include "css/style_playforms.css">
</head>
<body style=" color:White;">
<!-- partial:index.partial.html -->
<script src="https://www.amcharts.com/lib/4/core.js"></script>
<script src="https://www.amcharts.com/lib/4/maps.js"></script>
<script src="https://www.amcharts.com/lib/4/geodata/worldLow.js"></script>
<script src="https://www.amcharts.com/lib/4/charts.js"></script>

<div style="text-align:center;">
  <p style="font-size:120px;">
    statistics of ${url}
  </p>
</div>


<div align="center">
<div class="row" style="margin-top: 50px;">
  <div class="column">
    <h3>Browsers</h3>
    <div id="chartdivBrowsers"></div>
  </div>
  <div class="column">
    <h3>Platforms</h3>
    <div id="chartdivPlatforms"></div>
  </div>
</div>

<div class="row" style="margin-top: 70px; margin-bottom: 150px;">
    <h3>Countries</h3>
    <div id="chartdivMap"></div>
</div>

</div>



<!-- partial -->
  <script>
// Create map instance
var chart = am4core.create("chartdivMap", am4maps.MapChart);

// Set map definition
chart.geodata = am4geodata_worldLow;

// Set projection
chart.projection = new am4maps.projections.Miller();

// Create map polygon series
var polygonSeries = chart.series.push(new am4maps.MapPolygonSeries());

// Make map load polygon (like country names) data from GeoJSON
polygonSeries.useGeodata = true;

// Configure series
var polygonTemplate = polygonSeries.mapPolygons.template;
polygonTemplate.tooltipText = "{name} : {value}";
polygonTemplate.fill = "gray";

// Create hover state and set alternative fill color
var hs = polygonTemplate.states.create("hover");
hs.properties.fill = "black";

// Remove Antarctica
polygonSeries.exclude = ["AQ"];

// Add some data
polygonSeries.data = ${mapdata};

// Bind "fill" property to "fill" key in data
polygonTemplate.propertyFields.fill = "fill";


</script>

<script>

  // Create chart instance
  var chart = am4core.create("chartdivBrowsers", am4charts.PieChart);

  // Add data
  chart.data = ${browsersdata};

  // Add and configure Series
  var pieSeries = chart.series.push(new am4charts.PieSeries());
  pieSeries.dataFields.value = "counter";
  pieSeries.dataFields.category = "name";

  pieSeries.labels.template.fill = "white";
  pieSeries.ticks.template.stroke="white";
  pieSeries.ticks.template.strokeWidth=1.5;

</script>


<script>

  // Create chart instance
  var chart = am4core.create("chartdivPlatforms", am4charts.PieChart);

  // Add data
  chart.data = ${platformdata};

  // Add and configure Series
  var pieSeries = chart.series.push(new am4charts.PieSeries());
  pieSeries.dataFields.value = "counter";
  pieSeries.dataFields.category = "name";

  pieSeries.labels.template.fill = "white";
  pieSeries.ticks.template.stroke="white";
  pieSeries.ticks.template.strokeWidth=1.5;

</script>

</body>
</html>