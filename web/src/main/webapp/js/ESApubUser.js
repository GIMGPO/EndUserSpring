window.onload = function() {
	adjustHeight();
	if (document.getElementById('deliveryInfo')) {
		// 10/04/2013 - Bug Fix for activating the Finish button on the delivery page
		// activateBut('butNext','deliveryInfo');
		document.getElementById('userEmail').select();		
	}
}


//The language dropdown with jQuery

var timeout         = 500;
var closetimer		= 0;
var ddmenuitem      = 0;

function jsddm_open()
{	jsddm_canceltimer();
	jsddm_close();
	ddmenuitem = $(this).find('ul').eq(0).css('visibility', 'visible');}

function jsddm_close()
{	if(ddmenuitem) ddmenuitem.css('visibility', 'hidden');}

function jsddm_timer()
{	closetimer = window.setTimeout(jsddm_close, timeout);}

function jsddm_canceltimer()
{	if(closetimer)
	{	window.clearTimeout(closetimer);
		closetimer = null;}}

$(document).ready(function()
{	$('#jsddm > li').bind('mouseover', jsddm_open);
	$('#jsddm > li').bind('mouseout',  jsddm_timer);
});

document.onclick = jsddm_close;

//end of the language dropdown

function popupInstructions(urlToOpen)  {  
	var window_width = 600;  
	var window_height = 450;  
	var window_left = (screen.availWidth/2)-(window_width/2);  
	var window_top = (screen.availHeight/2)-(window_height/2);  
	var winParms = "Status=yes" + ",resizable=no" + ",height="+window_height+",width="+window_width + ",left="+window_left+",top="+window_top;  
	var newwindow = window.open(urlToOpen,'_blank',winParms);  
	newwindow.focus(); 
}



function adjustHeight() {
	var legendHeight = 498;
	var rightSideHeight = 514;
	if (document.getElementById('Legend'))
		legendHeight = document.getElementById('Legend').offsetHeight;
	if (document.getElementById('ConAndBut')) {
		rightSideHeight = document.getElementById('ConAndBut').offsetHeight;
		if (rightSideHeight < legendHeight) {
			if (navigator.userAgent && navigator.userAgent.indexOf("MSIE") != -1) {
				document.getElementById('ConAndBut').style.setAttribute('height', legendHeight + 'px');
			} else if (navigator.userAgent.indexOf("Firefox") != -1) {
				document.getElementById('ConAndBut').style['height'] = legendHeight + 'px';
			}
		}
	}
}

function alltrim(str) {
    return str.replace(/^\s+|\s+$/g, '');
}

function validate(form_id,email) {
	var address = document.forms[form_id].elements[email].value;
	var emails = address.split(";");
	var reg = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
	var k=0;
	for (var i=0; i<emails.length; i++) {
	   emails[i] = alltrim(emails[i]);
	   if(reg.test(emails[i]) == true) k++;
	}
	
	if (k != emails.length) {
		if((form_id != "missingStaticContent" || form_id != "feedback")  && email != "") {
			alert("Please enter valid e-mail addresses.");
			return false;
		}
	} else
	return true;
}

//to be used with <select> only. 
function activateBut(butId,formName) {
	var full = false;
	var selects = document.forms[formName].getElementsByTagName("select");
	if (selects.length > 0) {
		for (var i=0; i< selects.length; i++){
			if (selects[i].value) {
				full = true;
			} else {
				full = false;
			}
		}
	}
	var el = document.getElementById(butId);
	if ((full == true) && (el.className == "ESAbutInactive")) {
		el.className = "ESAbutActive";
		el.setAttribute("href", "javascript:submitform('" + formName + "')");
	}
	else if (full == false){		
		document.getElementById(butId).className = "ESAbutInactive";
		document.getElementById(butId).setAttribute("href","#");
	}
}

function submitform(name) {
	if (name=='uploadPDF') {
		document.getElementById("progress").style.display = 'block';
		document.forms['uploadPDF'].submit();
	} else
	if (name=='deliveryInfo') {
		if (validate('deliveryInfo','userEmail')!= false) {
			document.forms['deliveryInfo'].submit();
		}
	} else
	document.forms[name].submit();
}


function resetform(name) {
	if (name == "profilingAttr") {
		inputs = document.getElementsByTagName('input');
		firstName = inputs[0].name;
		inputs[0].removeAttribute("disabled");
		for (var i=1; i<inputs.length; i++) {
			if ((inputs[i].name != firstName) &&
				(inputs[i].name != "prof") &&
				(inputs[i].name != "what")) {
				inputs[i].setAttribute("disabled","disabled");
			} else {
				inputs[i].removeAttribute("disabled");
			}
		}
	}
	if(name == "mapsAndLangs") {
		var selects = document.forms[name].getElementsByTagName("select");
		if (selects.length > 0) {
			var options = document.forms[name].getElementsByTagName("option");
			if (options.length == 1) {
			// Do nothing
			} 
			else {
				document.getElementById("butNext").className = "ESAbutInactive";
				document.getElementById("butNext").setAttribute("href","#");
			}
		}
	}
	else if (name != "deliveryInfo") {
		document.getElementById("butNext").className = "ESAbutInactive";
		document.getElementById("butNext").setAttribute("href","#");
	}
	document.forms[name].reset();
}

/*
 * Function called from the <input type=checkbox> in the DISPLAY QUESTIONS (PROFILES) page
 * The call is from "onClick" attribute, the attribute "id" also should be present. The format 
 * of the attribute is "id='multi:target', where 'multi" is the profile.value multi-select setting,
 * the values could be 
 * 		false(single-select)
 * 		true(allow multi-select in the next group)
 * 		null(the last group)), 
 * and "target" is the name of the next profile
 */
function onBoxSelected(inputObj) {
	var currentName = inputObj.getAttribute("name");
	// current group settings
	var conf = inputObj.getAttribute("id").split(":");
	var targetName = conf[1];
	var mutli = "false";
	// "parent' here is a previous group, parentConf is multi:target for previous group
	var parentConf;
	var parentName = null;
	var id;
	var i;
	var allInputs = document.getElementsByTagName("input");

	for (i = 0; i < allInputs.length; i++) {
		id = allInputs[i].getAttribute("id");
		if(id == null)
			continue;
		parentConf =  id.split(":");
		if(allInputs[i].checked && currentName == parentConf[1]) {
			parentName = allInputs[i].getAttribute("name");
			mutli = parentConf[0];
			break;
		}
	}
	
	if(mutli == "false") {
		for (i = 0; i < allInputs.length; i++) {
			if(allInputs[i] == inputObj) //skip current
				continue;
			if(allInputs[i].getAttribute("name") == currentName)
				allInputs[i].checked = false;
		}
	}
	
	if(parentName != null) { //has parent
		var isAvailable = false;
		var values = depend[inputObj.getAttribute("value")];
		var value;
		if(values != null) {
			for (i = 0; i < allInputs.length && !isAvailable; i++) {
				if(!allInputs[i].checked || allInputs[i].getAttribute("name") != parentName)
					continue;
				//TODO 'OR', 'AND' logic to be implemented here
				value = allInputs[i].getAttribute("value");
				for(var k in values) {
					if(values[k] == value) {
						isAvailable = true;
						break;
					}
				}
			}
		}
		
		inputObj.setAttribute("disabled","disabled");
		if(isAvailable)
			inputObj.removeAttribute("disabled");
			
	}
	
	for (i = 0; i < allInputs.length; i++) {
		if(allInputs[i].getAttribute("name") != targetName)
			continue;
		allInputs[i].checked = false;
		allInputs[i].setAttribute("disabled","disabled"); // disable all target check-boxes, when all of the source check-box are unselected
		onBoxSelected(allInputs[i]);
	}


	var countFinalChecked = 0;
	// NS: Bug fix for activation of the Next button
	// Unfortunately, it is an additional loop. It should be here
	for (i = 0; i < allInputs.length; i++) {
		if(allInputs[i].checked && allInputs[i].getAttribute("id") == "null:null")
			countFinalChecked++;
	}
	
	var el = document.getElementById("butNext");
	el.className = "ESAbutInactive";
	el.setAttribute("href", "#");
	if(countFinalChecked > 0) {
			el.className = "ESAbutActive";
			el.setAttribute("href", "javascript:submitform('profilingAttr')");
	}
}
		
function changeLang(message,url) {
	if(confirm(message)) location.href = url;	
}