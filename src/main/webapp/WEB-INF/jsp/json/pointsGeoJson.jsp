<%@ include file="/common/taglibs.jsp"%><%
/*
 * GeoJSON representation of "distinct" points for a given occurrence search
 *
 * @author "Nick dos Remedios <Nick.dosRemedios@csiro.au>"
*/
%><%@ page contentType="application/json; charset=UTF-8" %>
<c:if test="${not empty param['callback']}">${param['callback']}(</c:if>
<json:object prettyPrint="true">
    <json:property name="type" value="FeatureCollection"/>
    <json:array name="featues" var="point" items="${points}">
        <json:object>
            <json:property name="type" value="Feature"/>
            <json:object name="geometry">
                <json:property name="type" value="Point"/>
                <json:array name="coordinates" var="coord" items="${point.coordinates}">
                    <json:property value="${coord}"/>
                </json:array>
            </json:object>
            <json:object name="properties">
                 <json:property name="type" value="${point.type.label}"/>
                 <json:property name="count" value="${point.count}"/>
            </json:object>
        </json:object>
    </json:array>
</json:object>
<c:if test="${not empty param['callback']}">)</c:if>