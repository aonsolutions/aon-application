import { AonElement } from "../../components/AonElement.js";
import { TAG } from "../../environments/environments.js";
import { getChargePayments } from "../../services/invoiceService.js";

export class AonDashboardChargePayments extends AonElement {
  filter;
  cypData;
  stackedChart;

  set id(id) {
    this.setAttribute("id", id);
  }

  get id() {
    return this.getAttribute("id");
  }

  constructor(period) {
    super();
    this.id = this.id || "aonDashboardChargePayments";
    this.applicationEl = document.querySelector("#cypContent");
    this.filter = {};
    this.filter.period = period;
    this.style.width = "100%";
    this.style.display = "flex";
    this.style.height = "100%";
  }

  connectedCallback() {
    this.initialize();
    this.build();
  }

  initialize() {}

  async build() {
    this.draw();
  }

  async draw() {
    this.innerHTML = "";

    let contentDiv = this.createElement(TAG.DIV);
    contentDiv.style.width = "100%";
    contentDiv.style.display = "flex";
    contentDiv.style.gap = ".5rem";
    contentDiv.style.flexDirection = "column";
    this.appendChild(contentDiv);

    let titleDiv = this.createElement(TAG.SPAN);
    titleDiv.innerHTML = 'Resumen: ' + this.getTitlePeriod(this.filter.period);
    titleDiv.style.color = "grey";
    titleDiv.style.fontWeight = "500";
    titleDiv.style.textAlign = "center";
    contentDiv.appendChild(titleDiv);

    let canvasDiv = this.createElement(TAG.DIV);
    canvasDiv.id = "pygCardCanvasDiv";
    canvasDiv.style.width = "100%";
    canvasDiv.style.height = "100%";
    contentDiv.appendChild(canvasDiv);

    let canvas = this.createElement(TAG.CANVAS);
    canvas.id = "pygCardCanvas";
    canvasDiv.appendChild(canvas);

    this.cypData = await this.getDataDB();

    this.drawBarLineChart(canvas, this.cypData);
  }

  drawBarLineChart(canvas, cypData){
    let dataChart = {
      labels: [
        "Anteriores", 
        "Periodo", 
        "Acumulado"
      ],
      datasets: [
        {
          label: "Cobros (Pedientes)",
          backgroundColor: "rgba(51, 168, 255, 0.7)",
          data: [cypData.charges.previous.pending, null, null]
        },
        {
          label: "Cobros (Devuelto)",
          backgroundColor: "rgba(10, 150, 255, 0.9)",
          data: [cypData.charges.previous.returns, null, null]
        },
        {
          label: "Pagos (Pedientes)",
          backgroundColor: "rgb(255, 116, 37, 0.7)",
          data: [cypData.payments.previous.pending, null, null]
        },
        {
          label: "Pagos (Devuelto)",
          backgroundColor: "rgb(255, 99, 9, 0.9)",
          data: [cypData.payments.previous.returns, null, null]
        },

        {
          label: "Cobros (Pedientes)",
          backgroundColor: "rgba(51, 168, 255, 0.7)",
          data: [null, cypData.charges.period.pending, null]
        },
        {
          label: "Cobros (Devuelto)",
          backgroundColor: "rgba(10, 150, 255, 0.9)",
          data: [null, cypData.charges.period.returns, null]
        },
        {
          label: "Pagos (Pedientes)",
          backgroundColor: "rgb(255, 116, 37, 0.7)",
          data: [null, cypData.payments.period.pending, null]
        },
        {
          label: "Pagos (Devuelto)",
          backgroundColor: "rgb(255, 99, 9, 0.9)",
          data: [null, cypData.payments.period.returns, null]
        },

        {
          label: "Cobros (Pedientes)",
          backgroundColor: "rgba(51, 168, 255, 0.7)",
          data: [null, null, cypData.charges.accumulate.pending]
        },
        {
          label: "Cobros (Devuelto)",
          backgroundColor: "rgba(10, 150, 255, 0.9)",
          data: [null, null, cypData.charges.accumulate.returns]
        },
        {
          label: "Pagos (Pedientes)",
          backgroundColor: "rgb(255, 116, 37, 0.7)",
          data: [null, null, cypData.payments.accumulate.pending]
        },
        {
          label: "Pagos (Devuelto)",
          backgroundColor: "rgb(255, 99, 9, 0.9)",
          data: [null, null, cypData.payments.accumulate.returns]
        },

        {
          label: "CashFlow",
          backgroundColor: "rgb(153, 0, 153, 0.9)",
          type: 'line',
          data: [cypData.cashFlow.previous, cypData.cashFlow.period, cypData.cashFlow.accumulate]
        }
      ]
    };
    
    const config = {
      type: "bar",
      data: dataChart,
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          x: {
            stacked: true,
          },
          y: {
            stacked: true,
          },
        },
        plugins: {
          legend: {
            display: false, // This hides all text in the legend and also the labels.
          },
        },
      },
    };

    if (this.stackedChart != undefined) {
      this.stackedChart.destroy();
    }

    this.stackedChart = new Chart(canvas, config);
  }
  
  async getDataDB(){
    try {
      let chargePayments = await getChargePayments({period: this.filter.period});
      return chargePayments;
    } catch(e){
      console.log(e);
    }
  }

  getTitlePeriod(period){
     switch (period) {
      case 'current_month':
        return 'Mes actual';
      case 'next_month':
        return 'Hasta próximo mes';
      case 'next_3month':
        return 'Hasta próximos 3 meses';
      case 'next_6month':
        return 'Hasta próximos 6 meses';
      default:
        return 'Año actual';
    }
  }
}

window.customElements.define(
  "aon-dashboard-charge-payments",
  AonDashboardChargePayments
);
