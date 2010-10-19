<%--
    Document   : main.jsp (sitemesh decorator file)
    Created on : 18/09/2009, 13:57
    Author     : dos009
--%><%@
taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %><%@
include file="/common/taglibs.jsp" %>
<!DOCTYPE html>
<html dir="ltr" lang="en-US">
    <head profile="http://gmpg.org/xfn/11">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

        <title><decorator:title default="Atlas of Living Australia" /></title>
        <link rel="stylesheet" href="${initParam.centralServer}/wp-content/themes/ala/style.css" type="text/css" media="screen" />
        <link rel="icon" type="image/x-icon" href="${initParam.centralServer}/wp-content/themes/ala/images/favicon.ico" />
        <link rel="shortcut icon" type="image/x-icon" href="${initParam.centralServer}/wp-content/themes/ala/images/favicon.ico" />

        <link rel="stylesheet" type="text/css" media="screen" href="${initParam.centralServer}/wp-content/themes/ala/css/sf.css" />
        <link rel="stylesheet" type="text/css" media="screen" href="${initParam.centralServer}/wp-content/themes/ala/css/superfish.css" />
        <link rel="stylesheet" type="text/css" media="screen" href="${initParam.centralServer}/wp-content/themes/ala/css/skin.css" />
        <link rel="stylesheet" type="text/css" media="screen" href="${initParam.centralServer}/wp-content/themes/ala/css/auth.css" />
        <link rel="stylesheet" type="text/css" media="screen" href="${initParam.centralServer}/wp-content/themes/ala/css/jquery.autocomplete.css" />
        <link rel="stylesheet" type="text/css" media="screen" href="${initParam.centralServer}/wp-content/themes/ala/css/biocache.css" />
<%--        <link rel="stylesheet" type="text/css" media="screen" href="${pageContext.request.contextPath}/static/css/biocache.css" /> --%>

        <script language="JavaScript" type="text/javascript" src="${initParam.centralServer}/wp-content/themes/ala/scripts/form.js"></script>
        <script language="JavaScript" type="text/javascript" src="${initParam.centralServer}/wp-content/themes/ala/scripts/jquery-1.4.2.min.js"></script>
        <script language="JavaScript" type="text/javascript" src="${initParam.centralServer}/wp-content/themes/ala/scripts/hoverintent-min.js"></script>
        <script language="JavaScript" type="text/javascript" src="${initParam.centralServer}/wp-content/themes/ala/scripts/superfish/superfish.js"></script>
        <script language="JavaScript" type="text/javascript" src="${initParam.centralServer}/wp-content/themes/ala/scripts/jquery.autocomplete.js"></script>
        <script type="text/javascript">

            //add the indexOf method for IE7
            if(!Array.indexOf){
                Array.prototype.indexOf = function(obj){
                    for(var i=0; i<this.length; i++){
                        if(this[i]===obj){
                            return i;
                        }
                    }
                    return -1;
                }
            }
            // initialise plugins
            jQuery(function(){
                jQuery('ul.sf').superfish( {
                    delay:500,
                    autoArrows:false,
                    dropShadows:false
                });

                // highlight explore menu tab
                jQuery("div#nav li.nav-explore").addClass("selected");
                // autocomplete for search input (Note: JQuery UI version)
                jQuery("form#search-form input#search").autocomplete({
                    source: function( request, response ) {
                        $.ajax({
                            url: "http://bie.ala.org.au/search/auto.jsonp",
                            dataType: "jsonp",
                            data: {
                                limit: 10,
                                q: request.term
                            },
                            success: function( data ) {
                                response( $.map( data.autoCompleteList, function( item ) {
                                    return {
                                            label: item.matchedNames[0],
                                            value: item.matchedNames[0]
                                    }
                                }));
                            }
                        });
                    },
                    minLength: 3,
                    zIndex: 11

                });
            });

        </script>
        <style type="text/css">
            ul.ui-autocomplete {
                text-align: left;
                z-index: 11 !important;
            }
        </style>
        <meta name='robots' content='noindex,nofollow' />
        <link rel="alternate" type="application/rss+xml" title="Atlas Living Australia NG &raquo; Feed" href="${initParam.centralServer}/?feed=rss2" />
        <link rel="alternate" type="application/rss+xml" title="Atlas Living Australia NG &raquo; Comments Feed" href="${initParam.centralServer}/?feed=comments-rss2" />
        <link rel='stylesheet' id='external-links-css'  href='${initParam.centralServer}/wp-content/plugins/sem-external-links/sem-external-links.css?ver=20090903' type='text/css' media='' />
        <!--<link rel="EditURI" type="application/rsd+xml" title="RSD" href="${initParam.centralServer}/xmlrpc.php?rsd" />
        <link rel="wlwmanifest" type="application/wlwmanifest+xml" href="${initParam.centralServer}/wp-includes/wlwmanifest.xml" />
        <link rel='index' title='Atlas Living Australia NG' href='${initParam.centralServer}' />
        <meta name="generator" content="WordPress 2.9.2" />
        <link rel='canonical' href='${initParam.centralServer}' />-->
        <decorator:head />
        <!-- WP Menubar 4.7: start CSS -->
        <!-- WP Menubar 4.7: end CSS -->
    </head>
    <body class="two-column-right">
        <div id="wrapper">
            <div id="banner">
                <div id="logo">
                    <a href="${initParam.centralServer}" title="Atlas of Living Australia home"><img src="${initParam.centralServer}/wp-content/themes/ala/images/ala_logo.png" width="215" height="80" alt="Atlas of Living Ausralia logo" /></a>
                </div><!--close logo-->

                <c:set var="queryString" value="${pageContext.request.queryString}"/>
                <c:choose>
                    <c:when test="${empty queryString}">
                        <c:set var="requestUrl" value="${pageContext.request.requestURL}"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="requestUrl" value="${pageContext.request.requestURL}?${fn:replace(queryString, '+', '%2B')}"/>
                    </c:otherwise>
                </c:choose>

                <ala:bannerMenu returnUrlPath="${requestUrl}"/>

                <div id="wrapper_search">
                    <form id="search-form" action="http://bie.ala.org.au/search" method="get" name="search-form">
                        <label for="search">Search</label>
                        <input type="text" class="filled" id="search" name="q" value="Search the Atlas" />
                        <span class="search-button-wrapper"><input type="submit" class="search-button" id="search-button" alt="Search" value="Search" /></span>
                    </form>
                </div><!--close wrapper_search-->
            </div><!--close banner-->
            <div id="wrapper_border"><!--main content area-->
                <div id="border">
                    <div id="content">
		                <c:if test="${!empty pageContext.request.remoteUser}">
		                    <div id="loginId"> You are logged in as: ${pageContext.request.remoteUser}</div>
		                </c:if>
<!--                        <div id="decoratorBody">-->
                            <decorator:body />
<!--                        </div>-->
                    </div><!--close content-->
                </div><!--close border-->
            </div><!--close wrapper_border-->
            <div id="footer">
                <ala:footerMenu returnUrlPath="${requestUrl}"/>
	        </div><!--close footer-->
        </div><!--close wrapper-->
    </body>
</html>