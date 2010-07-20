<%@ include file="/common/taglibs.jsp"%>
<%
        /*
         * Dataprovider page
         *
         * @author "Tommy Wang <tommy.wang@csiro.au>"
         */
%><%@ page contentType="text/html" pageEncoding="UTF-8" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="pageName" content="home"/>
        <title>Atlas of Living Australia - Data Provider: ${dataProvider.name}</title>
    </head>
    <body>
        <div id="breadcrumb">
            <a href="http://test.ala.org.au">Home</a>
            <a href="http://test.ala.org.au/explore">Explore</a>
            <a href="${pageContext.request.contextPath}/data_provider/data_providers">Data Providers</a>
            <c:out value="${dataProvider.name}" />
        </div>
        <div id="decoratorBody">
            <h2><a href="../occurrences/searchByDataProviderId?q=<c:out value="${dataProvider['id']}" />"><c:out value="${dataProvider['name']}" /></a></h2>
            <c:if test="${not empty dataResources}">
                <c:if test="${dataProvider.description!=null}">
                    <br />
                    <h4>Data Provider Description</h4>
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
        </div>
    </body>
</html>