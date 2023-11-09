import { AonElement } from "../../../components/AonElement.js";
import { AonIframe } from "../../../components/aon-iframe.js";
import { formatNumber, isEmptyObject, sortBy } from "../../../services/utils.js";
import { getCompanyCosts } from "../../../services/service.js";
import { PAYROLL_VIEWS } from "../PayrollEnums.js";
import { CompanyPieChart } from "./CompanyPieChart.js";
import { CONSTANT, CSS, MSG, TAG } from "../../../environments/environments.js";
import { AonDateUtils } from "../../utils/AonDateUtils.js";

let filterPeriod;
let chartCanva;

export class AonCompanyCostsCard extends AonElement {
  constructor(period) {
    super();
    filterPeriod = period;
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize() {
    this.id = this.id || PAYROLL_VIEWS.AON_COMPANY_COSTS_CARD;
  }

  async build() {
    await this.paintPieChar();
  }

  async paintPieChar() {
    const main = document.createElement(TAG.DIV);
    main.style.height = "100%";
    main.style.display = "flex";
    main.style.flexDirection = "column";
    main.style.gap = "1rem";
    main.style.justifyContent = "center";
    main.style.alignItems = "center";
    main.style.textAlign = "center";
    this.appendChild(main);

    //-----TITLE--------------
    let divTitle = this.createElement(TAG.DIV);
    divTitle.id = this.id + "titleDiv";
    divTitle.style.color  = "grey";
    divTitle.style.fontWeight ="500";
    main.appendChild(divTitle);

    //-----CHART--------------
    let canvasDiv = this.createElement(TAG.DIV);
    canvasDiv.id = this.id + "canvasDiv";
    canvasDiv.style.display = "flex";
    canvasDiv.style.justifyContent = "center";
    canvasDiv.style.maxWidth = "19rem";
    canvasDiv.style.maxHeight = "15rem";
    canvasDiv.style.height = "100%";
    canvasDiv.style.alignItems = "center";
    main.appendChild(canvasDiv);

    let canvasChart = this.createElement(TAG.CANVAS);
    canvasChart.id = this.id + "Chart";
    canvasDiv.appendChild(canvasChart);

    let emptyData = this.createElement(TAG.DIV);
    emptyData.id = this.id + "EmptyMessage";
    emptyData.innerHTML = "No existen nóminas para calcular costes";
    emptyData.style.fontWeight = "500";
    emptyData.style.display = 'none';
    canvasDiv.appendChild(emptyData);
  }

}

const paintCompanyCostPieChart = async () => {
  try {
    const data = await getData();

    let startDate = new Date();
    let endDate = new Date();
    let title = MSG.RESUME_COSTS;
    let workplaceText = "";
    let total = 0;
    
    if(data && data.length > 0 ){
      startDate = new Date(data[0].startDate);
      endDate   = new Date(data[0].endDate);
      
      const sumEnterpriseSs = data.reduce((sum,key)=> sum + (parseFloat(key.enterpriseSS) - parseFloat(key.bonuses)),0); 
      const sumEmployeeSs = data.reduce((sum,key)=>sum + (parseFloat(key.employeeSS) + parseFloat(key.otherDeductions)), 0); 
      const importIrpf = data.reduce((sum,key)=>sum + parseFloat(key.irpf), 0); 
      const totalLiquid = data.reduce((sum,key)=>sum + parseFloat(key.liquid), 0); 
      const totalSS = sumEnterpriseSs + sumEmployeeSs;
      total = sumEnterpriseSs + sumEmployeeSs + importIrpf + totalLiquid;

      const dataChart = {
        labels: [
          'SS Empresa',
          'SS Empleado',
          'Total SS',
          'Total IRPF',
          'Total Nominas'
        ],
        datasets: [{
          // label: 'My First Dataset',
          data: [sumEnterpriseSs, sumEmployeeSs, totalSS, importIrpf, totalLiquid],
          backgroundColor: [
            'rgba(0, 81, 198, 0.7)',
            'rgba(219, 68, 55, 0.7)',
            'rgba(0, 0, 0, 0.7)',
            'rgba(179, 179, 179, 0.7)',
            'rgba(94, 151, 246, 0.7)'
          ],
          hoverOffset: 4
        }]
      };

      const config = {
        type: 'pie',
        plugins: [ChartDataLabels],
        data: dataChart,
        options: {
          plugins: {
            legend: {
                display: false // This hides all text in the legend and also the labels.
            },
            datalabels: {
              display: 'auto',
              color: 'white',
              align: 'end',
              font: {
                weight: 'bold'
              },
              formatter: (value, ctx) => {
                const total = ctx.chart.getDatasetMeta(0).total;
                let percentage = (value * 100 / total).toFixed(2) + "%";
                return percentage;
              },
            }
          }
        }
      }

      let canvasChart = document.getElementById("aon-company-costs-cardChart");
      canvasChart.style.display = 'block';

      let emptyMessage = document.getElementById("aon-company-costs-cardEmptyMessage");
      emptyMessage.style.display = 'none';

      if(chartCanva){
        chartCanva.destroy();
      }
      
      chartCanva = new Chart(canvasChart, config);
    } else {
      let canvasChart = document.getElementById("aon-company-costs-cardChart");
      canvasChart.style.display = 'none';

      let emptyMessage = document.getElementById("aon-company-costs-cardEmptyMessage");
      emptyMessage.style.display = 'block';
    }

    let startDateText = AonDateUtils.getMonthYear(startDate),
    endDateText = AonDateUtils.getMonthYear(endDate);
    
    if(startDateText === endDateText){
      title = title + " "+ startDateText;
    } else {
      title = `${title} ${startDateText} - ${endDateText}`;
    }

    title = `${title}<br> ${workplaceText} <span style="color:black;font-weight:600;">${formatNumber(total, 2, "EUR")}<span>`;

    let divTitle = document.getElementById("aon-company-costs-cardtitleDiv");
    divTitle.innerHTML = title;
  } catch (error) {
    console.log(error);
  }
}

const getData = async () => {
  let data = [];
  try {
    let datos = await getCompanyCosts(filterPeriod);
    if (!isEmptyObject(datos)) {
      sortBy(datos, "employee", "asc").map((resp) => {
       const lettersType = getTypeSalaryText(
          resp.salaryType
        );
        const lettersHtml = `<div class="profile-letters ${lettersType.color}">${lettersType.typeReduce}</div>`;
        const obj = {
          ...resp,
          lettersHtml,
          name: resp.employee,
          salaryType: lettersType.type,
          workplaceName: resp.workplace,
        };
        data.push(obj);
      });
    }
  } catch (e) {
    console.log(e);
  }
  return data;
}

const getTypeSalaryText = (type) => {
  let obj = { color: "", type: "", typeReduce: "" };
  switch (type) {
    case "SALARY":
      obj.type = "NOMINA";
      break;
    case "EXTRA":
      obj.type = "EXTRA";
      obj.color = "in";
      break;
    case "SETTLE":
      obj.type = "FINIQUITO";
      obj.color = "fin";
      break;
    case "DELAY":
      obj.type = "ATRASOS";
      obj.color = "pause";
      break;
  }
  if (obj.type) obj.typeReduce = obj.type.toString().substr(0, 1);
  return obj;
}

export { paintCompanyCostPieChart };

window.customElements.define("aon-company-costs-card", AonCompanyCostsCard);
