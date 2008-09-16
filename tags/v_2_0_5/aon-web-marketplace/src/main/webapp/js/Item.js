function onLoad(){
	calc('Item_basePrice = ( ( Item_purchasePrice * ( 1 + Item_expensesPercentReal / 100) + Item_expensesFixedReal ) * ( 1 + ( Tax_percentage + Tax_surcharge ) / 100 ) )');
	calc('Item_salesPrice = ( Item_price * ( 1 + Tax_percentage / 100 ) )', 2);

	calc('Item_expensesPercent = ( Item_expensesPercentReal )');
	calc('Item_expensesFixed = ( Item_expensesFixedReal )');
	calc('Item_basePrice2 = ( ( Item_purchasePrice2 * ( 1 + Item_expensesPercent / 100) + Item_expensesFixed ) * ( 1 + ( Item_tax + Item_surcharge ) / 100 ) )');
	calc('Item_taxableBase = ( Item_price )');
	calc('Item_salesPrice2 = ( Item_salesPrice )', 2);
	calc('Item_benefit = ( Item_salesPrice2 - Item_basePrice2 )');
	if (document.getElementById('Item_basePrice2').value != 0) {
		calc('Item_benefitPercent = ( Item_benefit * 100 / Item_basePrice2 )');
	} else {
		calc('Item_benefitPercent = 0');
	}
	if (document.getElementById('Item_salesPrice2').value != 0) {
		calc('Item_benefitPercent2 = ( Item_benefit * 100 / Item_salesPrice2 )');
	} else {
		calc('Item_benefitPercent2 = 0');
	}
}

function calc_Item_purchasePrice(){
	calc('Item_purchasePrice = ( Item_purchasePrice )', 2);
	calc('Item_basePrice = ( ( Item_purchasePrice * ( 1 + Item_expensesPercentReal / 100) + Item_expensesFixedReal ) * ( 1 + ( Tax_percentage + Tax_surcharge ) / 100 ) )');
	if (document.getElementById('Item_basePrice').value != 0) {
		calc('Item_benefitPercentReal = ( ( Item_salesPrice - Item_basePrice ) * 100 / Item_basePrice )');
	} else {
		calc('Item_benefitPercentReal = 0');
	}
}

function calc_Item_salesPrice(){
	calc('Item_salesPrice = ( Item_salesPrice )', 2);
	calc('Item_price = ( Item_salesPrice / ( 1 + ( Tax_percentage / 100 ) ) )');
	if (document.getElementById('Item_basePrice').value != 0) {
		calc('Item_benefitPercentReal = ( ( Item_salesPrice - Item_basePrice ) * 100 / Item_basePrice )');
	} else {
		calc('Item_benefitPercentReal = 0');
	}
}

function calc_Item_purchasePrice2(){
	calc('Item_purchasePrice2 = ( Item_purchasePrice2 )', 2);
	calc('Item_basePrice2 = ( ( Item_purchasePrice2 * ( 1 + Item_expensesPercent / 100) + Item_expensesFixed ) * ( 1 + ( Item_tax + Item_surcharge ) / 100 ) )');
	calc('Item_benefit = ( Item_salesPrice2 - Item_basePrice2 )');
	if (document.getElementById('Item_basePrice2').value != 0) {
		calc('Item_benefitPercent = ( Item_benefit * 100 / Item_basePrice2 )');
	} else {
		calc('Item_benefitPercent = 0');
	}
	if (document.getElementById('Item_salesPrice2').value != 0) {
		calc('Item_benefitPercent2 = ( Item_benefit * 100 / Item_salesPrice2 )');
	} else {
		calc('Item_benefitPercent2 = 0');
	}
}

function calc_Item_expensesPercent(){
	calc('Item_expensesPercent = ( Item_expensesPercent )');
	calc('Item_basePrice2 = ( ( Item_purchasePrice2 * ( 1 + Item_expensesPercent / 100) + Item_expensesFixed ) * ( 1 + ( Item_tax + Item_surcharge ) / 100 ) )');
	calc('Item_benefit = ( Item_salesPrice2 - Item_basePrice2 )');
	if (document.getElementById('Item_basePrice2').value != 0) {
		calc('Item_benefitPercent = ( Item_benefit * 100 / Item_basePrice2 )');
	} else {
		calc('Item_benefitPercent = 0');
	}
	if (document.getElementById('Item_salesPrice2').value != 0) {
		calc('Item_benefitPercent2 = ( Item_benefit * 100 / Item_salesPrice2 )');
	} else {
		calc('Item_benefitPercent2 = 0');
	}
}

function calc_Item_expensesFixed(){
	calc('Item_expensesFixed = ( Item_expensesFixed )');
	calc('Item_basePrice2 = ( ( Item_purchasePrice2 * ( 1 + Item_expensesPercent / 100) + Item_expensesFixed ) * ( 1 + ( Item_tax + Item_surcharge ) / 100 ) )');
	calc('Item_benefit = ( Item_salesPrice2 - Item_basePrice2 )');
	if (document.getElementById('Item_basePrice2').value != 0) {
		calc('Item_benefitPercent = ( Item_benefit * 100 / Item_basePrice2 )');
	} else {
		calc('Item_benefitPercent = 0');
	}
	if (document.getElementById('Item_salesPrice2').value != 0) {
		calc('Item_benefitPercent2 = ( Item_benefit * 100 / Item_salesPrice2 )');
	} else {
		calc('Item_benefitPercent2 = 0');
	}
}

function calc_Item_basePrice2(){
	calc('Item_basePrice2 = ( Item_basePrice2 )');
	calc('Item_purchasePrice2 = ( ( ( Item_basePrice2 / ( 1 + ( Item_tax + Item_surcharge ) / 100 ) ) - Item_expensesFixed ) / ( 1 + Item_expensesPercent / 100 ) )', 2);
	calc('Item_benefit = ( Item_salesPrice2 - Item_basePrice2 )');
	if (document.getElementById('Item_basePrice2').value != 0) {
		calc('Item_benefitPercent = ( Item_benefit * 100 / Item_basePrice2 )');
	} else {
		calc('Item_benefitPercent = 0');
	}
	if (document.getElementById('Item_salesPrice2').value != 0) {
		calc('Item_benefitPercent2 = ( Item_benefit * 100 / Item_salesPrice2 )');
	} else {
		calc('Item_benefitPercent2 = 0');
	}
}

function calc_Item_benefitPercent(){
	calc('Item_benefitPercent = ( Item_benefitPercent )');
	calc('Item_benefit = ( Item_benefitPercent * Item_basePrice2 / 100 )');
	calc('Item_salesPrice2 = ( Item_basePrice2 + Item_benefit )', 2);
	calc('Item_taxableBase = ( Item_salesPrice2 / ( 1 + ( Item_tax2 / 100 ) ) )');
	if (document.getElementById('Item_salesPrice2').value != 0) {
		calc('Item_benefitPercent2 = ( Item_benefit * 100 / Item_salesPrice2 )');
	} else {
		calc('Item_benefitPercent2 = 0');
	}
}

function calc_Item_benefitPercent2(){
	calc('Item_benefitPercent2 = ( Item_benefitPercent2 )');
	if (document.getElementById('Item_benefitPercent2').value >= 100) {
		alert("Valor incorrecto.");
		document.getElementById('Item_benefitPercent2').focus();
	} else {
		calc('Item_salesPrice2 = ( ( Item_basePrice2 * 100 ) / ( 100 - Item_benefitPercent2 ) )', 2);
		calc('Item_taxableBase = ( Item_salesPrice2 / ( 1 + ( Item_tax2 / 100 ) ) )');
		calc('Item_benefit = ( Item_salesPrice2 - Item_basePrice2 )');
		if (document.getElementById('Item_basePrice2').value != 0) {
			calc('Item_benefitPercent = ( Item_benefit * 100 / Item_basePrice2 )');
		} else {
			calc('Item_benefitPercent = 0');
		}
	}
}

function calc_Item_benefit(){
	calc('Item_benefit = ( Item_benefit )');
	calc('Item_salesPrice2 = ( Item_basePrice2 + Item_benefit )', 2);
	calc('Item_taxableBase = ( Item_salesPrice2 / ( 1 + ( Item_tax2 / 100 ) ) )');
	if (document.getElementById('Item_basePrice2').value != 0) {
		calc('Item_benefitPercent = ( Item_benefit * 100 / Item_basePrice2 )');
	} else {
		calc('Item_benefitPercent = 0');
	}
	if (document.getElementById('Item_salesPrice2').value != 0) {
		calc('Item_benefitPercent2 = ( Item_benefit * 100 / Item_salesPrice2 )');
	} else {
		calc('Item_benefitPercent2 = 0');
	}
}

function calc_Item_taxableBase(){
	calc('Item_taxableBase = ( Item_taxableBase )');
	calc('Item_salesPrice2 = ( Item_taxableBase * ( 1 + Item_tax2 / 100 ) )', 2);
	calc('Item_benefit = ( Item_salesPrice2 - Item_basePrice2 )');
	if (document.getElementById('Item_basePrice2').value != 0) {
		calc('Item_benefitPercent = ( Item_benefit * 100 / Item_basePrice2 )');
	} else {
		calc('Item_benefitPercent = 0');
	}
	if (document.getElementById('Item_salesPrice2').value != 0) {
		calc('Item_benefitPercent2 = ( Item_benefit * 100 / Item_salesPrice2 )');
	} else {
		calc('Item_benefitPercent2 = 0');
	}
}

function calc_Item_salesPrice2(){
	calc('Item_salesPrice2 = ( Item_salesPrice2 )', 2);
	calc('Item_taxableBase = ( Item_salesPrice2 / ( 1 + ( Item_tax2 / 100 ) ) )');
	calc('Item_benefit = ( Item_salesPrice2 - Item_basePrice2 )');
	if (document.getElementById('Item_basePrice2').value != 0) {
		calc('Item_benefitPercent = ( Item_benefit * 100 / Item_basePrice2 )');
	} else {
		calc('Item_benefitPercent = 0');
	}
	if (document.getElementById('Item_salesPrice2').value != 0) {
		calc('Item_benefitPercent2 = ( Item_benefit * 100 / Item_salesPrice2 )');
	} else {
		calc('Item_benefitPercent2 = 0');
	}
}

function calculate(object){
	eval("calc_"+object.name+"();");
}
