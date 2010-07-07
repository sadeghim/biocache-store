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
	<h3>Data Resource Description</h3>
	<div><c:out value="${dataResource['description']}" /></div>
	<br />
</c:if>
	<br />
	<h3>Data Provider Name</h3>
	<div><c:out value="${dataProvider['name']}" /></div>
	<br />
<table>
	<tr>
		<th><b>Resource ID</b></th>
		<th><b>Resource Name</b></th>
	</tr>
	<tr>
		<td><c:out value="${dataResource['id']}" /></td>
		<c:choose>
			<c:when test="${dataResource['websiteUrl']!=null}">
				<td><a href="<c:out value="${dataResource['websiteUrl']}" />"><c:out
					value="${dataResource['name']}" /></a></td>
			</c:when>
			<c:otherwise>
				<td><c:out value="${dataResource['name']}" /></td>
			</c:otherwise>
		</c:choose>

	</tr>
</table>
