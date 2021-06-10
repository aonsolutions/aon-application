export const getRandomColor = () => {
  // let letters = "0123456789ABCDEF".split("");
  // let color = "#";
  // for (let i = 0; i < 6; i++) {
  //   color += letters[Math.floor(Math.random() * 16)];
  // }
  return "#76A7FA";
  // return color;
};

export const charts = (div, data) => {
  return new Promise((resolve) => {
    const drawBasic = () => {
      
      let table = google.visualization.arrayToDataTable([
        [
          "Days",
          "Horas",
          { role: "style" },
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
        },
        hAxis: {
          textStyle: {
            bold: true,
            fontSize: 11,
            fontName: "sans-serif",
          },
        },
        seriesType: "bars",
        // series: { 
        //   0: { 
        //     type: "line", 
        //     color: "#002469" 
        //   } 
        // },
        // trendlines: {
        //   1: {
        //     color: 'purple',
        //     lineWidth: 10,
        //     opacity: 0.2,
        //     type: 'exponential'
        //   }
        // }
        
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
};
