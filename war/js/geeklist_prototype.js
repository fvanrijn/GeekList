/*
	document.onload: builds the list of lists
*/
document.observe('dom:loaded', function() {
	// get lists from server
	new Ajax.Request('/list', {
	  	method:'get',
		onSuccess: function(d){
			// create ol to show lists
	     	var oJSON = d.responseText.evalJSON();
			var oList = new Element('ul');
			oJSON.lists.each(function(sItem) {
				var oListItem = new Element('li', { 'class' : 'list' }).update(sItem);
				oListItem.observe('click', function(event) {
					// name of the list clicked
					var sListName = event.target.innerHTML;
					refreshItemList(sListName);
				}); 
				
				oList.appendChild(oListItem);
			});

			// display on div
		 	$("list_overview").appendChild(oList);
		}
	});
});

	
/*
	Updates the list_detail div with the content of list sListName
*/
function refreshItemList(sListName) {
	// get listitems
	new Ajax.Request('/list/'+sListName, {
	  	method:'get',
		onSuccess: function(d){
			// clear div
			$("list_detail").childElements().each(function(li) {
				li.remove();
			});
			
	     	var oJSON = d.responseText.evalJSON();
	     	// create new itemlist
			var oList = new Element('ol');
			
			oJSON.items.each(function(oItem) {
				// construct the listitem
				var oListItem = new Element('li', { 'class': 'list_item' });
				
				// clickable title for the listitem
				var oTitleDiv = new Element('div', { 'class': 'title' }).update(oItem.title + "(" +oItem.voteCount+ " votes)");
				oTitleDiv.observe('click', function(event) {
					new Ajax.Request('/vote/'+sListName+'/'+oItem.title, {
					  	method:'get',
						onSuccess: function(d){
							alert("vote cast!");
							refreshItemList(sListName);
						},
						onFailure: function(d) {
							// response.status != 2xx. The servlet the return a compact error in the statusText
							alert(d.statusText);
						}
					});
				});

				// div contains author and url of the listitem
				var oMetaDiv  = new Element('div', {  'class': 'meta' }).update(oItem.author + " // ");
				oMetaDiv.appendChild(new Element('a', { 'href': oItem.url, 'target': '_blank' }).update(oItem.url));

				// add it all together and append to the list
				oListItem.appendChild(oTitleDiv);
				oListItem.appendChild(oMetaDiv);
				oList.appendChild(oListItem);
			});
			
			// display newly made list on div
		 	$("list_detail").appendChild(oList);
		}
	});
}


