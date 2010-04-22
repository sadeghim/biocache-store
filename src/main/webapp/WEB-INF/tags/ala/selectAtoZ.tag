<%@ include file="/common/taglibs.jsp"%>
<%@ attribute name="includeBlank" required="false" type="java.lang.Boolean"%>
<c:if test="${includeBlank}">
    <option value=""></option>
</c:if>
<%
char [] alphabet = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
for (int i = 0; i < alphabet.length; i++) {
    out.print("    <option value='"+alphabet[i]+"'>"+alphabet[i]+"</option>\n");
}
%>