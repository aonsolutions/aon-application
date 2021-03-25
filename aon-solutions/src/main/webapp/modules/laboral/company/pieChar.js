const getTotal = (dataArray) => {
  let total = 0;
  for (let i = 0; i < dataArray.length; i++) { total += dataArray[i][1];}
  return total;
}
const customTooltip = (name, value, total) =>  {
  return name + '<br/><b>' + value + ' (' + ((value/total) * 100).toFixed(2) + '%)</b>';
}


export const pieChar = (div, data, opts) => {
  google.charts.load("current", { packages: ["corechart"] });
  google.charts.setOnLoadCallback(drawChart);
  const total = getTotal(data);
  function drawChart() {

    //- Adding tooltip column  
  	for (let i = 0; i < data.length; i++) {data[i].push(customTooltip(data[i][0], data[i][1], total));}
    // --------Changing legend  
  	for (let i = 0; i < data.length; i++) {
      data[i][0] = data[i][0] + " " + data[i][1] +
      " (" + ((data[i][1] / total) * 100).toFixed(2) + "%)"; 
    }
    //------ Column names
    data.unshift(['Header', 'Body', 'Tooltip']);

    let table =  new google.visualization.arrayToDataTable(data);

    // let formatter = new google.visualization.NumberFormat({
    //   prefix: '€',
    //   fractionDigits: 2,
    //   decimalSymbol: ',',
    //   groupingSymbol: '.'
    // });
    // formatter.format(table, 1);
    // Setting role tooltip
    table.setColumnProperty(2, 'role', 'tooltip');
    table.setColumnProperty(2, 'html', true);
    let options = {
      title: "title",
      legend: {
      //   maxLines:2,
        // position: "bottom",
      //   alignment: 'start',
        // textStyle: {
        //   fontSize: 16,
        // },
      },
      tooltip: { 
        isHtml: true 
      }
    };
    if (opts) options = { ...options, ...opts };

    let chart = new google.visualization.PieChart(div);
    // google.visualization.events.addListener(chart, "select", (ev) => {
    //   let selectedItem = chart.getSelection()[0];
    //   if (selectedItem) {
    //     let topping = table.getValue(selectedItem.row, 0);
    //     console.log("The user selected " + topping);
    //   }
    // });
    chart.draw(table, options);
  }
};
