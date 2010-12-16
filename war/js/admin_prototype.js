/*
	document.onload: populate the listname selectboxes and initialize datebox
*/
document.observe('dom:loaded', function() {
	// fetch listnames and add them to listname-selects
	new Ajax.Request('/list', {
	  	method:'get',
		onSuccess: function(d){
			var oJSON = d.responseText.evalJSON();
			oJSON.lists.each(function(sItem) {
				$$('select.list_select').each(function(oSelect) {
					oSelect.appendChild(new Element('option').update(sItem));
				});
			});
		}
	});
	
	// initialize datebox
	var d = new Date();
	$('dateClosed').value = d.getFullYear() + "-" + (d.getMonth()+1) + "-" + d.getDate();
});