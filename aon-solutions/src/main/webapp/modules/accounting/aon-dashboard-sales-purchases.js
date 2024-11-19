import { AonElement } from "../../components/AonElement.js";
import { TAG } from "../../environments/environments.js";
import { isEmptyObject } from "../../services/utils.js";
import { getAccounting, getPeriods } from "../../services/accountingService.js";
import * as UTILS from "./AccountingUtils.js";
import { AonDateUtils } from "../utils/AonDateUtils.js";
import * as LS from "../../services/localStorageService.js";
import {
  getChartInvoices,
  getChartInvoicesPeriod,
} from "../../services/invoiceService.js";

export class AonDashboardSalesPurchases extends AonElement {
  PERIODS;
  invoices;
  filter;
  params = {
    domain: localStorage.getItem("aon_domain_id"),
    domainName: localStorage.getItem("aon_domain_name"),
    user: "",
    level: 5,
    byMonth: true,
  };

  purchases;
  sales;
  expenses;
  tickets;
  profits;
  chart;

  ACCOUNTS;
  filter;
  selectedPeriod;

  selectedElement;
  chartData;
  accounts;
  arrIncome;
  incomev;
  arrPurchases;
  purchases;
  arrOutgoings;
  outgoings;
  raw;
  arrAmortizations;
  amortizations;
  arrOtherOutgoings;
  otherOutgoings;
  liquid;
  data1;
  data2;
  chart;
  options;
  selAccounts;
  stackedChart;
  pieChart;

  set id(id) {
    this.setAttribute("id", id);
  }

  get id() {
    return this.getAttribute("id");
  }

  getFilter() {
    return this.filter;
  }

  constructor(period, year) {
    super();
    this.id = this.id || "aonDashboardSalesPurchases";
    this.applicationEl = document.querySelector("#vygContent");
    this.filter = {};
    this.filter.show = period;
    this.filter.year = year || new Date().getFullYear();
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
    if (!this.params.domain || !this.params.domainName) {
      try {
        let company = JSON.parse(localStorage.getItem("company"));
        this.params.domain = company.id;

        this.params.domainName = company.domain;
      } catch (error) {
        console.log(error);
      }
    }

    this.PERIODS = await getChartInvoicesPeriod(this.params).catch((error) => {
      console.log(error);
      return [];
    });

    this.draw();
  }

  formatDate(date) {
    const day = String(date.getDate()).padStart(2, "0"); // Obtener el día y asegurar que tenga 2 dígitos
    const month = String(date.getMonth() + 1).padStart(2, "0"); // Obtener el mes (añadir 1 porque los meses en JS son de 0 a 11)
    const year = date.getFullYear(); // Obtener el año

    return `${day}/${month}/${year}`; // Formatear la fecha
  }

  async draw() {
    this.innerHTML = "";

    if (this.PERIODS && this.PERIODS.length === 0) {
      let emptyMessage = this.createElement(TAG.DIV);
      emptyMessage.innerHTML = "No existe periodos disponibles";
      emptyMessage.style.fontWeight = "bold";
      emptyMessage.style.display = "flex";
      emptyMessage.style.justifyContent = "center";
      emptyMessage.style.alignItems = "center";
      emptyMessage.style.width = "100%";

      this.style.height = "100%";
      this.appendChild(emptyMessage);
    } else {
      let contentDiv = this.createElement(TAG.DIV);
      contentDiv.style.width = "100%";
      contentDiv.style.display = "flex";
      contentDiv.style.gap = ".5rem";
      contentDiv.style.flexDirection = "column";
      this.appendChild(contentDiv);

      let titleDiv = this.createElement(TAG.DIV);
      titleDiv.style.width = "100%";
      titleDiv.style.display = "flex";
      titleDiv.style.gap = ".5rem";
      titleDiv.style.alignItems = "center";
      titleDiv.style.justifyContent = "center";
      contentDiv.appendChild(titleDiv);

      let titleSpan = this.createElement(TAG.SPAN);
      titleSpan.innerHTML = "Resumen: " + this.getTitlePeriod(this.filter.show);
      titleSpan.className = "graphicsDashBoardSummary";
      titleDiv.appendChild(titleSpan);

      let yearelect = this.createElement("select");
      yearelect.id = "vyGyearSelect";
      yearelect.title = "Año";
      yearelect.className = "graphicsDashBoardYear";

      for (let year = this.PERIODS.min; year <= this.PERIODS.max; year++) {
        let optYear = this.createElement("option");
        optYear.value = JSON.stringify(year);
        optYear.innerHTML = year;
        if (this.filter.year == year) {
          optYear.selected = true;
        }
        yearelect.appendChild(optYear);
      }

      yearelect.addEventListener("change", () => {
        this.filter.year = yearelect.value;
        this.draw();
      });
      titleDiv.appendChild(yearelect);

      let canvasDiv = this.createElement(TAG.DIV);
      canvasDiv.id = "vygCardCanvasDiv";
      canvasDiv.style.width = "100%";
      canvasDiv.style.height = "100%";
      contentDiv.appendChild(canvasDiv);

      let canvas = this.createElement(TAG.CANVAS);
      canvas.id = "vygCardCanvas";
      canvasDiv.appendChild(canvas);

      this.filter.from = this.formatDate(new Date(this.filter.year, 0, 1));
      this.filter.to = this.formatDate(new Date(this.filter.year, 11, 31));
      this.invoices = await getChartInvoices(this.filter);

      if (this.invoices) {
        if (this.filter.show === "yearly") {
          this.calculateYearlyData(this.invoices);
          this.drawBarChart(canvas);
        } else {
          this.drawBarLineChart(canvas, this.invoices);
        }
      }
    }
  }

  calculateYearlyData(invoicesData) {
    this.purchases = invoicesData
      .filter((item) => item.type === 0)
      .reduce((acc, item) => acc + item.total, 0);
    this.sales = invoicesData
      .filter((item) => item.type === 1)
      .reduce((acc, item) => acc + item.total, 0);
    this.expenses = invoicesData
      .filter((item) => item.type === 2)
      .reduce((acc, item) => acc + item.total, 0);
    this.tickets = invoicesData
      .filter((item) => item.type === 3)
      .reduce((acc, item) => acc + item.total, 0);
    this.profits = this.sales - this.purchases - this.expenses - this.tickets;
  }

  drawBarChart(canvas) {
    let colorGrid = LS.isDarkTheme() ? "#ffffff" : "#bdbdbd";

    const dataChart = {
      labels: [this.filter.year],
      datasets: [
        {
          label: "Ventas",
          backgroundColor: "rgb(51, 102, 204, 0.7)",
          data: [this.sales],
          stack: "Stack 0",
        },
        {
          label: "Compras",
          backgroundColor: "rgb(220, 57, 18, 0.7)",
          data: [this.purchases],
          stack: "Stack 1",
        },
        {
          label: "Gastos.",
          backgroundColor: "rgb(255, 153, 0, 0.7)",
          data: [this.expenses],
          stack: "Stack 1",
        },
        {
          label: "Tickets",
          backgroundColor: "rgb(16, 150, 24, 0.7)",
          data: [this.tickets],
          stack: "Stack 1",
        },
        {
          label: "Beneficios",
          backgroundColor: "rgba(255, 153, 0, 0.7)",
          type: "line",
          data: [this.profits],
        },
      ],
    };

    const config = {
      type: "bar",
      data: dataChart,
      options: {
        responsive: true,
        maintainAspectRatio: false,
        interaction: {
          intersect: false,
        },
        plugins: {
          legend: {
            display: false, // This hides all text in the legend and also the labels.
          },
        },
      },
    };

    /*
    const config = {
      type: "bar",
      data: dataChart,
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          x: {
            grid: {
              display : false
            },
            border : {
              color : colorGrid
            },
            ticks : {
              color : colorGrid
            },
            stacked: true
          },
          y: {
            grid: {
              display : false
            },
            border : {
              color :  colorGrid
            },
            ticks : {
              color : colorGrid
            },
            stacked: true
          },
        },
        plugins: {
          legend: {
            display: false, // This hides all text in the legend and also the labels.
          },
        },
      },
    };
    */

    if (this.chart != undefined) {
      this.chart.destroy();
    }

    this.chart = new Chart(canvas, config);
  }

  drawBarLineChart(canvas, invoices) {
    let dataChart;

    if (this.filter.show === "quarterly") {
      dataChart = {
        labels: ["1T", "2T", "3T", "4T"],
        datasets: [],
      };
    } else {
      dataChart = {
        labels: [
          "Enero",
          "Febrero",
          "Marzo",
          "Abril",
          "Mayo",
          "Junio",
          "Julio",
          "Agosto",
          "Septiembre",
          "Octubre",
          "Noviembre",
          "Diciembre",
        ],
        datasets: [],
      };
    }

    if (this.filter.show === "quarterly") {
      // Crear un objeto para almacenar resultados por trimestre
      let quarterlyTotals = [
        {
          period: "1T",
          sales: 0,
          purchases: 0,
          expenses: 0,
          tickets: 0,
          total: 0,
        },
        {
          period: "2T",
          sales: 0,
          purchases: 0,
          expenses: 0,
          tickets: 0,
          total: 0,
        },
        {
          period: "3T",
          sales: 0,
          purchases: 0,
          expenses: 0,
          tickets: 0,
          total: 0,
        },
        {
          period: "4T",
          sales: 0,
          purchases: 0,
          expenses: 0,
          tickets: 0,
          total: 0,
        },
      ];

      // Clasificar cada registro por trimestre y calcular los valores
      invoices.forEach((item) => {
        let date = new Date(item.date);
        let month = date.getMonth(); // Los meses en JavaScript comienzan desde 0
        let quarter;

        if (month >= 0 && month <= 2) quarter = "1T";
        else if (month >= 3 && month <= 5) quarter = "2T";
        else if (month >= 6 && month <= 8) quarter = "3T";
        else if (month >= 9 && month <= 11) quarter = "4T";

        // Encontrar el objeto correspondiente al trimestre
        const quarterObj = quarterlyTotals.find((q) => q.period === quarter);

        // Sumar el campo total al tipo correspondiente en el trimestre
        switch (item.type) {
          case 1: // Sales
            quarterObj.sales += item.total;
            break;
          case 0: // Purchases
            quarterObj.purchases += item.total;
            break;
          case 2: // Expenses
            quarterObj.expenses += item.total;
            break;
          case 3: // Tickets
            quarterObj.tickets += item.total;
            break;
        }
      });

      // Calcular el total para cada trimestre
      quarterlyTotals.forEach((q) => {
        q.total = q.sales - q.purchases - q.expenses - q.tickets;
      });

      dataChart.datasets = [
        {
          label: "Ventas",
          backgroundColor: "rgb(51, 102, 204, 0.7)",
          data: quarterlyTotals.map((item) => item.sales),
          stack: "Stack 0",
        },
        {
          label: "Compras",
          backgroundColor: "rgb(220, 57, 18, 0.7)",
          data: quarterlyTotals.map((item) => item.purchases),
          stack: "Stack 1",
        },
        {
          label: "Gastos.",
          backgroundColor: "rgb(255, 153, 0, 0.7)",
          data: quarterlyTotals.map((item) => item.expenses),
          stack: "Stack 1",
        },
        {
          label: "Tickets",
          backgroundColor: "rgb(16, 150, 24, 0.7)",
          data: quarterlyTotals.map((item) => item.tickets),
          stack: "Stack 1",
        },
        {
          label: "Beneficios",
          backgroundColor: "rgba(255, 153, 0, 0.7)",
          type: "line",
          data: quarterlyTotals.map((item) => item.total),
        },
      ];
    } else {
      let months = [
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
      ];
      
      let monthlyTotals = [];
      
      // Rellenar los meses con valores por defecto
      months.forEach((month) => {
        monthlyTotals.push({
          period: month,
          sales: 0,
          purchases: 0,
          expenses: 0,
          tickets: 0,
          total: 0
        });
      });
      
      // Procesar los datos
      invoices.forEach((item) => {
        const date = new Date(item.date);
        if (date.getFullYear() === this.filter.year) {
          // Filtrar solo datos del año especificado
          const monthIndex = date.getMonth(); // Obtener el índice del mes (0 = Enero, 11 = Diciembre)
          const monthName = months[monthIndex]; // Obtener el nombre del mes en español
      
          // Encontrar el objeto correspondiente al mes
          const monthObj = monthlyTotals.find(m => m.period === monthName);
      
          // Sumar el campo total al tipo correspondiente
          switch (item.type) {
            case 1: // Sales
              monthObj.sales += item.total;
              break;
            case 0: // Purchases
              monthObj.purchases += item.total;
              break;
            case 2: // Expenses
              monthObj.expenses += item.total;
              break;
            case 3: // Tickets
              monthObj.tickets += item.total;
              break;
          }
        }
      });
      
      // Calcular el total para cada mes
      monthlyTotals.forEach(m => {
        m.total = m.sales - m.purchases - m.expenses - m.tickets;
      });

      dataChart.datasets = [
        {
          label: "Ventas",
          backgroundColor: "rgb(51, 102, 204, 0.7)",
          stack: "Stack 0",
          data: monthlyTotals.map((item) => item.sales),
        },
        {
          label: "Compras",
          backgroundColor: "rgb(220, 57, 18, 0.7)",
          stack: "Stack 1",
          data: monthlyTotals.map((item) => item.purchases),
        },
        {
          label: "Gastos.",
          backgroundColor: "rgb(255, 153, 0, 0.7)",
          stack: "Stack 1",
          data: monthlyTotals.map((item) => item.expenses),
        },
        {
          label: "Tickets",
          backgroundColor: "rgb(16, 150, 24, 0.7)",
          stack: "Stack 1",
          data: monthlyTotals.map((item) => item.tickets),
        },
        {
          label: "Beneficios",
          backgroundColor: "rgba(255, 153, 0, 0.7)",
          type: "line",
          data: monthlyTotals.map((item) => item.total),
        },
      ];
    }

    const config = {
      type: "bar",
      data: dataChart,
      options: {
        responsive: true,
        maintainAspectRatio: false,
        interaction: {
          intersect: false,
        },
        plugins: {
          legend: {
            display: false, // This hides all text in the legend and also the labels.
          },
        },
      },
    };

    if (this.chart != undefined) {
      this.chart.destroy();
    }

    this.chart = new Chart(canvas, config);
  }

  getTitlePeriod(period) {
    switch (period) {
      case "yearly":
        return "Vista anual";
      case "quarterly":
        return "Vista trimestral";
      case "monthly":
        return "Vista mensual";
      default:
        return "";
    }
  }
}

window.customElements.define(
  "aon-dashboard-sales-purchases",
  AonDashboardSalesPurchases
);
