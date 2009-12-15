function onLoad(){
	calc('Product_inventoriable = 0', 2);
	calc('Item_salesPrice = ( Item_price * ( 1 + Tax_vat_Lookup_percentage / 100 - Tax_retention_Lookup_percentage / 100 ) )', 2);

	calc('Item_expensesPercent = ( Item_expensesPercentReal )');
	calc('Item_expensesFixed = ( Item_expensesFixedReal )');
	calc('Item_basePrice = ( Item_initialPrice * ( 1 + ( Item_expensesPercent + Item_tax3 + Item_surcharge2 ) / 100 ) + Item_expensesFixed )');
	calc('Item_benefitPercent = ( Item_benefitPercentReal )');
	calc('Item_benefit = ( Item_benefitPercent * Item_basePrice / 100 )');
	calc('Item_taxable_base = ( Item_basePrice + Item_benefit )');
	calc('Item_benefitPercent2 = ( Item_benefit * 100 / Item_taxable_base )');
	calc('Item_salesPrice_new = ( Item_taxable_base * ( 1 + Item_tax2 / 100 ) )', 2);
}

function calc_Item_price(){
	calc('Item_price = ( Item_price )');
	calc('Item_salesPrice = ( Item_price * ( 1 + Tax_vat_Lookup_percentage / 100 - Tax_retention_Lookup_percentage / 100 ) )', 2);
}

function calc_Tax_vat_Lookup_id(){
	calc('Item_salesPrice = ( Item_price * ( 1 + Tax_vat_Lookup_percentage / 100 - Tax_retention_Lookup_percentage / 100 ) )', 2);
}

function calc_Tax_retention_Lookup_id(){
	calc('Item_salesPrice = ( Item_price * ( 1 + Tax_vat_Lookup_percentage / 100 - Tax_retention_Lookup_percentage / 100 ) )', 2);
}

function calc_Item_salesPrice(){
	calc('Item_salesPrice = ( Item_salesPrice )', 2);
	calc('Item_price = ( Item_salesPrice / ( 1 + ( Tax_vat_Lookup_percentage / 100 - Tax_retention_Lookup_percentage / 100 ) ) )');
}

function calc_Item_initialPrice(){
	calc('Item_initialPrice = ( Item_initialPrice )');
	calc('Item_basePrice = ( Item_initialPrice * ( 1 + ( Item_expensesPercent + Item_tax3 + Item_surcharge2 ) / 100 ) + Item_expensesFixed )');
	calc('Item_benefit = ( Item_benefitPercent * Item_basePrice / 100 )');
	calc('Item_taxable_base = ( Item_basePrice + Item_benefit )');
	calc('Item_benefitPercent2 = ( Item_benefit * 100 / Item_taxable_base )');
	calc('Item_salesPrice_new = ( Item_taxable_base * ( 1 + Item_tax2 / 100 ) )', 2);
}

function calc_Item_expensesPercent(){
	calc('Item_expensesPercent = ( Item_expensesPercent )');
	calc('Item_basePrice = ( Item_initialPrice * ( 1 + ( Item_expensesPercent + Item_tax3 + Item_surcharge2 ) / 100 ) + Item_expensesFixed )');
	calc('Item_benefit = ( Item_benefitPercent * Item_basePrice / 100 )');
	calc('Item_taxable_base = ( Item_basePrice + Item_benefit )');
	calc('Item_benefitPercent2 = ( Item_benefit * 100 / Item_taxable_base )');
	calc('Item_salesPrice_new = ( Item_taxable_base * ( 1 + Item_tax2 / 100 ) )', 2);
}

function calc_Item_expensesFixed(){
	calc('Item_expensesFixed = ( Item_expensesFixed )');
	calc('Item_basePrice = ( Item_initialPrice * ( 1 + ( Item_expensesPercent + Item_tax3 + Item_surcharge2 ) / 100 ) + Item_expensesFixed )');
	calc('Item_benefit = ( Item_benefitPercent * Item_basePrice / 100 )');
	calc('Item_taxable_base = ( Item_basePrice + Item_benefit )');
	calc('Item_benefitPercent2 = ( Item_benefit * 100 / Item_taxable_base )');
	calc('Item_salesPrice_new = ( Item_taxable_base * ( 1 + Item_tax2 / 100 ) )', 2);
}

function calc_Item_benefitPercent(){
	calc('Item_benefitPercent = ( Item_benefitPercent )');
	calc('Item_benefit = ( Item_benefitPercent * Item_basePrice / 100 )');
	calc('Item_taxable_base = ( Item_basePrice + Item_benefit )');
	calc('Item_benefitPercent2 = ( Item_benefit * 100 / Item_taxable_base )');
	calc('Item_salesPrice_new = ( Item_taxable_base * ( 1 + Item_tax2 / 100 ) )', 2);
}

function calc_Item_benefitPercent2(){
	calc('Item_benefitPercent2 = ( Item_benefitPercent2 )');
	if (document.getElementById('Item_benefitPercent2').value >= 100) {
		alert("Valor incorrecto.");
		document.getElementById('Item_benefitPercent2').focus();
	} else {
		calc('Item_taxable_base = ( ( Item_basePrice * 100 ) / ( 100 - Item_benefitPercent2 ) )');
		calc('Item_salesPrice_new = ( Item_taxable_base * ( 1 + Item_tax2 / 100 ) )', 2);
		calc('Item_benefit = ( Item_taxable_base - Item_basePrice )');
		if (document.getElementById('Item_basePrice').value != 0) {
			calc('Item_benefitPercent = ( Item_benefit * 100 / Item_basePrice )');
		} else {
			calc('Item_benefitPercent = 0');
		}
	}
}

function calc_Item_benefit(){
	calc('Item_benefit = ( Item_benefit )');
	calc('Item_taxable_base = ( Item_basePrice + Item_benefit )');
	calc('Item_salesPrice_new = ( Item_taxable_base * ( 1 + Item_tax2 / 100 ) )', 2);
	if (document.getElementById('Item_basePrice').value != 0) {
		calc('Item_benefitPercent = ( Item_benefit * 100 / Item_basePrice )');
		calc('Item_benefitPercent2 = ( Item_benefit * 100 / Item_taxable_base )');
	} else {
		calc('Item_benefitPercent = 0');
		calc('Item_benefitPercent2 = 0');
	}
}

function calc_Item_taxable_base(){
	calc('Item_taxable_base = ( Item_taxable_base )');
	calc('Item_salesPrice_new = ( Item_taxable_base * ( 1 + Item_tax2 / 100 ) )', 2);
	calc('Item_benefit = ( Item_taxable_base - Item_basePrice )');
	if (document.getElementById('Item_basePrice').value != 0) {
		calc('Item_benefitPercent = ( Item_benefit * 100 / Item_basePrice )');
		calc('Item_benefitPercent2 = ( Item_benefit * 100 / Item_taxable_base )');
	} else {
		calc('Item_benefitPercent = 0');
		calc('Item_benefitPercent2 = 0');
	}
}

function calc_Item_salesPrice_new(){
	calc('Item_salesPrice_new = ( Item_salesPrice_new )', 2);
	calc('Item_taxable_base = ( Item_salesPrice_new / ( 1 + ( Item_tax2 / 100 ) ) )');
	calc('Item_benefit = ( Item_taxable_base - Item_basePrice )');
	if (document.getElementById('Item_basePrice').value != 0) {
		calc('Item_benefitPercent = ( Item_benefit * 100 / Item_basePrice )');
		calc('Item_benefitPercent2 = ( Item_benefit * 100 / Item_taxable_base )');
	} else {
		calc('Item_benefitPercent = 0');
		calc('Item_benefitPercent2 = 0');
	}
}

function calculate(object){
	eval("calc_"+object.name+"();");
}