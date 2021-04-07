const btn = document.createElement("button");
btn.id = "back";
btn.type = "image";
const btn_img = document.createElement("img");
btn_img.src = "./img/left_arrow.png";
btn_img.style.width = "25px";
btn_img.style.height = "25px";
btn.appendChild(btn_img);

const getData = async() => {
  const jason = await fetch("./data.json").then((response) => response.json()).catch(error => {
    return null;
  });
  if (jason) {
    console.log(jason);
      if (window.innerWidth >= 600) chartWidth = window.innerWidth / 4 * 3;
      else {
          chartWidth = null;
          position = "top";
      }
      for (let i = 0; i < 12; i++) arrMonthly[i] = [0, 0, 0];
      accounts = jason.filter(
        (entry) =>
          Date.parse(entry.fecha) >= Date.parse("2020-01-01") &&
          Date.parse(entry.fecha) <= Date.parse("2020-12-31")
      );
  
      accounts.forEach((entry) => {
        let date = new Date(Date.parse(entry.fecha));
        let monthIndex = date.getMonth();
  
        let amount;
  
        if (entry.cuenta_contable.charAt(0) == "7") {
          amount = entry.haber - entry.debe;
          arrMonthly[monthIndex][0] += amount;
          arrMonthly[monthIndex][2] += amount;
        } else if (
          entry.cuenta_contable.substring(0, 2) >= "60" &&
          entry.cuenta_contable.substring(0, 2) <= "69"
        ) {
          amount = entry.debe - entry.haber;
          arrMonthly[monthIndex][1] += amount;
          arrMonthly[monthIndex][2] -= amount;
        }
      });
      google.charts.load("current", { packages: ["corechart"] });
      google.charts.setOnLoadCallback(drawVisualization);
  }
}

getData();

let accounts;

function formatNumber(number) {
  return number.toLocaleString("es-ES", { minimumFractionDigits: 2 });
}

function getArray(selectedColumn) {
  switch (selectedColumn) {
    case 1:
      return arrIncome;
    case 2:
      return arrPurchases;
    case 3:
      return arrOutgoings;
    case 5:
      return arrAmortizations;
    default:
      return null;
  }
}

function getTitle(selectedColumn) {
  switch (selectedColumn) {
    case 1:
      return "Ingresos";
    case 2:
      return "Compras";
    case 3:
      return "Gastos";
    case 5:
      return "Amortizaciones";
    default:
      return "";
  }
}

var arrMonthly = new Array();

function drawVisualization() {
    document.getElementById("chart_div").style.width = chartWidth;
  var data = google.visualization.arrayToDataTable([
    ["Month", "Ingresos", "Gastos", "Beneficio"],
    ["Ene.", arrMonthly[0][0], arrMonthly[0][1], arrMonthly[0][2]],
    ["Feb.", arrMonthly[1][0], arrMonthly[1][1], arrMonthly[1][2]],
    ["Mar.", arrMonthly[2][0], arrMonthly[2][1], arrMonthly[2][2]],
    ["Abr.", arrMonthly[3][0], arrMonthly[3][1], arrMonthly[3][2]],
    ["May.", arrMonthly[4][0], arrMonthly[4][1], arrMonthly[4][2]],
    ["Jun.", arrMonthly[5][0], arrMonthly[5][1], arrMonthly[5][2]],
    ["Jul.", arrMonthly[6][0], arrMonthly[6][1], arrMonthly[6][2]],
    ["Ago.", arrMonthly[7][0], arrMonthly[7][1], arrMonthly[7][2]],
    ["Sep.", arrMonthly[8][0], arrMonthly[8][1], arrMonthly[8][2]],
    ["Oct.", arrMonthly[9][0], arrMonthly[9][1], arrMonthly[9][2]],
    ["Nov.", arrMonthly[10][0], arrMonthly[10][1], arrMonthly[10][2]],
    ["Dic.", arrMonthly[11][0], arrMonthly[11][1], arrMonthly[11][2]],
  ]);

  var options = {
    title: "Gráfico de resultados 2020",
    vAxis: { title: "Cantidad (€)" },
    hAxis: { title: "Mes" },
    seriesType: "bars",
    series: { 2: { type: "line" } },
    //width: chartWidth,
    height: chartWidth / 2,
    legend: {position: position}
  };

  chart = new google.visualization.ComboChart(
    document.getElementById("chart_div")
  );

  google.visualization.events.addListener(chart, "select", listenerYear);

  chart.draw(data, options);

  if (
    document.getElementById("back") !== null &&
    document.getElementById("back") !== undefined
  )
    document.getElementById("back").remove();
}

function listenerYear() {
  if (chart.getSelection()[0] != null) {
    let selectedRow = chart.getSelection()[0].row;
    startDate = new Date(0);
    startDate.setFullYear(2020);
    startDate.setMonth(selectedRow);
    startDate.setDate(1);

    endDate = new Date(0);
    endDate.setFullYear(2020);
    endDate.setMonth(selectedRow + 1);
    endDate.setDate(0);

    drawPeriodChart(startDate, endDate);
  }
}

function listener() {
  if (chart.getSelection()[0] != null) {
    let selectedColumn = chart.getSelection()[0].column;
    let arr = getArray(selectedColumn);

    let formatter = Intl.DateTimeFormat("es");

    let sDteStr = formatter.format(startDate);
    let eDteStr = formatter.format(endDate);

    let title = getTitle(selectedColumn) + ` del ${sDteStr} al ${eDteStr}`;

    if (arr != null) {
      data2 = new google.visualization.DataTable();
      data2.addColumn("string", "Concepto");
      data2.addColumn("number", "Cantidad");

      let rows = new Array();

      arr.forEach((entry) => {
        let amount = null;
        if (entry.cuenta_contable.charAt(0) == "7")
          amount = entry.haber - entry.debe;
        else amount = entry.debe - entry.haber;

        rows.push([
          entry.cuenta_contable,
          { v: amount, f: formatNumber(amount) + " €" },
        ]);
      });

      data2.addRows(rows);

      let opt = {
        title: title,
        //width: chartWidth,
        height: chartWidth / 2,
        isStacked: true,
      };

      let body = document.getElementsByTagName("body")[0];

      btn.addEventListener("click", () => {
        drawPeriodChart(startDate, endDate);
      });

      body.prepend(btn);

      let mainDiv = document.getElementById("chart_div");
      mainDiv.style.width = chartWidth;
      var chart2 = new google.visualization.PieChart(mainDiv);
      chart2.draw(data2, opt);
    }
  }
}

// Callback that creates and populates a data table,
// instantiates the pie chart, passes in the data and
// draws it.
function drawChart() {
  // Create the data table.
  document.getElementById("chart_div").style.width = chartWidth;
  data1 = new google.visualization.DataTable();
  data1.addColumn("string", "Concepto");
  data1.addColumn("number", "Ingresos");
  data1.addColumn("number", "Compras");
  data1.addColumn("number", "Gastos");
  data1.addColumn("number", "Resultado bruto");
  data1.addColumn("number", "Amortizaciones");
  data1.addColumn("number", "Resultado neto");

  data1.addRows([
    [
      "Ingresos",
      { v: income, f: formatNumber(income) + " €" },
      null,
      null,
      null,
      null,
      null,
    ],
    [
      "Costes",
      null,
      { v: purchases, f: formatNumber(purchases) + " €" },
      { v: outgoings, f: formatNumber(outgoings) + " €" },
      null,
      null,
      null,
    ],
    [
      "Rdo. bruto",
      null,
      null,
      null,
      { v: raw, f: formatNumber(raw) + " €" },
      null,
      null,
    ],
    [
      "Amortiz.",
      null,
      null,
      null,
      null,
      { v: amortizations, f: formatNumber(amortizations) + " €" },
      null,
    ],
    [
      "Rdo. neto",
      null,
      null,
      null,
      null,
      null,
      { v: liquid, f: formatNumber(liquid) + " €" },
    ],
  ]);
  let formatter = Intl.DateTimeFormat("es");

  let sDteStr = formatter.format(startDate);
  let eDteStr = formatter.format(endDate);
  // Set chart options
  options = {
    title: `Gráfico de resultados del ${sDteStr} al ${eDteStr}`,
    vAxis: { title: "Cantidad (€)" },
    //width: chartWidth,
    height: chartWidth / 2,
    isStacked: true,
    legend: {
      position: "none",
    },
  };

  // Instantiate and draw our chart, passing in some options.
  chart = new google.visualization.ColumnChart(
    document.getElementById("chart_div")
  );
  chart.draw(data1, options);

  //Adding listener
  google.visualization.events.addListener(chart, "select", listener);
}

var arrIncome;

var income;

var arrPurchases;

var purchases;

var arrOutgoings;

var outgoings;

var raw;

var arrAmortizations;

var amortizations;

var liquid;

var data1;

var data2;

var chart;

var options;

var selAccounts;

var startDate;

var endDate;

var chartWidth;

var position = null;

window.onresize = () => {
  if (window.innerWidth >= 600) chartWidth = window.innerWidth / 4 * 3;
  else {
      chartWidth = null;
      position = "bottom";
    };
};

function drawPeriodChart(startDate, endDate) {
  let body = document.getElementsByTagName("body")[0];

  btn.addEventListener("click", () => {
    drawVisualization();
  });

  body.prepend(btn);

  selAccounts = accounts.filter(
    (entry) =>
      Date.parse(entry.fecha) >= startDate && Date.parse(entry.fecha) <= endDate
  );
  arrIncome = selAccounts.filter(
    (a) =>
      a.cuenta_contable != null &&
      a.cuenta_contable.length > 2 &&
      a.cuenta_contable.substring(0, 1) == "7"
  );

  income =
    arrIncome.length != 0
      ? arrIncome.map((a) => a.haber - a.debe).reduce((a, b) => a + b)
      : 0;

  arrPurchases = selAccounts.filter(
    (a) =>
      a.cuenta_contable != null &&
      a.cuenta_contable.length > 2 &&
      Number.parseInt(a.cuenta_contable.substring(0, 2)) >= 60 &&
      Number.parseInt(a.cuenta_contable.substring(0, 2)) <= 61
  );

  purchases =
    arrPurchases.length != 0
      ? arrPurchases.map((a) => a.debe - a.haber).reduce((a, b) => a + b)
      : 0;

  arrOutgoings = selAccounts.filter(
    (a) =>
      a.cuenta_contable != null &&
      a.cuenta_contable.length > 2 &&
      Number.parseInt(a.cuenta_contable.substring(0, 2)) >= 62 &&
      Number.parseInt(a.cuenta_contable.substring(0, 2)) <= 67
  );

  outgoings =
    arrOutgoings.length != 0
      ? arrOutgoings.map((a) => a.debe - a.haber).reduce((a, b) => a + b)
      : 0;

  raw = income - purchases - outgoings;

  arrAmortizations = selAccounts.filter(
    (a) =>
      a.cuenta_contable != null &&
      a.cuenta_contable.length > 2 &&
      a.cuenta_contable.substring(0, 2) == "68"
  );

  amortizations =
    arrAmortizations.length != 0
      ? arrAmortizations.map((a) => a.debe - a.haber).reduce((a, b) => a + b)
      : 0;

  liquid = raw - amortizations;

  // Load the Visualization API and the corechart package.
  google.charts.load("current", { packages: ["corechart"] });

  // Set a callback to run when the Google Visualization API is loaded.
  google.charts.setOnLoadCallback(drawChart);
}
