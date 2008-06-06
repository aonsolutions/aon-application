  function UpdateMessageLength() {
  	var messageBox = document.getElementById( 'homepage:smsForm:SMS_message' );
    var countDescription = '{0} caracteres (max 160)';
    var currentCount = messageBox.value.length;	
    currentCount = currentCount + GetExtendedCharacterCount( messageBox.value );
    document.getElementById( 'homepage:smsForm:messageLength' ).value = countDescription.replace('{0}', currentCount);
  alert( document.getElementById( 'homepage:smsForm:messageLength' ).value );
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
