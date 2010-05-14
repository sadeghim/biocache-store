delimiter $$
-- Function to get the names for a geo regino
--The arguments:
--in_oc: The occurrence record id
--in_low: The id for the lowest region type to include
--in_hi: The id for the highest region type to include
DROP FUNCTION IF EXISTS get_georegion_names$$
CREATE FUNCTION get_georegion_names(in_oc INT, in_low INT, in_hi INT) returns varchar(1000)
deterministic
	BEGIN
			declare result varchar(1000);
			SELECT GROUP_CONCAT(gr.name ORDER BY gr.name SEPARATOR "|") into result FROM geo_region gr
			INNER JOIN geo_mapping gm ON gr.id = gm.geo_region_id
			AND (gr.region_type >= in_low AND gr.region_type <= in_hi) AND gm.occurrence_id = in_oc;

			return result;

	END;
	$$
delimiter ;

SELECT 'id','data_provider_id','data_provider','data_resource_id','data_resource',
'institution_code_id','institution_code','institution_code_name','institution_code_lsid',
'collection_code_id','collection_code','catalogue_number_id','catalogue_number',
'taxon_concept_lsid','taxon_name','author','rank_id','rank','raw_taxon_name','raw_author','country_code',
'kingdom_lsid','kingdom','phylum_lsid','phylum','class_lsid','class','order_lsid','order',
'family_lsid','family','genus_lsid','genus','species_lsid','species',
'state',
'biogeographic_region',
'places',
'latitude','longitude','lat_long_precision','lat_long',
'cell_id','centi_cell_id','tenmilli_cell_id',
'year','month','occurrence_date','basis_of_record_id','basis_of_record','raw_basis_of_record',
'type_status','identifier_type','identifier_value','identifier_name','identifier_date',
'collector','taxonomic_issue','geospatial_issue','other_issue','created_date','modified_date'
UNION
SELECT oc.id, oc.data_provider_id, dp.`name`, oc.data_resource_id, dr.`name`,
oc.institution_code_id, ic.code, ic.`name`, ic.lsid,
oc.collection_code_id, cc.code, oc.catalogue_number_id, cn.code,
tc.lsid, tn.canonical, tn.author, tc.rank, rnk.`name`, ror.scientific_name, ror.author, oc.iso_country_code,
kdc.lsid, kdn.canonical, phc.lsid, phn.canonical, clc.lsid, cln.canonical, odc.lsid, odn.canonical,
fmc.lsid, fmn.canonical, gnc.lsid, gnn.canonical, spc.lsid, spn.canonical,
get_georegion_names(oc.id, 0, 2) as states,
get_georegion_names(oc.id, 2000, 2000) as bio_geo_regions,
get_georegion_names(oc.id, 3, 11) as places,
oc.latitude, oc.longitude, ror.lat_long_precision, CONCAT_WS(',', oc.latitude, oc.longitude) as lat_long,
oc.cell_id, oc.centi_cell_id, oc.tenmilli_cell_id,
oc.`year`, oc.`month`, DATE_FORMAT(oc.occurrence_date,'%Y-%m-%dT%H:%i:%sZ'), oc.basis_of_record, bor.description, ror.basis_of_record,
typ.type_status, lit.it_value, idr.identifier, ror.identifier_name, DATE_FORMAT(ror.identification_date,'%Y-%m-%dT%H:%i:%sZ'),
ror.collector_name, oc.taxonomic_issue, oc.geospatial_issue, oc.other_issue, ror.created, ror.modified
FROM occurrence_record oc
INNER JOIN raw_occurrence_record ror ON ror.id = oc.id
INNER JOIN taxon_name tn ON tn.id = oc.taxon_name_id
INNER JOIN taxon_concept tc ON tc.id = oc.nub_concept_id
INNER JOIN rank rnk ON rnk.id = tc.rank
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
INNER JOIN data_provider dp ON dp.id = oc.data_provider_id
INNER JOIN data_resource dr ON dr.id = oc.data_resource_id
INNER JOIN institution_code ic ON ic.id = oc.institution_code_id
INNER JOIN collection_code cc ON cc.id = oc.collection_code_id
INNER JOIN catalogue_number cn ON cn.id = oc.catalogue_number_id
INNER JOIN basis_of_record bor ON bor.id = oc.basis_of_record
LEFT JOIN typification_record typ ON typ.occurrence_id = oc.id
LEFT JOIN identifier_record idr ON idr.occurrence_id = oc.id
LEFT JOIN lookup_identifier_type lit ON lit.it_key = idr.identifier_type
--WHERE oc.data_resource_id = 56
INTO outfile '/data/bie-staging/biocache/occurrences.csv'
--INTO outfile '/data/bie-staging/biocache/occurrences.56.csv'
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' LINES TERMINATED BY '\n'; -- ESCAPED BY '"';