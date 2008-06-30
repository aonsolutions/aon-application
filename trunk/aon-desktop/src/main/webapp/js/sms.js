  function UpdateMessageLength() {
  	var messageBox = document.getElementById( 'homepage:smsForm:SMS_message' );
    var currentCount = messageBox.value.length;
    if ( currentCount < 161 ) {
	    var countDescription = '{0} caracter' + ((currentCount > 1)? 'es': '') + ' (max 160)';
	    currentCount = currentCount + GetExtendedCharacterCount( messageBox.value );
	    document.getElementById( 'homepage:smsForm:messageLength' ).value = countDescription.replace('{0}', currentCount);
	} else {
    	alert( "Limite de caracteres alcanzado." );
		messageBox.value = messageBox.value.substring(0, 160); 
    }
	
  }		

  function GetExtendedCharacterCount(message) {
    var extendedCharacters = '^{}\\[]|';
    var extendedCharacterCount = 0 ; 
   	
    for (var i = 0; i < message.length; i++) { 
	    if (extendedCharacters.indexOf(message.charAt(i)) > -1) { 
		    extendedCharacterCount++ ;
	    }
    }
    return extendedCharacterCount ;
  }
