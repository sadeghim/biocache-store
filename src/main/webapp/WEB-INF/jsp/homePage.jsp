<%@ page contentType="text/html" pageEncoding="UTF-8" %><%@ 
taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" 
%><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="pageName" content="home"/>
        <title>Atlas of Living Australia - Biocache</title>
    </head>
    <body>
        <p>Welcome to the Atlas of Living Australia <strong>Biocache</strong>.</p>
        <p>
          This is an early release of this functionality for <strong>release 4 (July 21st)</strong>.
        </p>
        
        <h3>Free text occurrence search</h3>
        <form action="occurrences/search" method="get">
        	<input name="q" type="text" value="Red kangaroo"/>
        	<input type="submit"/>        
        </form>
    </body>
</html>