<%-- 
    Document   : show
    Created on : Apr 21, 2010, 9:36:39 AM
    Author     : "Nick dos Remedios <Nick.dosRemedios@csiro.au>"
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Occurrence Record ${id}</title>
    </head>
    <body>
        <h1>Occurrence Details: ${occurrence.institutionCode} ${occurrence.collectionCode} ${occurrence.catalogueNumber}</h1>
        <div id="occurrenceDataset">
            <h2>Datset</h2>
            <p>Data Provider: ${occurrence.dataProvider}</p>
            <p>Data Resource: ${occurrence.dataResource}</p>
            <p>Institution Code: ${occurrence.institutionCode}</p>
            <p>Collection Code: ${occurrence.collectionCode}</p>
            <p>Catalogue Number: ${occurrence.catalogueNumber}</p>
            <p>Basis of Record: ${occurrence.basisOfRecord}</p>
            <p>${fn:replace(occurrence.identifierType, 'IDENTIFIERTYPE_', '')}: ${occurrence.identifierValue}</p>
            <p>taxonomicIssue: ${occurrence.taxonomicIssue}</p>
        </div>
        <div id="occurrenceTaxonomy">
            <h2>Taxonomy</h2>
            <p>Scientific Name: ${occurrence.taxonName} ${occurrence.author} (interpreted as ${occurrence.rawTaxonName} ${occurrence.author}</p>
            <p>Kingdom: ${occurrence.kingdom}</p>
            <p>Family: ${occurrence.family}</p>
            <p>Genus: ${occurrence.genus}</p>
            <p>Species ${occurrence.species}</p>
        </div>
    </body>
</html>
