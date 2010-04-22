<%@ include file="/common/taglibs.jsp"%>
<%@ attribute name="name" required="true" type="java.lang.String"%>
<%
if (name.length() > 0) {
    try {
        org.ala.web.util.AnnotationType at = org.ala.web.util.AnnotationType.valueOf(name);
        out.print(at.getDescription());
    } catch(Exception e) {
        // do nothing
    }
}
%>