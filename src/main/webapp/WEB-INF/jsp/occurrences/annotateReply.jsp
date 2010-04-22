<%@ include file="/common/taglibs.jsp"%>
<div class="boxes">
    <div id="replyTo" class="window">
        <form action="${pageContext.request.contextPath}/annotation/saveAnnotation" method="post" name="replyToForm" id="replyToForm">
        <h4 style="margin:0 0 10px 0">Annotation Reply</h4>
        <table>
          <jsp:include page="annotateCommon1.jsp"/>
        </table>
        <input type="hidden" name="type" value="reply" id="type"/>
        <input type="hidden" name="field" value="" id="field"/>
        <input type="hidden" name="rootAnnotation" id="rootAnnotation"/>
        <jsp:include page="annotateCommon2.jsp"><jsp:param name="section" value="reply" /></jsp:include>
        </form>
    </div>
    <!-- Mask to cover the whole screen
    <div id="mask"></div> -->
</div>
