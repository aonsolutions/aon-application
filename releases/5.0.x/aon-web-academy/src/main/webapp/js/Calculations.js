DEF_ROUND = 3;
// FUNCION DE CALCULO DE CASILLAS
function calcRound ( exp , round ) {
    var regexpOperand = /=/;
    var arrOperand    = exp.split( regexpOperand );
    if (arrOperand.length == 2) {
        arrOperand[1] = getCalc ( arrOperand[1] );
        var nu = 0;
        nu = eval( arrOperand[1] ) ;
        nu = redondea( nu , round );
        var field = arrOperand[0].replace (" ","");
        eval ("document.getElementById('" + field + "').value = '" + nu + "'");
    } else {
        msg = "Too much = symbols.";
        if (arrOperand.length == 1) msg = "Expecting =";
        alert ( 'Invalid expression. Usage: calc("exp=exp2"):  \n\n' + msg + '\n\n' +exp );
    }
}

function calc ( exp, round ) {
	if (round == null) 
		round = DEF_ROUND;
	calcRound ( exp , round );
}

function parseExp ( exp ) {
    var regexpDone = new RegExp();
    var regexpVariable = /^\D/;
    var regexpPattern  = /[^A-Za-z0-9_\.\,]/;
    var arrVar = exp.split(regexpPattern);
    var strDone = "";
    for (var i=0; (i< arrVar.length) ; i++) {
        if (regexpVariable.test ( arrVar[i] ) ) {
	        regexpDone.compile(arrVar[i]);
	        if (!regexpDone.test ( strDone )) {
	            do {
	            	if(document.getElementById(arrVar[i]) != null){
	            		field = document.getElementById(arrVar[i]).value;
		                exp = exp.replace ( regexpDone, field );
	            	} else{
	            		exp = exp.replace ( regexpDone, 0 );
	            	}
	            } while (regexpDone.test ( exp ));
	            strDone = strDone.concat (arrVar[i]);
	        }
        }
    }
    return exp;
}

// evalua una expresion y retorna su valor. Expresion matem?tica.
function getCalc ( exp ) {
    return (eval ( parseExp ( exp ) ) );
}
// FIN FUNCION DE CALCULO DE CASILLAS

// REDONDEO
function onExit(object){
	onExit(object,DEF_ROUND);
}
function onExit(object,round){
	object.value = deleteLeftZeros(object.value);
	object.value = redondea(object.value, round);
}
function deleteLeftZeros(exp) {
	for(var i=0; i<exp.length; i++) {
		if (exp.charAt(i) != "0" || exp.charAt(i+1) == ".") {
			exp = exp.substring(i, exp.length);
			break;
		}
	}
	return exp;
}
function redondea(xparam , digits ){
    retvalue = xparam;
    if ( digits > 0 ) {
    	value = Math.pow(10,digits);
        retvalue = Math.round(parseFloat(xparam)*value)/value;
    }
    if ( digits == 0 ) {
        retvalue = Math.round(parseFloat(xparam));
    }
    if ( isNaN(parseFloat(retvalue)) ) {
        retvalue = 0;
    }
    return retvalue;
}
// FIN REDONDEO

function setFormat( objName , value , percent ){
    var n = getNumberFormatted( value , percent );
    document.getElementById(objName).innerText = n;
}

function getNumberFormatted(f, percent){
    if (f.length == 0){
        f = getDefaultNumeric ( percent );
    }
    else {
        if (canBeFormatted(f)){
            f = new String ( parseFloat ( f ) );
            var minus = "";
            if (isNeg(f)){
                minus = f.substring(0,1);
                f = f.substring(1,f.length);
            }
            f = setDecimalSeparator(f , percent);
            f = setGroupingSeparator(f);
            f = minus + f;
        }
        else {
            f = getDefaultNumeric ( percent );
        }
    }
    return f;
}

function getDecimalSeparator() {
    return ",";
}

function setDecimalSeparator( n , percent ){
    n = new String ( n );
    var  pos = n.indexOf( "." );
    if ( percent == 0 ){
        if (pos != -1){
            n = n.substring(0,pos);
        }
    }
    else {
        switch (pos){
            case -1:
                n += '.';
                break;
            case 0:
                n += '0' + ".";
                break;
            default:
                break;
        }
        for (i=0; i < percent; i++){
            n += '0';
        }
        pos = n.indexOf( "." );
        n = n.substring(0,pos) + "," + n.substring( pos+1, (pos+1 + percent) );
    }
    return n;
}

function getGroupingSeparator() {
    return ".";
}

function setGroupingSeparator(n){
    var pos = n.indexOf( getDecimalSeparator() );
    if (pos == -1){
        pos = n.length;
    }
    while (pos > 0){
        pos -= 3;
        if (pos <= 0) break;
        n = n.substring(0,pos) + getGroupingSeparator() + n.substring(pos, n.length);
    }
    return n;
}

function getDefaultNumeric ( percent ) {
    var retValue = "";
    if ( percent == null || percent == 0) {
        retValue = "0";
    }
    else {
        retValue += "0" + getDecimalSeparator();
        for ( i=0; i < percent; ++i ){
            retValue +="0";
        }
    }
    return retValue;
}

function canBeFormatted( n ) {
    var regexpPatternF = /^-|^[0-9]|[0-9\.]/;
    return hasMoreCharacters ( n , regexpPatternF );
}

function isNeg(n){
    n = new String ( n );
    var pos = n.substring(0,1);
    return ( (pos == "-") ? true : false );
}

function hasMoreCharacters ( n , regExp ) {
    n = new String ( n );
    var arrVarF = n.split( regExp );
    var arrayLength = 0;
    for (i=0; (i<arrVarF.length);++i){
        if (arrVarF[i] != "") {
            ++arrayLength;
        }
    }
    return (arrayLength>0?false:true);
}

function getNumberUnFormatted(f, percent){
    f = new String ( f );
    if (f.length == 0){
        f = getDefaultNumeric ( percent );
    }
    if ( canBeUnFormatted( f ) ){
        var regexpPattern  = /[^0-9,-]/;
        var arrVar = f.split(regexpPattern);
        var number = arrVar.join("");
        regexpPattern = /,/;
        arrVar = number.split(regexpPattern);
        number = arrVar.join( "." );
        return number;
    }
    else{
        f = getDefaultNumeric ( percent );
    }
}

function canBeUnFormatted( n ){
    var regexpPatternUF = /^-|^[0-9]|[0-9\.,]/;
    return hasMoreCharacters ( n , regexpPatternUF );
}