import { formatDate } from "../../services/utils.js";
import { getTotal } from "../laboral/company/pieChar.js";
import { getRandomColor } from "./accounting-utils.js";

let selectedElement,
chartData,
accounts,
arrIncome,
income,
arrPurchases,
purchases,
arrOutgoings,
outgoings,
raw,
arrAmortizations,
amortizations,
liquid,
data1,
data2,
chart,
options,
selAccounts,
position = null;

function getDateFromString(strDate) {

  if (typeof(strDate) == "string") {
    let splittedDate = strDate.split("/");
    
    if (splittedDate.length != 3)
      return null;
    
    return new Date(`${splittedDate[1]}/${splittedDate[0]}/${splittedDate[2]}`);
  } else
    return null;

}

function formatNumber(number) {
  if (number && !Number.isNaN(number)) {
    let nmbr = Math.round(number*100) / 100;

    return nmbr.toLocaleString("es-ES", { minimumFractionDigits: 2 });
  }
  return null;
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

function getTotalByColumn(selectedColumn) {
  switch (selectedColumn) {
    case 1:
      return income;
    case 2:
      return purchases;
    case 3:
      return outgoings;
    case 5:
      return amortizations;
    default:
      return null;
  }
}

export const colChart = (div, data, selectedPeriod, isMobile, filter) => {
  
  return new Promise((resolve) => {
    // let arr7 = ["SALES", "STOCK", "WORK", "OTHER_INCOMES"];
    // let arr6 = ["PURCHASES", "SALARIES", "OPERATING_EXPENSES", "TAXES", "AMORTIZATION"];
    // let arrUndef = ["EXTRA_INCOME_EXPENSES", "FINANCIAL_INCOME_EXPENSES", "PROVISION"];


    //PARA EL TRIMESTRAL


    /*if (filter && filter.show === "quarterly") {
      console.log("QUARTER");
      console.log(selectedPeriod);
      console.log(accounts);
      // accounts = data.intervals;
      let dteFrom = getDateFromString(selectedPeriod.initiationDate);
      accounts = [
      ];
      for (let i=1; i<=4; i++) {
        let dteTo = new Date(dteFrom.getTime());
        dteTo.setMonth(dteTo.getMonth()+3);
        dteTo.setDate(0);



        let stmnts = [];
        for (let j=0; j<data.intervals.length; j++) {
          let interFrom = getDateFromString(data.intervals[j].interval.fromDate);
          let interTo = getDateFromString(data.intervals[j].interval.toDate);

          if((interFrom != interTo) && (dteFrom <= interFrom && dteTo >= interTo)) {
            let repeatedAccount = stmnts.filter( st => JSON.stringify(st.account) == JSON.stringify(data.intervals[j].account));
            if (repeatedAccount.length > 0) {
              repeatedAccount[0].debit = repeatedAccount[0].debit + data.intervals[j].debit;
              repeatedAccount[0].credit = repeatedAccount[0].credit + data.intervals[j].credit;
            } else {
              let statement = {
                account: data.intervals[j].account,
                credit: data.intervals[j].credit,
                debit: data.intervals[j].debit
              };
              stmnts.push(statement);
            }

          } else break;

        }





        let quarter =
          {
            interval: {
              fromDate: formatDate(dteFrom),
              toDate: formatDate(dteTo),
              name: `T${i}`
            },
            statements: stmnts
          };
          accounts.push(quarter);
          dteFrom.setMonth(dteFrom.getMonth()+3);
          dteFrom.setDate(1);
      }
      console.log("weas");
      console.log(accounts);
    } else {
      accounts = data.intervals || [];
    }*/
    accounts = data.intervals || [];

    const drawChart = () => {


      if (accounts.length > 1) {
        chartData = [["Month", "Ingresos", "Gastos", "Beneficio"]];

      

        if (accounts != null) {
          let elements = accounts.filter(
            (acc) => acc.interval.fromDate != acc.interval.toDate
          );
  
          if (elements != null) {
            elements.forEach((element) => {
              let result = element.statements.filter(
                (st) => st.account.type == "RESULT"
              )[0];
              let total = result.credit - result.debit;
              let entry = [
                element.interval.name,
                result.credit,
                result.debit,
                total,
              ];
              chartData.push(entry);
            });
          }
        }
  
        let table = google.visualization.arrayToDataTable(chartData);
        position = isMobile ? "none" : "right"; 
        let options = {
          title: `Gráfico de resultados ${selectedPeriod.name}`,
          vAxis: { title: "Cantidad (€)" },
          hAxis: { title: "Mes" },
          seriesType: "bars",
          series: { 2: { type: "line" } },
          height: isMobile ? window.innerHeight / 2 : window.innerWidth / 3,
          legend: {
                    position: position,
                    alignment:'center'
                  }
        };
  
        chart = new google.visualization.ComboChart(div);
  
        google.visualization.events.addListener(chart, "select", callbackYear);
  
        document.getElementById("aonGraphicsTrialToolbarHeaderToolSectionBackButtonIconButton").style.display = "none";
  
        chart.draw(table, options);

      } else {
        let divMessage = document.createElement("div");
        divMessage.style.display = "flex";
        divMessage.style.justifyContent = "center";
        divMessage.style.alignItems = "center";
        divMessage.style.paddingTop = "1.5em";


        let message = document.createElement("h4");
        message.style.textAlign = "center";
        message.innerHTML = "NO HAY DATOS DISPONIBLES PARA ESTA CONSULTA";
        message.style.margin = "0";
        
        let infoSpan = document.createElement("span");
        infoSpan.classList.add("material-icons-outlined");
        infoSpan.style.fontSize = "3.5em";
        infoSpan.style.marginRight = "10px";


        infoSpan.innerHTML = "info";


        divMessage.appendChild(infoSpan);
        divMessage.appendChild(message);

        div.appendChild(divMessage);

        document.getElementById("aonGraphicsTrialToolbarHeaderToolSectionBackButtonIconButton")
        .addEventListener("click", () => {
          drawChart();
          document.querySelector("#aonAccountingSidenavOPCIONESList li:nth-child(1)").click();

          
        });

      }



    
    };

    google.charts.load("current", { packages: ["corechart"] });
    google.charts.setOnLoadCallback(drawChart);
    resolve(true);

    const callbackYear = () => {
      if (chart.getSelection()[0] != null) {
        let selectedRow = chart.getSelection()[0].row;

        let intervalName = chartData[selectedRow + 1][0];

        selectedElement = accounts.filter(
          (element) => element.interval.name == intervalName
        )[0];

        drawPeriodChart(selectedElement);
      }
    };

    function listener() {
      if (chart.getSelection()[0] != null) {
        let selectedColumn = chart.getSelection()[0].column;
        let arr = getArray(selectedColumn);

        let formatter = Intl.DateTimeFormat("es");

        let sDteStr = selectedElement.interval.fromDate;
        let eDteStr = selectedElement.interval.toDate;

        let title = getTitle(selectedColumn) + ` del ${sDteStr} al ${eDteStr}`;

        if (arr != null) {
          data2 = new google.visualization.DataTable();
          data2.addColumn("string", "Concepto");
          data2.addColumn("number", "Cantidad");

          let rows = new Array();

          let colors = [];

          arr.forEach((entry) => {
            let amount = null;
            if (selectedColumn == 1) amount = entry.credit - entry.debit;
            else amount = entry.debit - entry.credit;

            rows.push([
              entry.account.description,
              { v: amount, f: formatNumber(amount) + " €" },
            ]);
            colors.push(getRandomColor());
          });

          data2.addRows(rows);

          let opt = {
            // title: title,
            height: window.innerHeight / 100 * 80,
            // width: (window.innerWidth / 2),
            isStacked: true,
            colors: colors,
            legend: { position: "none" },
          };

          document.getElementById("aonGraphicsTrialToolbarHeaderToolSectionBackButtonIconButton")
          .addEventListener("click", () => {
            drawPeriodChart(selectedElement);
          });


          

          let pieDiv = document.createElement("div");
          pieDiv.id = "pieDiv";

          console.log(isMobile);
          pieDiv.style.width =  isMobile ? "100%" : "60%";

          var chart2 = new google.visualization.PieChart(pieDiv);

          let legend = document.createElement("table");
          legend.id = "legend";
          legend.style.padding = "10px";
          legend.style.marginTop = "auto";
          legend.style.marginBottom = "auto";

          for (let i = 0; i < arr.length; i++) {
            let amount = selectedColumn != 1 ? arr[i].debit - arr[i].credit : arr[i].credit - arr[i].debit;

            let legendTr = document.createElement("tr");
            let colorTd = document.createElement("td");
            // colorTd.style.backgroundColor = colors[i];
            // colorTd.style.width = "50px";

            const colorDiv = document.createElement("div");
            colorDiv.style.background = colors[i];

            const pixels = 15;

            colorDiv.style.width = pixels + "px";
            colorDiv.style.height = pixels + "px";
            // colorDiv.style.padding = "5px";
            colorDiv.style.borderRadius = "999px";

            colorTd.appendChild(colorDiv);

            legendTr.appendChild(colorTd);

            let percentTd = document.createElement("td");
            percentTd.id = "percentTd";
            let percent = amount / getTotalByColumn(selectedColumn) * 100;
            percentTd.innerHTML = formatNumber(percent) + "%";
            percentTd.style.textAlign = "right";
            percentTd.style.width = "4em";
            legendTr.appendChild(percentTd);




            let descTd = document.createElement("td");
            descTd.style.maxWidth = "10em";
            

            // descTd.style.fontSize = ".8em";
            descTd.style.whiteSpace = "nowrap";
            descTd.style.textOverflow = "ellipsis";
            descTd.style.overflow = "hidden";
            let description = arr[i].account.description;
            descTd.innerText = description;
            descTd.style.padding = "5px";
            descTd.style.cursor = "pointer";
            // descTd.dataset["complete"] = arr[i].account.description;
            descTd.addEventListener("click", () => {
              if (descTd.style.whiteSpace == "nowrap") {
                descTd.style.whiteSpace = "";
                descTd.style.textOverflow = "";
                descTd.style.overflow = "";
                colorTd.style.display = "flex";
                colorTd.style.alignItems = "flex-start";
                colorDiv.style.marginTop = "6.5px";
                percentTd.style.verticalAlign = "text-top";
                percentTd.style.marginTop = "5.5px";
              } else {
                descTd.style.whiteSpace = "nowrap";
                descTd.style.textOverflow = "ellipsis";
                descTd.style.overflow = "hidden";
                colorTd.style.display = "";
                colorTd.style.alignItems = "";
                colorDiv.style.marginTop = "";
                percentTd.style.verticalAlign = "";
                percentTd.style.marginTop = ""
              }
            });


            legendTr.appendChild(descTd);
            
            let amountTd = document.createElement("td");
            amountTd.style.textAlign = "right";
            amountTd.style.width = "15em";
            amountTd.innerText = formatNumber(amount)+ "€";
            legendTr.appendChild(amountTd);
            legend.appendChild(legendTr);
            legend.style.width = isMobile ? "95%": "38%";
          }

          let head = document.createElement("h1");
          head.innerHTML = title;
          head.style.fontSize = "1em";

          div.innerHTML = "";
          div.appendChild(pieDiv);
          div.appendChild(legend);
          div.style.marginTop = "3em"
          div.style.marginBottom = "3em"
          chart2.draw(data2, opt);
          pieDiv.prepend(head);
        }
      }
    }


    function drawPeriodChart(selectedElement) {
      div.innerHTML = "";



      document.getElementById("aonGraphicsTrialToolbarHeaderToolSectionBackButtonIconButton")
      .addEventListener("click", () => {
        drawChart();
        document.getElementById("aonGraphicsTrialToolbarHeaderToolSectionBackButtonIconButton").style.display = "none";
      });


      selAccounts = selectedElement.statements;

      arrIncome = selAccounts.filter(
        (a) =>
          a.account.code != null &&
          a.account.code.length > 2 &&
          ((a.account.code.substring(0, 1) == "7" && a.credit - a.debit > 0) ||
            (a.account.code.substring(0, 1) == "6" && a.debit - a.credit < 0))
      ).sort((a, b) => (b.credit-b.debit)-(a.credit-a.debit));

      income =
        arrIncome.length != 0
          ? arrIncome.map((a) => a.credit - a.debit).reduce((a, b) => a + b)
          : 0;

      arrPurchases = selAccounts.filter(
        (a) =>
          a.account.code != null &&
          a.account.code.length > 2 &&
          Number.parseInt(a.account.code.substring(0, 2)) >= 60 &&
          Number.parseInt(a.account.code.substring(0, 2)) <= 61 &&
          a.debit - a.credit > 0
      ).sort((a, b) => (b.debit-b.credit)-(a.debit-a.credit));

      purchases =
        arrPurchases.length != 0
          ? arrPurchases.map((a) => a.debit - a.credit).reduce((a, b) => a + b)
          : 0;

      arrOutgoings = selAccounts.filter(
        (a) =>
          a.account.code != null &&
          a.account.code.length > 2 &&
          ((Number.parseInt(a.account.code.substring(0, 2)) >= 62 &&
            Number.parseInt(a.account.code.substring(0, 2)) <= 67 &&
            a.debit - a.credit > 0) ||
            (a.account.code.substring(0, 1) == "7" && a.credit - a.debit < 0))
      ).sort((a, b) => (b.debit-b.credit)-(a.debit-a.credit));

      outgoings =
        arrOutgoings.length != 0
          ? arrOutgoings.map((a) => a.debit - a.credit).reduce((a, b) => a + b)
          : 0;

      raw = income - purchases - outgoings;

      arrAmortizations = selAccounts.filter(
        (a) =>
          a.account.code != null &&
          a.account.code.length > 2 &&
          a.account.code.substring(0, 2) == "68" &&
          a.debit - a.credit > 0
      ).sort((a, b) => (b.debit-b.credit)-(a.debit-a.credit));

      amortizations =
        arrAmortizations.length != 0
          ? arrAmortizations
              .map((a) => a.debit - a.credit)
              .reduce((a, b) => a + b)
          : 0;

      liquid = raw - amortizations;
      google.charts.load("current", { packages: ["corechart"] });
      google.charts.setOnLoadCallback(drawBarChart);
    }

    function drawBarChart() {
      // Create the data table.

      document.getElementById("aonGraphicsTrialToolbarHeaderToolSectionBackButtonIconButton").style.display = "";

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
          "Vtas./Ing.",
          { v: income, f: formatNumber(income) + " €" },
          null,
          null,
          null,
          null,
          null,
        ],
        [
          "Cpas./Gtos.",
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

      let sDteStr = selectedElement.interval.fromDate;
      let eDteStr = selectedElement.interval.toDate;
      // Set chart options
      options = {
        title: `Gráfico de resultados del ${sDteStr} al ${eDteStr}`,
        vAxis: { title: "Cantidad (€)" },
        height: isMobile ? window.innerHeight / 2 : window.innerWidth / 3,
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
  });
};
