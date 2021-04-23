export function getRandomColor() {
  var letters = "0123456789ABCDEF";
  var color = "#";
  for (var i = 0; i < 6; i++) {
    color += letters[Math.floor(Math.random() * 16)];
  }
  return color;
}
export function getDateFromString(strDate) {
  if (typeof strDate == "string") {
    let splittedDate = strDate.split("/");

    if (splittedDate.length != 3) return null;

    return new Date(`${splittedDate[1]}/${splittedDate[0]}/${splittedDate[2]}`);
  } else return null;
}

export function formatNumber(number) {
  if (number != null && !Number.isNaN(number)) {
    let nmbr = Math.round(number * 100) / 100;

    let formatted = nmbr.toLocaleString("es-ES", { minimumFractionDigits: 2 });

    if (formatted && formatted.length == 7 && formatted.charAt(0) != "-") {
      formatted = formatted.charAt(0) + "." + formatted.substring(1);
    } else if (formatted && formatted.length == 8 && formatted.charAt(0) == "-") {
      formatted = formatted.substring(0, 2) + "." + formatted.substring(2);
    }

    return formatted;
  }
  return null;
}

export function getTitle(selectedColumn) {
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

export function getPeriodName(dateFromStr, dateToStr) {
  let spaMonths = [
    "enero",
    "febrero",
    "marzo",
    "abril",
    "mayo",
    "junio",
    "julio",
    "agosto",
    "septiembre",
    "octubre",
    "noviembre",
    "diciembre",
  ];

  if (typeof dateFromStr == "string" && typeof dateToStr == "string") {
    let splittedFrom = dateFromStr.split("/");
    let splittedTo = dateToStr.split("/");
    if (splittedFrom.length == 3 && splittedTo.length == 3) {
      if (splittedFrom[1] == splittedTo[1]) {
        let monthNmbr = Number.parseInt(splittedFrom[1]);
        return monthNmbr &&
          !Number.isNaN(monthNmbr) &&
          monthNmbr > 0 &&
          monthNmbr < 13
          ? `${spaMonths[monthNmbr - 1]} de ${splittedFrom[2]}`
          : `del ${dateFromStr} al ${dateToStr}`;
      } else if (
        /0?1\/0?1\/d*/.test(dateFromStr) &&
        /31\/0?3\/d*/.test(dateToStr)
      ) {
        return `1T/${splittedFrom[2]}`;
      } else if (
        /0?1\/0?4\/d*/.test(dateFromStr) &&
        /30\/0?6\/d*/.test(dateToStr)
      ) {
        return `2T/${splittedFrom[2]}`;
      } else if (
        /0?1\/0?7\/d*/.test(dateFromStr) &&
        /30\/0?9\/d*/.test(dateToStr)
      ) {
        return `3T/${splittedFrom[2]}`;
      } else if (
        /0?1\/10\/d*/.test(dateFromStr) &&
        /31\/12\/d*/.test(dateToStr)
      ) {
        return `4T/${splittedFrom[2]}`;
      } else {
        return `del ${dateFromStr} al ${dateToStr}`;
      }
    }
  }
  return "";
}

export function getMobileLegend() {
  let mobileLegend = document.createElement("table");
  mobileLegend.style.margin = "auto";

  const colorSize = 15;

  const legendData = [
    {
      color: "#3366cc",
      name: "Vtas./Ing."
    },
    {
      color: "#dc3912",
      name: "Cpas./Gtos./Amort."
    },
    {
      color: "#ff9900",
      name: "Resultado"
    }
  ];

  legendData.forEach(d => {
    let trColumn = document.createElement("tr");
    let tdColumnColor = document.createElement("td");
    tdColumnColor.style.width = colorSize * 2.5 + "px";
  
    let divColumnColor = document.createElement("div");
    divColumnColor.style.width = "100%";
    divColumnColor.style.height = colorSize + "px";
    divColumnColor.style.borderRadius = "1px";
    divColumnColor.style.background = d.color;
  
    tdColumnColor.appendChild(divColumnColor);
    trColumn.appendChild(tdColumnColor);
  
    let tdColumnDescriptor = document.createElement("td");
    tdColumnDescriptor.innerHTML = d.name;
    tdColumnDescriptor.style.textIndent = "1em";
  
    trColumn.appendChild(tdColumnDescriptor);
  
    mobileLegend.appendChild(trColumn);
  });


  return mobileLegend;
}
