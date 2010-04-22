<%@ include file="/common/taglibs.jsp"%>
<%@ attribute name="dataType" required="true" type="java.lang.String"%>
<%
for (org.ala.web.util.AnnotationType atype : org.ala.web.util.AnnotationType.values()) {
    if (dataType.equals(atype.getDataType())) {
        out.print("<option value='"+atype.toString()+"'>"+atype.getDescription()+"</option>");
    }
}
%>