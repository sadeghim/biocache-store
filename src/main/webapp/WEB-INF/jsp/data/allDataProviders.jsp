<%@ include file="/common/taglibs.jsp"%>
<%
/*
 * accordion page
 *
 * @author "Tommy Wang <tommy.wang@csiro.au>"
*/
%><%@ page contentType="text/html" pageEncoding="UTF-8"%>

<script type="text/javascript">
$(document).ready(function() {
    var facetLinksSize = $("ul#subnavlist li").size();

    if (facetLinksSize == 0) {
        // Hide an empty facet link list
        $("#facetBar > h4").hide();
        $("#facetBar #navlist").hide();
    }
    /* accordion widget */
    var icons = {
        header: "ui-icon-circle-arrow-e",
        headerSelected: "ui-icon-circle-arrow-s"
    };

    $("#accordion").accordion({
        icons: icons,
        autoHeight: false
    });

    $("#toggle").button().toggle(function() {
        $("#accordion").accordion("option", "icons", false);
    }, function() {
        $("#accordion").accordion("option", "icons", icons);
    });

    $("select#sort").change(function() {
        var val = $("option:selected", this).val();
        reloadWithParam('sort',val);
    });
    $("select#dir").change(function() {
        var val = $("option:selected", this).val();
        reloadWithParam('dir',val);
    });

    $("#searchButtons > button").button();
    $("#searchButtons > button#download").click(function() {
        var downloadUrl = "${pageContext.request.contextPath}/occurrences/download?q=${query}&fq=${fn:join(facetQuery, '&fq=')}";
        //alert("URL is "+downloadUrl);
        if (confirm("Continue with download?\rClick 'OK' to download or 'cancel' to abort.")) {
            window.location.replace(downloadUrl);
        }
    });

    $('button#showMap').click(function (e) {
        window.location.replace("#searchResults");
        $("#pointsMap").show();
        loadMap();
        $('#pointsMap').modal();

    });

    // more/fewer search option links
    $("#refineMore a").click(function(e) {
        e.preventDefault();
        $("#accordion").slideDown();
        $("#refineLess").show('slow');
        $("#refineMore").hide('slow');
    });
    $("#refineLess a").click(function(e) {
        e.preventDefault();
        $("#accordion").slideUp();
        $("#refineLess").hide('slow');
        $("#refineMore").show('slow');
    });

});
	
</script>
<h2>All Data Providers</h2>
<div id="facets" style="width:100%;">
	<div id="accordion"  style="display:inline;">
<c:if test="${dataProviders!=null}">
	
		<c:forEach var="dataProvider" items="${dataProviders}">
			
				<a href="#" style="border:0;">
				<h3 style="margin-left:30px;"><b><c:out value="${dataProvider['name']}" /></b></h3>
				</a>

				
			<div id="subnavlist" style="border:0; padding-left: 20px;">
			<ul>
			<c:forEach var="dataResource" items="${dataResources}">
				<c:if test="${dataResource['dataProviderId']==dataProvider['id']}">
				
							<li><a href="../occurrences/searchByDataResourceId?q=<c:out value="${dataResource['id']}" />">${dataResource['name']}</a></li>
			
				</c:if>
			</c:forEach>
		</ul>
		</div>
		</c:forEach>
	
</c:if>
</div>
</div>
