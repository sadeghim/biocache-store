<%@ include file="/common/taglibs.jsp"%>
<%
	/*
	 * Dataresource page
	 *
	 * @author "Tommy Wang <tommy.wang@csiro.au>"
	 */
%><%@ page contentType="text/html" pageEncoding="UTF-8"%>
<h2><c:out value="${dataResource.name}" /></h2>
<c:if test="${dataResource.description!=null && not empty dataResource.description}">
	<br />
	<b>Data Resource Description</b>
	<div><c:out value="${dataResource['description']}" /></div>
	<br />
</c:if>
	<br />
	<b>Data Provider Name</b>
	<div><c:out value="${dataProvider['name']}" /></div>
	<br />
<table>
	<tr>
		<th><b>Resource Name</b></th>
		<th><b>Number of Records</b></th>
		<th><b>Basis of Record</b></th>
	</tr>
	<tr>
		<td><a href="../occurrences/searchByDataResourceId?q=<c:out value="${dataResource['id']}" />"><c:out value="${dataResource['name']}" /></a></td>
		<td><c:out value="${dataResource['providerRecordCount']}" /></td>
		<td><c:out value="${dataResource['basisOfRecord']}" /></td>

	</tr>
</table>
