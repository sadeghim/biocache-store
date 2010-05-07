-- Export Occurrend data from MySQL DB portal on alaproddb1-cbr.vm.csiro.au
--
-- Dump of occurrence table with de-normalised values for indexing via Lucene or SOLR
-- as CSV file with headers
SELECT 'id','data_provider_id','data_provider','data_resource_id','data_resource',
'institution_code_id','institution_code','institution_code_name','institution_code_lsid',
'collection_code_id','collection_code','catalogue_number_id','catalogue_number',
'taxon_concept_lsid','taxon_name','author','rank_id','rank','raw_taxon_name','raw_author','country_code',
'kingdom_lsid','kingdom','family_lsid','family',
'state',
'biogeographic_region',
'places',
'latitude','longitude','lat_long_precision','cell_id','centi_cell_id','tenmilli_cell_id',
'year','month','occurrence_date','basis_of_record_id','basis_of_record','raw_basis_of_record',
'type_status','identifier_type','identifier_value','identifier_name','identifier_date',
'collector','taxonomic_issue','geospatial_issue','other_issue','created_date','modified_date'
UNION
SELECT oc.id, oc.data_provider_id, dp.name data_provider_name, oc.data_resource_id, dr.name data_resource_name,
oc.institution_code_id, ic.code institution_code_code, ic.name institution_code_name, ic.lsid institution_code_lsid,
oc.collection_code_id, cc.code collection_code, oc.catalogue_number_id, cn.code catalogue_number, 
tc.lsid, tn.canonical taxon_name, tn.author, tc.rank, rnk.`name`, ror.scientific_name, ror.author raw_author, oc.iso_country_code,
ktc.lsid, ktn.canonical, ftc.lsid, ftn.canonical,
GROUP_CONCAT(st.name ORDER BY st.name SEPARATOR "|") as states,
GROUP_CONCAT(bgr.name ORDER BY bgr.name SEPARATOR "|") as bio_geo_regions,
GROUP_CONCAT(plc.name ORDER BY plc.name SEPARATOR "|") as places,
oc.latitude, oc.longitude, ror.lat_long_precision, oc.cell_id, oc.centi_cell_id, oc.tenmilli_cell_id,
oc.year, oc.month, DATE_FORMAT(oc.occurrence_date,'%Y-%m-%dT%H:%i:%sZ'), oc.basis_of_record basis_of_record_id, bor.description basis_of_record, ror.basis_of_record,
typ.type_status, lit.it_value, idr.identifier, ror.identifier_name, DATE_FORMAT(ror.identification_date,'%Y-%m-%dT%H:%i:%sZ'),
ror.collector_name, oc.taxonomic_issue, oc.geospatial_issue, oc.other_issue, ror.created, ror.modified
FROM occurrence_record oc
INNER JOIN raw_occurrence_record ror ON ror.id = oc.id
INNER JOIN taxon_name tn ON tn.id = oc.taxon_name_id
INNER JOIN taxon_concept tc ON tc.id = oc.nub_concept_id
INNER JOIN rank rnk ON rnk.id = tc.rank
INNER JOIN taxon_concept ktc ON ktc.id = oc.kingdom_concept_id
INNER JOIN taxon_name ktn ON ktn.id = ktc.taxon_name_id
INNER JOIN taxon_concept ftc ON ftc.id = oc.family_concept_id
INNER JOIN taxon_name ftn ON ftn.id = ftc.taxon_name_id
INNER JOIN data_provider dp ON dp.id = oc.data_provider_id
INNER JOIN data_resource dr ON dr.id = oc.data_resource_id
INNER JOIN institution_code ic ON ic.id = oc.institution_code_id
INNER JOIN collection_code cc ON cc.id = oc.collection_code_id
INNER JOIN catalogue_number cn ON cn.id = oc.catalogue_number_id
INNER JOIN basis_of_record bor ON bor.id = oc.basis_of_record
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
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' LINES TERMINATED BY '\n'; -- ESCAPED BY '"'

-- WHERE oc.data_resource_id = 56