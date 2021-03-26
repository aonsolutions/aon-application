import { formatNumber } from "../../../services/utils.js";

export const getTotal = (dataArray) => {
  let total = 0;
  for (let i = 0; i < dataArray.length; i++) { total += dataArray[i][1];}
  return total;
}
const customLenged = (data, total) => {
     for (let i = 0; i < data.length; i++) {
      let format = formatNumber(data[i][1],2);
      // let format = data[i][1];
      data[i][0] = `${data[i][0]} ${format} (${ ((data[i][1] / total) * 100).toFixed(1)}%)`;
    }
}
const customTooltip = (data, total) =>  {
  for (let i = 0; i < data.length; i++) {
    data[i].push( data[i][0] + '<br/><b>' + data[i][1] + ' (' + ((data[i][1]/total) * 100).toFixed(2) + '%)</b>');
  }
}


export const pieChar = (div, data, opts) => {
  function drawChart() {
    const total = getTotal(data);
    customLenged(data, total);
    // customTooltip(data, total);
    //  
    let table =  new google.visualization.DataTable({
      cols : [
        { id : 'name', label : 'Name', type : 'string' },
        { id : 'value', label : 'Value', type : 'number' }
      ]
    });

    table.addRows( data );

    let formatter = new google.visualization.NumberFormat({
      prefix: '€',
      fractionDigits: 2,
      decimalSymbol: ',',
      groupingSymbol: '.'
    });
    formatter.format(table, 1);
    // Setting role tooltip
    // table.setColumnProperty(2, 'role', 'tooltip');
    // table.setColumnProperty(2, 'html', true);
    let options = {
      theme:"material",
      chartArea: {     
        width: '80%', 
        height: '75%',
      },
      vAxis: {maxValue: 10},
      title: "",
      legend: {
          position: "bottom",
          textStyle: {
            fontSize: 10,
            bold:true
          },
      },

    };
    if (opts) options = { ...options, ...opts };
    
    let chart = new google.visualization.PieChart(div);
    google.visualization.events.addListener(chart, 'select', selectHadler(chart, table));
    chart.draw(table, options);
  }

  google.charts.load("current", { packages: ["corechart"] });
  google.charts.setOnLoadCallback(drawChart);
};

const selectHadler = (chart, table) => (ev) => {
  let item = chart.getSelection()[0];
  if(item){
    let name = table.getValue(item.row, 0);
    let value = table.getValue(item.row, 1);
    console.log({name, value});
  }
}
