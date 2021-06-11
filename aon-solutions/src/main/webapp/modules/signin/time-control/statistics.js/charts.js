export const charts = (div, data) => new Promise((resolve) => {
    const drawBasic = () => {
      
      let table = google.visualization.arrayToDataTable([
        [
          "Days",
          "Horas",
          "Average"
        ],
        ...data
      ]);

      const options = {
        theme: "material",
        legend: "none",
        vAxis: {
          textStyle: {
            bold: true,
          },
          gridlines:{
            color: '#fff' //lines white
          }
        },
        hAxis: {
          textStyle: {
            bold: true,
            fontSize: 11,
            fontName: "sans-serif",
          }
        },
        seriesType: "bars",
        series: { 
          1: {
            type: 'line',
            lineWidth: 2,
            color:"#808080",
            labelInLegend: false,
            visibleInLegend: false
          }
        },
      };

      const chart = new google.visualization.ComboChart(div);
      chart.draw(table, options);

      resolve(chart);
    };

  google.charts.load("current", {
    packages: ["corechart", "bar"],
    language: "es",
  });
  google.charts.setOnLoadCallback(drawBasic);
});

