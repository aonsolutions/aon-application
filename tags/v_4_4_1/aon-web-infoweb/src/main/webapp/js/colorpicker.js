/************************************************************************************************************
(C) www.dhtmlgoodies.com, October 2005

This is a script from www.dhtmlgoodies.com. You will find this and a lot of other scripts at our website.	

Terms of use:
You are free to use this script as long as the copyright message is kept intact. However, you may not
redistribute, sell or repost it without our permission.

Thank you!

www.dhtmlgoodies.com
Alf Magne Kalleland

************************************************************************************************************/	

var MSIE = navigator.userAgent.indexOf('MSIE')>=0?true:false;
var navigatorVersion = navigator.appVersion.replace(/.*?MSIE (\d\.\d).*/g,'$1')/1;
	
var namedColors = new Array('#190707','#2a0a0a','#3b0b0b','#610b0b','#8a0808','#b40404','#df0101','#ff0000','#fe2e2e','#fa5858','#f78181','#f5a9a9','#f6cece',
'#f8e0e0','#fbefef','#191007','#2a1b0a','#3b240b','#61380b','#8a4b08','#b45f04','#df7401','#ff8000','#fe9a2e','#faac58','#f7be81',
'#f5d0a9','#f6e3ce','#f8ece0','#fbf5ef','#181907','#292A0A','#393B0B','#5e610b','#868A08','#AEB404','#D7DF01','#FFFF00','#F7FE2E',
'#F4FA58','#F3F781','#F2F5A9','#F5F6CE','#F7F8E0','#FBFBEF','#101907','#1B2A0A','#243b0b','#38610B','#4B8A08','#5FB404','#74DF00',
'#80FF00','#9AFE2E','#ACFA58','#BEF781','#D0F5A9','#E3F6CE','#ECF8E0','#F5FBEF','#071907','#0A2A0A','#0B3B0B','#0B610B','#088A08',
'#04B404','#01DF01','#00FF00','#2EFE2E','#58FA58','#81F781','#A9F5A9','#CEF6CE','#E0F8E0','#EFFBEF','#071910','#0A2A1B','#0B3B24',
'#0B6138','#088A4B','#04B45F','#01DF74','#00FF80','#2EFE9A','#58FAAC','#81F7BE','#A9F5D0','#CEF6E3','#E0F8EC','#EFFBF5','#071918',
'#0A2A29','#0B3B39','#0B615E','#088A85','#04B4AE','#01DFD7','#00FFFF','#2EFEF7','#58FAF4','#81F7F3','#A9F5F2','#CEF6F5','#E0F8F7',
'#EFFBFB','#071019','#0A1B2A','#0B243B','#0B3861','#084B8A','#045FB4','#0174DF','#0080FF','#2E9AFE','#58ACFA','#81BEF7','#A9D0F5',
'#CEE3F6','#E0ECF8','#EFF5FB','#070719','#0A0A2A','#0B0B3B','#0B0B61','#08088A','#0404B4','#0101DF','#0000FF','#2E2EFE','#5858FA',
'#8181F7','#A9A9F5','#CECEF6','#E0E0F8','#EFEFFB','#100719','#1B0A2A','#240B3B','#380B61','#4B088A','#5F04B4','#7401DF','#8000FF',
'#9A2EFE','#AC58FA','#BE81F7','#D0A9F5','#E3CEF6','#ECE0F8','#F5EFFB','#190718','#2A0A29','#3B0B39','#610B5E','#8A0886','#B404AE',
'#DF01D7','#FF00FF','#FE2EF7','#FA58F4','#F781F3','#F5A9F2','#F6CEF5','#F8E0F7','#FBEFFB','#190710','#2A0A1B','#3B0B24','#610B38',
'#8A084B','#B4045F','#DF0174','#FF0080','#FE2E9A','#FA58AC','#F781BE','#F5A9D0','#F6CEE3','#F8E0EC','#FBEFF5','#000000','#0b0b0b',
'#151515','#1c1c1c','#2e2e2e','#424242','#585858','#6e6e6e','#848484','#a4a4a4','#bdbdbd','#d8d8d8','#e6e6e6','#f2f2f2','#FFFFFF');

 var namedColorRGB = new Array('#190707','#2a0a0a','#3b0b0b','#610b0b','#8a0808','#b40404','#df0101','#ff0000','#fe2e2e','#fa5858','#f78181','#f5a9a9','#f6cece',
'#f8e0e0','#fbefef','#191007','#2a1b0a','#3b240b','#61380b','#8a4b08','#b45f04','#df7401','#ff8000','#fe9a2e','#faac58','#f7be81',
'#f5d0a9','#f6e3ce','#f8ece0','#fbf5ef','#181907','#292A0A','#393B0B','#5e610b','#868A08','#AEB404','#D7DF01','#FFFF00','#F7FE2E',
'#F4FA58','#F3F781','#F2F5A9','#F5F6CE','#F7F8E0','#FBFBEF','#101907','#1B2A0A','#243b0b','#38610B','#4B8A08','#5FB404','#74DF00',
'#80FF00','#9AFE2E','#ACFA58','#BEF781','#D0F5A9','#E3F6CE','#ECF8E0','#F5FBEF','#071907','#0A2A0A','#0B3B0B','#0B610B','#088A08',
'#04B404','#01DF01','#00FF00','#2EFE2E','#58FA58','#81F781','#A9F5A9','#CEF6CE','#E0F8E0','#EFFBEF','#071910','#0A2A1B','#0B3B24',
'#0B6138','#088A4B','#04B45F','#01DF74','#00FF80','#2EFE9A','#58FAAC','#81F7BE','#A9F5D0','#CEF6E3','#E0F8EC','#EFFBF5','#071918',
'#0A2A29','#0B3B39','#0B615E','#088A85','#04B4AE','#01DFD7','#00FFFF','#2EFEF7','#58FAF4','#81F7F3','#A9F5F2','#CEF6F5','#E0F8F7',
'#EFFBFB','#071019','#0A1B2A','#0B243B','#0B3861','#084B8A','#045FB4','#0174DF','#0080FF','#2E9AFE','#58ACFA','#81BEF7','#A9D0F5',
'#CEE3F6','#E0ECF8','#EFF5FB','#070719','#0A0A2A','#0B0B3B','#0B0B61','#08088A','#0404B4','#0101DF','#0000FF','#2E2EFE','#5858FA',
'#8181F7','#A9A9F5','#CECEF6','#E0E0F8','#EFEFFB','#100719','#1B0A2A','#240B3B','#380B61','#4B088A','#5F04B4','#7401DF','#8000FF',
'#9A2EFE','#AC58FA','#BE81F7','#D0A9F5','#E3CEF6','#ECE0F8','#F5EFFB','#190718','#2A0A29','#3B0B39','#610B5E','#8A0886','#B404AE',
'#DF01D7','#FF00FF','#FE2EF7','#FA58F4','#F781F3','#F5A9F2','#F6CEF5','#F8E0F7','#FBEFFB','#190710','#2A0A1B','#3B0B24','#610B38',
'#8A084B','#B4045F','#DF0174','#FF0080','#FE2E9A','#FA58AC','#F781BE','#F5A9D0','#F6CEE3','#F8E0EC','#FBEFF5','#000000','#0b0b0b',
'#151515','#1c1c1c','#2e2e2e','#424242','#585858','#6e6e6e','#848484','#a4a4a4','#bdbdbd','#d8d8d8','#e6e6e6','#f2f2f2','#FFFFFF');

var color_picker_div = false;
var color_picker_active_tab = false;
var color_picker_form_field = false;
var color_picker_active_input = false;

function baseConverter (number,ob,nb) {
	number = number + "";
	number = number.toUpperCase();
	var list = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	var dec = 0;
	for (var i = 0; i <=  number.length; i++) {
		dec += (list.indexOf(number.charAt(i))) * (Math.pow(ob , (number.length - i - 1)));
	}
	number = "";
	var magnitude = Math.floor((Math.log(dec))/(Math.log(nb)));
	for (var i = magnitude; i >= 0; i--) {
		var amount = Math.floor(dec/Math.pow(nb,i));
		number = number + list.charAt(amount); 
		dec -= amount*(Math.pow(nb,i));
	}
	if(number.length==0)number=0;
	return number;
}

function colorPickerGetTopPos(inputObj)
{
	
  var returnValue = inputObj.offsetTop;
  while((inputObj = inputObj.offsetParent) != null){
  	returnValue += inputObj.offsetTop;
  }
  return returnValue;
}

function colorPickerGetLeftPos(inputObj)
{
  var returnValue = inputObj.offsetLeft;
  while((inputObj = inputObj.offsetParent) != null)returnValue += inputObj.offsetLeft;
  return returnValue;
}

function cancelColorPickerEvent(){
	return false;
}

function showHideColorOptions()
{
	var parentNode = this.parentNode;
	var subDiv = parentNode.getElementsByTagName('DIV')[0];
	counter=0;		
	var contentDiv = document.getElementById('color_picker_content').getElementsByTagName('DIV')[0];
	do{			
		if(subDiv.tagName=='DIV' && subDiv.className!='colorPickerCloseButton'){
			if(subDiv==this){
				this.className='colorPickerTab_active';
				this.style.zIndex = 50;
				var img = this.getElementsByTagName('IMG')[0];
				img.src = "images/tab_right_active.gif"
				img.src = img.src.replace(/inactive/,'active');							
				contentDiv.style.display='block';
				self.status = counter;					
			}else{
				subDiv.className = 'colorPickerTab_inactive';	
				var img = subDiv.getElementsByTagName('IMG')[0];
				img.src = "images/tab_right_inactive.gif"
				self.status = img.src;
				subDiv.style.zIndex = 10 - counter;
				contentDiv.style.display='none';
			}
			counter++;
		}
		subDiv = subDiv.nextSibling;
		contentDiv = contentDiv.nextSibling;
	}while(subDiv);
	
	document.getElementById('colorPicker_statusBarTxt').innerHTML = ' ';


}

function createColorPickerTopRow(inputObj){
	var tabs = ['Colores'];
	var tabWidths = [90,70];
	var div = document.createElement('DIV');
	div.className='colorPicker_topRow';

	inputObj.appendChild(div);	
	var currentWidth = 0;
	for(var no=0;no<tabs.length;no++){			
		
		var tabDiv = document.createElement('DIV');
		tabDiv.onselectstart = cancelColorPickerEvent;
		tabDiv.ondragstart = cancelColorPickerEvent;
		if(no==0){
			suffix = 'active'; 
			color_picker_active_tab = this;
		}else suffix = 'inactive';
		
		tabDiv.id = 'colorPickerTab' + no;
		tabDiv.onclick = showHideColorOptions;
		if(no==0)tabDiv.style.zIndex = 50; else tabDiv.style.zIndex = 1 + (tabs.length-no);
		tabDiv.style.left = currentWidth + 'px';
		tabDiv.style.position = 'absolute';
		tabDiv.className='colorPickerTab_' + suffix;
		var tabSpan = document.createElement('SPAN');
		tabSpan.innerHTML = tabs[no];
		tabDiv.appendChild(tabSpan);
		var tabImg = document.createElement('IMG');
		tabImg.src = "images/tab_right_" + suffix + ".gif";
		tabDiv.appendChild(tabImg);
		if(navigatorVersion<6 && MSIE){	/* Lower IE version fix */
			tabSpan.style.position = 'relative';
			tabImg.style.position = 'relative';
			tabImg.style.left = '-3px';		
			tabDiv.style.cursor = 'hand';	
		}			
		div.appendChild(tabDiv);
		currentWidth = currentWidth + tabWidths[no];
	
	}
	
	var closeButton = document.createElement('DIV');
	closeButton.className='colorPickerCloseButton';
	closeButton.innerHTML = 'x';
	closeButton.onclick = closeColorPicker;
	closeButton.onmouseover = toggleCloseButton;
	closeButton.onmouseout = toggleOffCloseButton;
	div.appendChild(closeButton);
	
}

function toggleCloseButton()
{
	this.style.color='#FFF';
	this.style.backgroundColor = '#317082';	
}
function toggleOffCloseButton()
{
	this.style.color='';
	this.style.backgroundColor = '';			
	
}
function closeColorPicker()
{
	color_picker_div.style.display='none';
}
function createWebColors(inputObj){
	var webColorDiv = document.createElement('DIV');
	inputObj.appendChild(webColorDiv);
	for(var r=15;r>=0;r-=3){
		for(var g=0;g<=15;g+=3){
			for(var b=0;b<=15;b+=3){
				var red = baseConverter(r,10,16) + '';
				var green = baseConverter(g,10,16) + '';
				var blue = baseConverter(b,10,16) + '';
				
				var color = '#' + red + red + green + green + blue + blue;
				var div = document.createElement('DIV');
				div.style.backgroundColor=color;
				div.innerHTML = '<span></span>';
				div.className='colorSquare';
				div.title = color;	
				div.onclick = chooseColor;
				div.setAttribute('rgbColor',color);
				div.onmouseover = colorPickerShowStatusBarText;
				div.onmouseout = colorPickerHideStatusBarText;
				webColorDiv.appendChild(div);
			}
		}
	}
}
	
function createNamedColors(inputObj){
	var namedColorDiv = document.createElement('DIV');
	namedColorDiv.style.display='block';
	inputObj.appendChild(namedColorDiv);
	for(var no=0;no<namedColors.length;no++){
		var color = namedColorRGB[no];
		var div = document.createElement('DIV');
		div.style.backgroundColor=color;
		div.innerHTML = '<span></span>';
		div.className='colorSquare';
		div.title = namedColors[no];	
		div.onclick = chooseColor;
		div.onmouseover = colorPickerShowStatusBarText;
		div.onmouseout = colorPickerHideStatusBarText;
		div.setAttribute('rgbColor',color);
		namedColorDiv.appendChild(div);				
	}		
}

function colorPickerHideStatusBarText()
{
	document.getElementById('colorPicker_statusBarTxt').innerHTML = ' ';
}

function colorPickerShowStatusBarText()
{
	var txt = this.getAttribute('rgbColor');
	if(this.title.indexOf('#')<0)txt = txt + "";
	document.getElementById('colorPicker_statusBarTxt').innerHTML = txt;	
}

function createAllColorDiv(inputObj){
	var namedColorDiv = document.createElement('DIV');
	namedColorDiv.style.display='none';
	inputObj.appendChild(namedColorDiv);	
}

function chooseColor()
{
	color_picker_form_field.value = this.getAttribute('rgbColor');
	color_picker_div.style.display='none';
}

function createStatusBar(inputObj)
{
	var div = document.createElement('DIV');
	div.className='colorPicker_statusBar';	
	var innerSpan = document.createElement('SPAN');
	innerSpan.id = 'colorPicker_statusBarTxt';
	div.appendChild(innerSpan);
	inputObj.appendChild(div);
}

function showColorPicker(inputObj)
{
	if(!color_picker_div){
		color_picker_div = document.createElement('DIV');
		color_picker_div.id = 'dhtmlgoodies_colorPicker';
		color_picker_div.style.display='none';
		createColorPickerTopRow(color_picker_div);
		
		var contentDiv = document.createElement('DIV');
		contentDiv.id = 'color_picker_content';
		color_picker_div.appendChild(contentDiv);
		
		//createWebColors(contentDiv);
		createNamedColors(contentDiv);
		createAllColorDiv(contentDiv);
		createStatusBar(color_picker_div);
		document.body.appendChild(color_picker_div);
	}		
	if(color_picker_div.style.display=='none' || color_picker_active_input!=inputObj)color_picker_div.style.display='block'; else color_picker_div.style.display='none';		
	color_picker_div.style.left = colorPickerGetLeftPos(inputObj) + 'px';
	color_picker_div.style.top = colorPickerGetTopPos(inputObj) + inputObj.offsetHeight + 2 + 'px';
	color_picker_form_field = inputObj;
	color_picker_active_input = inputObj;
	
}