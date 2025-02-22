import { CSS, MSG, TAG } from "../../../environments/environments.js";
import { formatNumber, isEmptyObject } from "../../../services/utils.js";
import { PAYROLL_VIEWS } from "../PayrollEnums.js";

// const customLenged = (data, total) => {
//      for (let i = 0; i < data.length; i++) {
//       let format = formatNumber(data[i][1],2);
//       data[i][0] = `${data[i][0]} ${format} (${ ((data[i][1] / total) * 100).toFixed(1)}%)`;
//     }
// }
// const customTooltip = (data, total) =>  {
//   for (let i = 0; i < data.length; i++) {
//     data[i].push( data[i][0] + '<br/><b>' + data[i][1] + ' (' + ((data[i][1]/total) * 100).toFixed(2) + '%)</b>');
//   }
// }

const changeSliceColor = (colors) => {
  let slices = {};
  for (const key in colors) {
    slices[key] = { color: colors[key] };
  }
  return slices;
};

const createStylePoint = (document, color) => {
  let div = document.createElement("div");
  div.style.height = "10px";
  div.style.width = "10px";
  div.style.backgroundColor = color || "#bbb";
  div.style.borderRadius = "50%";
  div.style.display = "inline-block";
  return div;
};

// export const getTotal = (dataArray) => {
//   let total = 0;
//   for (let i = 0; i < dataArray.length; i++) {
//     total += dataArray[i][1];
//   }
//   return total;
// };

const paintPieChart = async (data, parent, aonIframe, leyend) => {
  const sumEnterpriseSs = data.reduce((sum,key)=> sum + (parseFloat(key.enterpriseSS) - parseFloat(key.bonuses)),0); 
  const sumEmployeeSs = data.reduce((sum,key)=>sum + (parseFloat(key.employeeSS) + parseFloat(key.otherDeductions)), 0); 
  const importIrpf = data.reduce((sum,key)=>sum + parseFloat(key.irpf), 0); 
  const totalLiquid = data.reduce((sum,key)=>sum + parseFloat(key.liquid), 0); 
  const totalSS = sumEnterpriseSs + sumEmployeeSs;
  const total = sumEnterpriseSs + sumEmployeeSs + importIrpf + totalLiquid;

  let fields = [
    ['SS Empresa', sumEnterpriseSs],
    ['SS Empleado', sumEmployeeSs],
    ['Total IRPF', importIrpf],
    ['Total Nominas', totalLiquid]
  ];

  const colors = ['#0051C6','#db4437', '#B3B3B3', '#5e97f6'];

  await pieChar(parent, aonIframe, fields, { slices: colors }, leyend, (evClick)=>{
    console.log(evClick);
  });


  fields.splice(2, 0, ["Total SS", totalSS]);
  colors.splice(2, 0, "none");
  
  let newColor = colors.map(color=> ({ divColor: color, nameColor: 'grey', valueColor: 'grey'}));

  newColor[2].valueColor = newColor[3].valueColor =  newColor[4].valueColor = "black";
  
  let newData = fields.map(el=> [el[0], formatNumber(el[1], 2, "EUR")]);

  if(leyend)
    await addLegend(parent, aonIframe, newData, newColor, (evClick)=>console.log(evClick));

  const workplaceEl = document.querySelector('#workplace');

  const workplaceText = workplaceEl && workplaceEl.querySelector('LI') ? workplaceEl.querySelector('LI').textContent+": " : "";

  return {
    workplaceText,
    total
  }

};

const createButton = (parent, aonIframe, aonCompanyCostsList)=>{
   //----CREATE BUTTON NOMINAS 

   let button = aonIframe.getDocument().createElement(TAG.BUTTON);
   button.className = CSS.AON_BUTTON;
   button.id = `${aonCompanyCostsList.id}Nomina`;
   button.innerHTML = MSG.VIEW_PAYROLLS;
   button.style.marginTop = "10px";
   parent.appendChild(button);
 
   button.onclick = () => {
     aonCompanyCostsList.getApplicationParent().showView(PAYROLL_VIEWS.AON_PAYROLL_LIST);
   }
}



const pieChar = (parent, aonIframe, fields, opts, leyend, callBackClick) => {

  const google = aonIframe.getGoogle();

  return new Promise((resolve) => {
    const drawChart = () => {
      let table = new google.visualization.DataTable({
        cols: [
          { id: "name", label: "Name", type: "string" },
          { id: "value", label: "Value", type: "number" },
        ],
      });

      table.addRows(fields);

      let formatter = new google.visualization.NumberFormat({
        prefix: "€",
        fractionDigits: 2,
        decimalSymbol: ",",
        groupingSymbol: ".",
      });
      formatter.format(table, 1);
      let options = {
        theme: "material",
        height: leyend ? 500 : 250,
        width: leyend ? 500 : 290,
        legend: "none"
      };
      if (opts) {
        if (opts.slices && opts.slices.length > 0) {
          opts.slices = changeSliceColor(opts.slices);
        }
        options = { ...options, ...opts };
      }

      let chart = new google.visualization.PieChart(parent);
      google.visualization.events.addListener(chart, "select", (ev) => {
        let item = chart.getSelection()[0];
        if (item) {
          let name = table.getValue(item.row, 0);
          let value = table.getValue(item.row, 1);
          callBackClick({ name, value });
        }
      });
      chart.draw(table, options);
      resolve(chart);
    };

    google.charts.load("current", { packages: ["corechart"] });
    google.charts.setOnLoadCallback(drawChart);
  });
};


const addLegend = (parent, aonIframe, data, colors, fn) => {
  return new Promise((resolve) => {
    const document = aonIframe.getDocument();
    const id = "pieLegend";

    let tableLegend = document.getElementById(id) || document.createElement("table");
    tableLegend.innerHTML = "";
    tableLegend.id = id;
    tableLegend.style.textAlign = "right";
    tableLegend.style.margin = "auto";
    tableLegend.style.color = "grey";
    tableLegend.style.fontSize = "14px";
    tableLegend.style.borderCollapse = "separate";
    tableLegend.style.borderSpacing = "1em .5em";
    parent.appendChild(tableLegend);
    let tbody = document.createElement("tbody");
    tableLegend.appendChild(tbody);

    for (let idx in data) {
      let name = data[idx][0];
      let value = data[idx][1];
      if(name && value){
        let color = colors[idx];
        let tr = addTrTableLegend({ name, value, color }, tbody, document);
        tr.addEventListener("click", () => 
          fn({ name, value })
        );
      }  
    }
    resolve(tableLegend);
  });
};

const addTrTableLegend = (data, tbody, document) => {
  let tr = document.createElement("tr");
  let th = document.createElement("th");
  let td = document.createElement("td");
  th.innerHTML = data.name + ":";
  td.innerHTML = data.value;
  td.style.fontWeight = 600;
  td.style.color = "grey";
  let tdColor = document.createElement("td");
  if (!isEmptyObject(data.color)) {
    const { divColor, nameColor, valueColor } = data.color;
    if (divColor) tdColor.appendChild(createStylePoint(document, divColor));
    if (nameColor) th.style.color = nameColor;
    if (valueColor) td.style.color = valueColor;
  }

  tr.appendChild(tdColor);
  tr.appendChild(th);
  tr.appendChild(td);

  tbody.appendChild(tr);

  return tr;
};



export const CompanyPieChart = {
  paintPieChart,
  createButton
}