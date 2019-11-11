<!DOCTYPE html>
<html lang="en" >
<head>
  <meta charset="UTF-8">
  <title>amCharts V4:  Map (simple, step 4)</title>
  <#include "css/astyle.css">

</head>
<body>
<!-- partial:index.partial.html -->
<script src="https://www.amcharts.com/lib/4/core.js"></script>
<script src="https://www.amcharts.com/lib/4/maps.js"></script>
<script src="https://www.amcharts.com/lib/4/geodata/worldLow.js"></script>
<div id="chartdiv"></div>
<!-- partial -->
  <script>



// Create map instance
var chart = am4core.create("chartdiv", am4maps.MapChart);

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

</body>
</html>