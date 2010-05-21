<%@ include file="/common/taglibs.jsp"%>
        <br/>
        <input type="hidden" name="lim" value="9" id="tn" />
        <input type="hidden" name="act" value="2" id="act"/>
        <input type="hidden" name="xpath" value="${param.section}" id="xpath"/>
        <input type="hidden" name="url" value="${hostUrl}/occurrences/${occurrence.id}" size="128"/>
        <input type="hidden" name="recordKey" value="${rawOccurrenceRecord.key}" id="recordKey"/>
        <input type="hidden" name="ref" value="" size="128"/>
        <input type="hidden" name="token" value="/tmp/TUEb3WK"/>
        <div class="submitButtons">
            <span class="btn"><input type="submit" name="submit" value="Submit"></span>
            <span class="btn"><input type="button" value="Cancel" class="close"></span>
        </div>
        <div class="errorMsg">&nbsp;</div>
        <div class="loading"><img src="${pageContext.request.contextPath}/static/css/images/wait.gif" alt="loading data..."/></div>
        <div class='message'><span id="msgTextGeospatial" class="msgText"></span><input type="button" value="Close" class="finish"></div>