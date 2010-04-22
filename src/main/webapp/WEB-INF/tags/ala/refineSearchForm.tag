<%@ include file="/common/taglibs.jsp" %>
<%@ attribute name="type" required="true" type="java.lang.String" %>
<c:if test="${not empty type}">
    <form name="refineSearchForm" method="get" onsubmit="return submitQuickSearch1(false);" action="">
        <input id="query1" type="search" name="keyword" <c:if test="${not empty searchString}">value="${searchString}"</c:if> placeholder="<spring:message code="blanket.search.placeholder"/>" autosave="gbif.blanketsearch" results="5" tabindex="1"/>
        <a href="javascript:submitFromLinkQuickSearch1();" id="go"><spring:message code="blanket.search.go"/></a>
    </form>
    <script type="text/javascript">
        //document.getElementById("searchQuery").focus();
        function submitQuickSearch1(formSubmit){
            //check for empty value
            var textValue = document.getElementById('query1').value;
            if(textValue!=null && textValue.length>0){
                if(formSubmit)
                    document.quickSearchForm.submit();
                return true;
            }
            return false;
        }

        function submitFromLinkQuickSearch1(){
            //check for empty value
            var textValue = document.getElementById('query1').value;
            if(textValue!=null && textValue.length>0){
                    document.refineSearchForm.submit();
            }
        }
    </script>
</c:if>