<%@ include file="/common/taglibs.jsp"%>
<%
/*
 * Dataprovider page
 *
 * @author "Tommy Wang <tommy.wang@csiro.au>"
*/
%><%@ page contentType="text/html" pageEncoding="UTF-8" %>
<h2><a href="../occurrences/searchByDataProviderId?q=<c:out value="${dataProvider['id']}" />"><c:out value="${dataProvider['name']}" /></a></h2>
<c:if test="${not empty dataResources}">
	<c:if test="${dataProvider.description!=null}">	
	<br />
	<b>Data Provider Description</b>
	<div><c:out value="${dataProvider['description']}" /></div><br />
	</c:if>
	<table>
		<tr>
			<th><b>Resource Name</b></th>
			<th><b>Number of Records</b></th>
			<th><b>Basis of Record</b></th>
		</tr>
		<c:forEach var="dataResource" items="${dataResources}">
			<tr>
				<td><a href="../occurrences/searchByDataResourceId?q=<c:out value="${dataResource['id']}" />"><c:out value="${dataResource['name']}" /></a></td>
				<td><c:out value="${dataResource['providerRecordCount']}" /></td>
				<td>
					<c:forEach var="basisOfRecord" items="${basisOfRecords}">
						<c:if test="${dataResource['basisOfRecord']==basisOfRecord['id']}">
							<c:out value="${basisOfRecord['description']}" />
						</c:if>							
					</c:forEach>
				</td>
			</tr>
		</c:forEach>
	</table>
</c:if>
