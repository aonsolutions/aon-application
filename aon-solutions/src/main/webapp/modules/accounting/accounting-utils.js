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

  //INCOME

  let trIncome = document.createElement("tr");
  let tdIncomeColor = document.createElement("td");
  tdIncomeColor.style.width = colorSize * 2.5 + "px";

  let divIncomeColor = document.createElement("div");
  divIncomeColor.style.width = "100%";
  divIncomeColor.style.height = colorSize + "px";
  divIncomeColor.style.borderRadius = "1px";
  divIncomeColor.style.background = "#3366cc";

  tdIncomeColor.appendChild(divIncomeColor);
  trIncome.appendChild(tdIncomeColor);

  let tdIncomeDesc = document.createElement("td");
  tdIncomeDesc.innerHTML = "Ingresos";
  tdIncomeDesc.style.textIndent = "1em";

  trIncome.appendChild(tdIncomeDesc);

  mobileLegend.appendChild(trIncome);

  //OUTGOING

  let trOutgoing = document.createElement("tr");
  let tdOutgoingColor = document.createElement("td");
  tdOutgoingColor.style.width = colorSize * 2.5 + "px";

  let divOutgoingColor = document.createElement("div");
  divOutgoingColor.style.width = "100%";
  divOutgoingColor.style.height = colorSize + "px";
  divOutgoingColor.style.borderRadius = "1px";
  divOutgoingColor.style.background = "#dc3912";

  tdOutgoingColor.appendChild(divOutgoingColor);
  trOutgoing.appendChild(tdOutgoingColor);

  let tdOutgoingDesc = document.createElement("td");
  tdOutgoingDesc.innerHTML = "Gastos";
  tdOutgoingDesc.style.textIndent = "1em";

  trOutgoing.appendChild(tdOutgoingDesc);

  mobileLegend.appendChild(trOutgoing);

  //BENEFIT

  let trBenefit = document.createElement("tr");
  let tdBenefitColor = document.createElement("td");
  tdBenefitColor.style.width = colorSize * 2.5 + "px";

  let divBenefitColor = document.createElement("div");
  divBenefitColor.style.width = "100%";
  divBenefitColor.style.height = colorSize / 3 + "px";
  divBenefitColor.style.borderRadius = "1px";
  divBenefitColor.style.background = "#ff9900";

  tdBenefitColor.appendChild(divBenefitColor);
  trBenefit.appendChild(tdBenefitColor);

  let tdBenefitDesc = document.createElement("td");
  tdBenefitDesc.innerHTML = "Beneficios";
  tdBenefitDesc.style.textIndent = "1em";

  trBenefit.appendChild(tdBenefitDesc);

  mobileLegend.appendChild(trBenefit);

  return mobileLegend;
}
