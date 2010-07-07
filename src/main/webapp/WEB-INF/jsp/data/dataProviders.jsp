<%@ include file="/common/taglibs.jsp"%>
<%
/*
 * Dataprovider page
 *
 * @author "Tommy Wang <tommy.wang@csiro.au>"
*/
%><%@ page contentType="text/html" pageEncoding="UTF-8" %>
<h2><c:out value="${dataProvider.name}" /></h2>
<c:if test="${not empty dataResources}">
	<c:if test="${dataProvider.description!=null}">	
	<br />
	<h3>Data Provider Description</h3>
	<div><c:out value="${dataProvider['description']}" /></div><br />
	</c:if>
	<table>
		<tr>
			<th><b>Resource ID</b></th>
			<th><b>Resource Name</b></th>
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
