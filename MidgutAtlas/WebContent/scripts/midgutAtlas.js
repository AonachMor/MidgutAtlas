				/* MidgutAtlas.js 01.02.2016 */
				
// Writes start of (hidden) form setting values of options
function startForm()
{	
	var form = document.createElement("form");
	form.setAttribute("method", "get");
	form.setAttribute("action", "");
	form.setAttribute("accept-charset", "UTF-8");
	
		// No. of hits to display if relevant
	if(document.getElementById('maxdisplayed'))
	{
		var maxdisplayed = document.getElementById('maxdisplayed').value;
		var hiddenMaxDisplayedField = document.createElement("input");
		hiddenMaxDisplayedField.setAttribute("type", "hidden");
		hiddenMaxDisplayedField.setAttribute("name", "maxdisplayed");
		hiddenMaxDisplayedField.setAttribute("value", maxdisplayed);
		form.appendChild(hiddenMaxDisplayedField);
	}
		
	return form;
}

		// Create hidden submission forms for different queries and appropriate pages //
		
function sendSearchGeneForm() 
{
	var gene = document.getElementById('gene').value;
	// idtype handled separately because of smart check of gene value

	if(gene=="")
	{
		alert("Please enter a gene identifier");
	}
	else
	{ 
		var form = startForm();		
			// identifier field for gene search form (will be null at start)
		var hiddenSearchField = document.createElement("input");
		hiddenSearchField.setAttribute("type", "hidden");
		hiddenSearchField.setAttribute("name", "search");
		hiddenSearchField.setAttribute("value", "gene");		
		form.appendChild(hiddenSearchField);		
			// gene id etc (from and for) gene text field
		var hiddenGeneField = document.createElement("input");	
		hiddenGeneField.setAttribute("type", "hidden");
		hiddenGeneField.setAttribute("name", "gene");
		hiddenGeneField.setAttribute("value", gene);		
		form.appendChild(hiddenGeneField);		
			// idtype from (and for) radio button choice
		var idtype = getIDType();
		var hiddenIDField = document.createElement("input");	
		hiddenIDField.setAttribute("type", "hidden");
		hiddenIDField.setAttribute("name", "idtype");
		hiddenIDField.setAttribute("value", idtype);	
		form.appendChild(hiddenIDField);
				
		document.body.appendChild(form);
		form.submit();
	}
}

function sendSearchTopForm()	// no text capture here
{
	var form = startForm();
		// identifier field for top search form
	var hiddenSearchField = document.createElement("input");
	hiddenSearchField.setAttribute("type", "hidden");
	hiddenSearchField.setAttribute("name", "search");
	hiddenSearchField.setAttribute("value", "top");
	form.appendChild(hiddenSearchField);
		// tissue ID 
	var hiddenTissueField = document.createElement("input");
	var tissue = document.getElementById('tissue').value;
	hiddenTissueField.setAttribute("type", "hidden");
	hiddenTissueField.setAttribute("name", "tissue");
	hiddenTissueField.setAttribute("value", tissue);
	form.appendChild(hiddenTissueField);
		// GorT term (gene/transcript)
	var hiddenGorTField = document.createElement("input");
	var GorT = document.getElementById('GorT').value;
	hiddenGorTField.setAttribute("type", "hidden");
	hiddenGorTField.setAttribute("name", "GorT");
	hiddenGorTField.setAttribute("value", GorT);
	form.appendChild(hiddenGorTField);
	// Max No. of results to display (maxdisplayed)
	var hiddenMaxField = document.createElement("input");
	var maxdisplayed = document.getElementById('maxdisplayed').value;
	hiddenMaxField.setAttribute("type", "hidden");
	hiddenMaxField.setAttribute("name", "maxdisplayed");
	hiddenMaxField.setAttribute("value", maxdisplayed);
	form.appendChild(hiddenMaxField);


	document.body.appendChild(form);
	form.submit();
}

		// Create hidden submission forms for links to different sections //
// repeated stuff
function startToForm()
{	
	var form = document.createElement("form");
	form.setAttribute("method", "get");
	form.setAttribute("action", "");
	form.setAttribute("accept-charset", "UTF-8");			
	return form;
}

function toGeneForm() 
{
	var form = startToForm();
	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "hidden");
	hiddenField.setAttribute("name", "page");	//
	hiddenField.setAttribute("value", "gene");	// gene page
	form.appendChild(hiddenField);
	document.body.appendChild(form);
	form.submit();
}

function toTopForm() 
{
	var form = startToForm();
	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "hidden");
	hiddenField.setAttribute("name", "page");
	hiddenField.setAttribute("value", "top");		// top page
	form.appendChild(hiddenField);
	document.body.appendChild(form);
	form.submit();
}

function toHomeForm() 
{
	var form = startToForm();
	var hiddenField = document.createElement("input");
	hiddenField.setAttribute("type", "hidden");
	hiddenField.setAttribute("name", "page");
	hiddenField.setAttribute("value", "home");		// home page
	form.appendChild(hiddenField);
	document.body.appendChild(form);
	form.submit();
}


// Smart check for correct idtype choice
// determines name of gene and auto-assigns FBgn or CG choices to fbgn and cgnum, resp.
function getIDType()
{
	var idtype;		// idtype choice
	var input = document.getElementById('gene').value;

	if(input.substring(0,4) == "FBgn")
	{
		idtype = "fbgn";
	}
	else if(input.substring(0,2) == "CG")
	{
		idtype = "cgnum";
	}
	else if(document.getElementById('symbol').checked)
	{
		idtype = "symbol";
	}
	else if(document.getElementById('name').checked)
	{
		idtype = "name";
	}
	else if(document.getElementById('cgnum').checked)
	{
		idtype = "cgnum";
	}
	else
	{
		idtype = "fbgn";
	}	
	return idtype;
}

// Submit forms by hitting 'enter' key (code 13)
function geneKey(e)
{
	if (e.keyCode == 13) 
	{
		sendSearchGeneForm();
	}
}
function topKey(e)
{
	if (e.keyCode == 13)
	{
		sendSearchTopForm();
	}
}

//createLink (called in 'onload'): creates *single* hide/show link w. text changing in concert
//after Craig Sailsa and many others

	var defLinkText = "  show";	// default link text
	var altLinkText = "  hide";	// alternative link text
		// check the following ids are actually unique!
	var visDivID = "visible";		// id of div with vis text to which link ele is added
	var hidDivID = "hideme";		// id of div with hide/show text
	var linkID = "expand";			// id for link - generated by js
	
	var visDivID2 = "visible2";		// id of div with vis text to which link ele is added
	var hidDivID2 = "hideme2";		// id of div with hide/show text
	var linkID2 = "expand2";			// id for link - generated by js
	
	// creates link on line with vis text if there is div with hidden text
	function createLink()
	{
		if(document.getElementById(visDivID) && document.getElementById(hidDivID))
		{
			var visDiv = document.getElementById(visDivID);		// div to add link ele to
			var hidDiv = document.getElementById(hidDivID);		// div to hide/show
			
				// create 'a' element with js link to hideShow function and append to visible div
			var hsLink = document.createElement("a");
			hsLink.id = linkID;	// provide link with id to ref for text change 
				// construct the ahref as the js hideShow()
			hsLink.href = "javascript:hideShow('" + hsLink.id + "','" + hidDiv.id + "');";
				// add linked text to element and add element to div
			hsLink.appendChild(document.createTextNode(defLinkText));
			visDiv.appendChild(hsLink);
		}
	}
	
		// creates 2nd link on line with vis text if there is div with hidden text
	function createLink2()
	{
		if(document.getElementById(visDivID2) && document.getElementById(hidDivID2))
		{
			var visDiv = document.getElementById(visDivID2);		// div to add link ele to
			var hidDiv = document.getElementById(hidDivID2);		// div to hide/show
			
				// create 'a' element with js link to hideShow function and append to visible div
			var hsLink = document.createElement("a");
			hsLink.id = linkID2;	// provide link with id to ref for text change 
				// construct the ahref as the js hideShow()
			hsLink.href = "javascript:hideShow('" + hsLink.id + "','" + hidDiv.id + "');";
				// add linked text to element and add element to div
			hsLink.appendChild(document.createTextNode(defLinkText));
			visDiv.appendChild(hsLink);
		}
	}
	
	// takes ids of link element and hide/show target div to do hide/show and text change
	function hideShow(link, target)
	{
			// does the hide/show stuff on the target
		theStyle = document.getElementById(target).style;
			//var text;	// name of link
		if (theStyle.display == "block")
		{
			theStyle.display = "none";
			newText = defLinkText;
		}
		else
		{
			theStyle.display = "block";
			newText = altLinkText;
		}	
			// get the link element and change its text
		var linkEle = document.getElementById(link);
		linkEle.replaceChild(document.createTextNode(newText), linkEle.firstChild);
	}
	
	// Sends SVG to Servlet SVGreflector for returning as downloadable file
	function sendSVG() 
	{
		var svgText = document.getElementById('svg').innerHTML;			// id of div holding SVG
		var geneID = document.getElementById('graphID').textContent;		// id of span holding unique name
		
		var form = document.createElement("form");
		form.setAttribute("method", "post");
		form.setAttribute("action", "/SVGreflector/image.svg");
		form.setAttribute("accept-charset", "UTF-8");	

		var hiddenSVGField = document.createElement("input");	
		hiddenSVGField.setAttribute("type", "hidden");
		hiddenSVGField.setAttribute("name", "svgText");
		hiddenSVGField.setAttribute("value", svgText);		
		form.appendChild(hiddenSVGField);
		
		var hiddenNameField = document.createElement("input");	
		hiddenNameField.setAttribute("type", "hidden");
		hiddenNameField.setAttribute("name", "graphName");
		hiddenNameField.setAttribute("value", geneID);		
		form.appendChild(hiddenNameField);
		
		document.body.appendChild(form);
		form.submit();
	}
