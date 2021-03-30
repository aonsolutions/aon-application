import { formatNumber, isEmptyObject } from "../../../services/utils.js";

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

const changeSliceColor = (colors) =>{
  let slices = {};
  for (const key in colors) { slices[key] = { color: colors[key]}; }
  return slices;
} 

export const pieChar = (div, data, opts, callBackClick) => {
    return new Promise(resolve => {

   const drawChart = () => {
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
    let options = {
      theme:"material",
      height: 300,
      legend: 'none',
    };
    if (opts) {
      if(opts.slices && opts.slices.length > 0){ opts.slices = changeSliceColor(opts.slices); }
      options = { ...options, ...opts };
    }
    
    let chart = new google.visualization.PieChart(div);
    google.visualization.events.addListener(chart, 'select', (ev) => {
      let item = chart.getSelection()[0];
      if(item){
        let name = table.getValue(item.row, 0);
        let value = table.getValue(item.row, 1);
        callBackClick({name, value})
      }
    });
    chart.draw(table, options);
    resolve(chart);
  }

    google.charts.load("current", { packages: ["corechart"] });
    google.charts.setOnLoadCallback(drawChart);
  });
};

export const addLegend = (div, data, colors, fn) => {
  return new Promise(resolve=>{
    let id = 'pieLegend';
    let tableLegend = document.getElementById(id) || createElement('table');
    tableLegend.innerHTML = "";
    tableLegend.id = id;
    tableLegend.style.textAlign = "right";
    tableLegend.style.margin = "auto";
    tableLegend.style.width = "50%";
    tableLegend.style.color = "grey";
    tableLegend.style.fontSize ="14px";
    tableLegend.style.borderCollapse = "separate";
    tableLegend.style.borderSpacing = "0 4px";
    div.appendChild(tableLegend);
    let tbody = createElement('tbody');
    tableLegend.appendChild(tbody);
  
    for(let idx in data){
      let color = colors[idx];
      let name = data[idx][0];
      let value = data[idx][1];

      let tr = addTrTableLegend({name,value, color}, tbody);
      tr.addEventListener('click',(ev) => {
        fn({name, value});
      })
    }
    resolve(tableLegend);
  })
}


const createElement = (el) => {
  return document.createElement(el);
}

export const addTrTableLegend = (data, tbody, el) => {
  let tr = createElement('tr');
  let th = createElement('th');
  let td = createElement('td');
  th.innerHTML = data.name;
  td.innerHTML = data.value;
  td.style.fontWeight = 600;
  td.style.color = "grey";
  let tdColor = createElement('td');
  if(!isEmptyObject(data.color)){
    const {divColor, nameColor, valueColor} = data.color;
    if(divColor)   tdColor.appendChild(createStylePoint(divColor));
    if(nameColor)  th.style.color = nameColor;
    if(valueColor) td.style.color = valueColor;
  }

  tr.appendChild(tdColor);
  tr.appendChild(th);
  tr.appendChild(td);
  tbody.appendChild(tr);
  return tr;
}

const createStylePoint  = (color) => {
  let div = createElement("div");
  div.style.height = "10px";
  div.style.width = "10px";
  div.style.backgroundColor = color || "#bbb";
  div.style.borderRadius = "50%";
  div.style.display = "inline-block";
  return div;
}
