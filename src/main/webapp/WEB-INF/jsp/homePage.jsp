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
    	<div class="section">
    	<h1>BioCache</h1>
        <p>Welcome to the Atlas of Living Australia <strong>BioCache</strong>.</p>
        <p>
          This is an early release of this functionality for <strong>release 5 (October 29th 2010)</strong>.
        </p>
        <h3>Free text occurrence search</h3>
        <form id="search-form" action="occurrences/search" method="get" name="search-form"> 
            <input type="text" class="empty" id="search" name="q" value="Search occurrence records" autocomplete="off"> 
            <span class="search-button-wrapper"><input type="submit" class="search-button" id="search-button" alt="Search" value="Search"></span> 
        </form>
        </div>
    </body>
</html>