import { MSG } from "../../../../environments/environments";
import { waitEl } from "../../../../services/utils";

// #9e9e9e
export const charts = (div, data, opts) => new Promise(async(resolve) => {
    await waitEl("script[src*='jsapi']");
    const drawBasic = () => {
      const chart = new google.visualization.ComboChart(div);
      const table = google.visualization.arrayToDataTable([
        [
          "Days",
          MSG.HOURS,
          { role: 'style'},
          MSG.AVERAGE,
        ],
        ...data
      ]);

      const options = {
        theme: "material",
        legend: "none",
        lineWidth: 5,
        width: opts.width || 260,
        vAxis: {
          textStyle: {
            bold: true,
          },
          gridlines:{
            count:0
          },
          baselineColor: '#fff'
        },
        hAxis: {
          textStyle: {
            bold: true,
            fontSize: 11,
            fontName: "sans-serif",
          },
        },
        seriesType: "bars",
        series: { 
          1: {
            type: 'line',
            lineWidth: 2,
            color:"#808080",
            lineDashStyle: [3],
            labelInLegend: false,
            visibleInLegend: false
          }
        },
        // animation: {
        //   duration: 800,
        //   easing: 'in',
        //   startup: true
        // }
      };

      google.visualization.events.addListener(chart, 'ready', ()=> changeBorderRadius(div));
      google.visualization.events.addListener(chart, 'select', ()=> changeBorderRadius(div));
      google.visualization.events.addListener(chart, 'onmouseover', ()=> changeBorderRadius(div));
      google.visualization.events.addListener(chart, 'onmouseout', ()=> changeBorderRadius(div));
      google.visualization.events.addListener(chart, 'animationfinish', ()=> changeBorderRadius(div));

      chart.draw(table, options);
      resolve(chart);
    };

    google.charts.load("current", {packages: ["corechart", "bar"]});
    google.charts.setOnLoadCallback(drawBasic);
});


const changeBorderRadius = (div) =>  {
  const chartColumns = div.getElementsByTagName('rect');
  const lineAverage = div.querySelector('path');

  if(lineAverage) {
    lineAverage.style.transform = "scaleX(.93)";
  }
  
  Array.prototype.forEach.call(chartColumns, (column)=> {
    column.setAttribute('rx', 9);
    column.setAttribute('ry', 9);
  });
}
