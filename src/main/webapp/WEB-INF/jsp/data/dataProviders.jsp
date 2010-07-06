<%@ include file="/common/taglibs.jsp"%>
<%
/*
 * Dataprovider page
 *
 * @author "Tommy Wang <tommy.wang@csiro.au>"
*/
%><%@ page contentType="text/html" pageEncoding="UTF-8" %>
<h1><c:out value="${dataProvider.name}" /></h1>
<c:if test="${not empty dataResources}">
	<c:if test="${dataProvider.description!=null}">	
	<br />
	<h3>Data Provider Description</h3>
	<div><c:out value="${dataProvider['description']}" /></div><br />
	</c:if>
	<table>
		<tr>
			<th>Resource ID</th>
			<th>Resource Name</th>
		</tr>
		<c:forEach var="dataResource" items="${dataResources}">
			<tr>
				<td><c:out value="${dataResource['id']}" /></td>
				<c:choose>
				<c:when test="${dataResource['websiteUrl']!=null}">
					<td><a href="<c:out value="${dataResource['websiteUrl']}" />"><c:out value="${dataResource['name']}" /></a></td>
				</c:when>
				<c:otherwise>
					<td><c:out value="${dataResource['name']}" /></td>
				</c:otherwise>
				</c:choose>
			</tr>
		</c:forEach>
	</table>
</c:if>
