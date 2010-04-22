<%@ include file="/common/taglibs.jsp"%>
<%
char [] alphabet = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
for (int i = 0; i < alphabet.length; i++) {
    out.print("<a href='#"+alphabet[i]+"' id='filterLink' onClick='filter(\""+alphabet[i]+"\")'>"+alphabet[i]+"</a> ");
    if (i != 25) out.print(" | ");
}
%>