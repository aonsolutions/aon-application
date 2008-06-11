  function UpdateMessageLength() {
  	var messageBox = document.getElementById( 'homepage:smsForm:SMS_message' );
    var currentCount = messageBox.value.length;	
    var countDescription = '{0} caracter' + ((currentCount > 1)? 'es': '') + ' (max 160)';
    currentCount = currentCount + GetExtendedCharacterCount( messageBox.value );
    document.getElementById( 'homepage:smsForm:messageLength' ).value = countDescription.replace('{0}', currentCount);
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
