-- Export Occurrend data from MySQL DB portal on alaproddb1-cbr.vm.csiro.au
--
-- Dump of occurrence table with de-normalised values for indexing via Lucene or SOLR
-- as CSV file with headers
SELECT 'id','data_provider_id','data_provider','data_resource_id','data_resource',
'institution_code_id','institution_code','institution_code_name','institution_code_lsid',
'collection_code_id','collection_code','catalogue_number_id','catalogue_number',
'taxon_concept_lsid','taxon_name','author','rank_id','rank','raw_taxon_name','raw_author','country_code',
'kingdom_lsid','kingdom','phylum_lsid','phylum','class_lsid','class','order_lsid','order',
'family_lsid','family','genus_lsid','genus','species_lsid','species',
'state',
'biogeographic_region',
'places',
'latitude','longitude','lat_long_precision','cell_id','centi_cell_id','tenmilli_cell_id',
'year','month','occurrence_date','basis_of_record_id','basis_of_record','raw_basis_of_record',
'type_status','identifier_type','identifier_value','identifier_name','identifier_date',
'collector','taxonomic_issue','geospatial_issue','other_issue','created_date','modified_date'
UNION
SELECT oc.id, oc.data_provider_id, dp.`name`, oc.data_resource_id, dr.`name`,
oc.institution_code_id, ic.code, ic.`name`, ic.lsid,
oc.collection_code_id, cc.code, oc.catalogue_number_id, cn.code, 
tc.guid, tn.canonical, tn.author, tc.rank, rnk.`name`, ror.scientific_name, ror.author, oc.iso_country_code,
kdc.guid, kdn.canonical, phc.guid, phn.canonical, clc.guid, cln.canonical, odc.guid, odn.canonical,
fmc.guid, fmn.canonical, gnc.guid, gnn.canonical, spc.guid, spn.canonical,
GROUP_CONCAT(st.`name` ORDER BY st.`name` SEPARATOR "|") as states,
GROUP_CONCAT(bgr.`name` ORDER BY bgr.`name` SEPARATOR "|") as bio_geo_regions,
GROUP_CONCAT(plc.`name` ORDER BY plc.`name` SEPARATOR "|") as places,
oc.latitude, oc.longitude, ror.lat_long_precision, oc.cell_id, oc.centi_cell_id, oc.tenmilli_cell_id,
oc.'year', oc.'month', DATE_FORMAT(oc.occurrence_date,'%Y-%m-%dT%H:%i:%sZ'), oc.basis_of_record, bor.description, ror.basis_of_record,
typ.type_status, lit.it_value, idr.identifier, ror.identifier_name, DATE_FORMAT(ror.identification_date,'%Y-%m-%dT%H:%i:%sZ'),
ror.collector_name, oc.taxonomic_issue, oc.geospatial_issue, oc.other_issue, ror.created, ror.modified
FROM occurrence_record oc
LEFT JOIN raw_occurrence_record ror ON ror.id = oc.id
LEFT JOIN taxon_name tn ON tn.id = oc.taxon_name_id
LEFT JOIN taxon_concept tc ON tc.id = oc.nub_concept_id
LEFT JOIN rank rnk ON rnk.id = tc.rank
LEFT JOIN taxon_concept kdc ON kdc.id = oc.kingdom_concept_id
LEFT JOIN taxon_name kdn ON kdn.id = kdc.taxon_name_id
LEFT JOIN taxon_concept phc ON phc.id = oc.phylum_concept_id
LEFT JOIN taxon_name phn ON phn.id = phc.taxon_name_id
LEFT JOIN taxon_concept clc ON clc.id = oc.class_concept_id
LEFT JOIN taxon_name cln ON cln.id = clc.taxon_name_id
LEFT JOIN taxon_concept odc ON odc.id = oc.order_concept_id
LEFT JOIN taxon_name odn ON odn.id = odc.taxon_name_id
LEFT JOIN taxon_concept fmc ON fmc.id = oc.family_concept_id
LEFT JOIN taxon_name fmn ON fmn.id = fmc.taxon_name_id
LEFT JOIN taxon_concept gnc ON gnc.id = oc.genus_concept_id
LEFT JOIN taxon_name gnn ON gnn.id = gnc.taxon_name_id
LEFT JOIN taxon_concept spc ON spc.id = oc.species_concept_id
LEFT JOIN taxon_name spn ON spn.id = spc.taxon_name_id
LEFT JOIN data_provider dp ON dp.id = oc.data_provider_id
LEFT JOIN data_resource dr ON dr.id = oc.data_resource_id
LEFT JOIN institution_code ic ON ic.id = oc.institution_code_id
LEFT JOIN collection_code cc ON cc.id = oc.collection_code_id
LEFT JOIN catalogue_number cn ON cn.id = oc.catalogue_number_id
LEFT JOIN basis_of_record bor ON bor.id = oc.basis_of_record
LEFT JOIN geo_mapping gm ON gm.occurrence_id = oc.id
LEFT JOIN geo_region st ON st.id = gm.geo_region_id AND st.region_type <= 2
LEFT JOIN geo_region bgr ON bgr.id = gm.geo_region_id AND bgr.region_type = 2000
LEFT JOIN geo_region plc ON plc.id = gm.geo_region_id AND (plc.region_type >= 3 AND plc.region_type <= 11)
LEFT JOIN typification_record typ ON typ.occurrence_id = oc.id
LEFT JOIN identifier_record idr ON idr.occurrence_id = oc.id
LEFT JOIN lookup_identifier_type lit ON lit.it_key = idr.identifier_type
--WHERE oc.data_resource_id = 56
GROUP BY oc.id
INTO outfile '/data/bie-staging/biocache/occurrences.csv'
--INTO outfile '/data/bie-staging/biocache/occurrences.56.csv'
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' LINES TERMINATED BY '\n'; -- ESCAPED BY '"'
