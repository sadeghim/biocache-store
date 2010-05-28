delimiter $$
-- Function to get the names for a geo regino
-- The arguments:
-- in_oc: The occurrence record id
-- in_low: The id for the lowest region type to include
-- in_hi: The id for the highest region type to include
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

-- Stored function to lookup taxon guid for a given higher taxa concept id.
-- Nick dos Remedios 2010-05-16
-- Args:
-- concept_id: the input concept id
DROP FUNCTION IF EXISTS get_taxa_guid$$
CREATE FUNCTION get_taxa_guid(concept_id INT) returns VARCHAR(1000) 
DETERMINISTIC
READS SQL DATA
    BEGIN
        DECLARE guid VARCHAR(1000);
        SELECT tc.guid
        INTO guid
        FROM taxon_concept tc
        WHERE tc.id = concept_id;

        RETURN guid;
    END $$

-- Stored function to lookup taxon name for a given higher taxa concept id.
-- Nick dos Remedios 2010-05-16
-- Args:
-- concept_id: the input concept id
DROP FUNCTION IF EXISTS get_taxa_canonical$$
CREATE FUNCTION get_taxa_canonical(concept_id INT) returns VARCHAR(1000)
DETERMINISTIC
READS SQL DATA
    BEGIN
        DECLARE canonical VARCHAR(1000);
        SELECT tn.canonical
        INTO canonical
        FROM taxon_concept tc
        INNER JOIN taxon_name tn ON tn.id = tc.taxon_name_id
        WHERE tc.id = concept_id;

        RETURN canonical;
    END $$

-- Stored function to lookup type status.
-- Nick dos Remedios 2010-05-16
-- Args:
-- concept_id: the input concept id
DROP FUNCTION IF EXISTS get_type_status$$
CREATE FUNCTION get_type_status(occurrence_id INT) returns VARCHAR(1000)
DETERMINISTIC
READS SQL DATA
    BEGIN
        DECLARE type_status VARCHAR(1000);
        SELECT typ.type_status
        INTO type_status
        FROM typification_record typ
        WHERE typ.occurrence_id = occurrence_id;

        RETURN type_status;
    END $$

-- Stored function to lookup type status.
-- Nick dos Remedios 2010-05-16
-- Args:
-- concept_id: the input concept id
DROP FUNCTION IF EXISTS get_identifier_type$$
CREATE FUNCTION get_identifier_type(occurrence_id INT) returns VARCHAR(1000)
DETERMINISTIC
READS SQL DATA
    BEGIN
        DECLARE identifier_type VARCHAR(1000);
        SELECT lit.it_value
        INTO identifier_type
        FROM identifier_record idr
        LEFT JOIN lookup_identifier_type lit ON lit.it_key = idr.identifier_type
        WHERE idr.occurrence_id = occurrence_id;

        RETURN identifier_type;
    END $$

-- Stored function to lookup type status.
-- Nick dos Remedios 2010-05-16
-- Args:
-- concept_id: the input concept id
DROP FUNCTION IF EXISTS get_identifier_name$$
CREATE FUNCTION get_identifier_name(occurrence_id INT) returns VARCHAR(1000)
DETERMINISTIC
READS SQL DATA
    BEGIN
        DECLARE identifier_name VARCHAR(1000);
        SELECT idr.identifier
        INTO identifier_name
        FROM identifier_record idr
        WHERE idr.occurrence_id = occurrence_id;

        RETURN identifier_name;
    END $$

DELIMITER ;

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
'point-1','point-0.1','point-0.01','point-0.001','point-0.0001',
'year','month','occurrence_date','basis_of_record_id','basis_of_record','raw_basis_of_record',
'type_status','identifier_type','identifier_value','identifier_name','identifier_date',
'collector','taxonomic_issue','geospatial_issue','other_issue','created_date','modified_date'
UNION
SELECT IFNULL(oc.id,''),IFNULL(oc.data_provider_id,''),IFNULL(dp.`name`,''),IFNULL(oc.data_resource_id,''),
IFNULL(dr.`name`,''),IFNULL(oc.institution_code_id,''),IFNULL(ic.code,''),IFNULL(ic.`name`,''),
IFNULL(ic.lsid,''),IFNULL(oc.collection_code_id,''),IFNULL(cc.code,''),IFNULL(oc.catalogue_number_id,''),
IFNULL(cn.code,''),IFNULL(tc.guid,''),IFNULL(tn.canonical,''),IFNULL(tn.author,''),IFNULL(tc.rank,''),
IFNULL(rnk.`name`,''),IFNULL(ror.scientific_name,''),IFNULL(ror.author,''),IFNULL(oc.iso_country_code,''),
IFNULL(get_taxa_guid(oc.kingdom_concept_id),''), IFNULL(get_taxa_canonical(oc.kingdom_concept_id),''),
IFNULL(get_taxa_guid(oc.phylum_concept_id),''), IFNULL(get_taxa_canonical(oc.phylum_concept_id),''),
IFNULL(get_taxa_guid(oc.class_concept_id),''), IFNULL(get_taxa_canonical(oc.class_concept_id),''),
IFNULL(get_taxa_guid(oc.order_concept_id),''), IFNULL(get_taxa_canonical(oc.order_concept_id),''),
IFNULL(get_taxa_guid(oc.family_concept_id),''), IFNULL(get_taxa_canonical(oc.family_concept_id),''),
IFNULL(get_taxa_guid(oc.genus_concept_id),''), IFNULL(get_taxa_canonical(oc.genus_concept_id),''),
IFNULL(get_taxa_guid(oc.species_concept_id),''), IFNULL(get_taxa_canonical(oc.species_concept_id),''),
IFNULL(get_georegion_names(oc.id,0,2),'') as states,
IFNULL(get_georegion_names(oc.id,2000,3999),'') as bio_geo_regions,
IFNULL(get_georegion_names(oc.id,3,11),'') as places,
IFNULL(oc.latitude,''),IFNULL(oc.longitude,''),IFNULL(ror.lat_long_precision,''),
IFNULL(CONCAT_WS(',', oc.latitude, oc.longitude),'') as lat_long,
IFNULL(oc.cell_id,''),IFNULL(oc.centi_cell_id,''),IFNULL(oc.tenmilli_cell_id,''),
CONCAT(CONCAT(round(oc.latitude *1)/1, ','), round(oc.longitude *1)/1),
CONCAT(CONCAT(round(oc.latitude *10)/10, ','), round(oc.longitude *10)/10),
CONCAT(CONCAT(round(oc.latitude *100)/100, ','), round(oc.longitude *100)/100),
CONCAT(CONCAT(round(oc.latitude *1000)/1000, ','), round(oc.longitude *1000)/1000),
CONCAT(CONCAT(round(oc.latitude *10000)/10000, ','), round(oc.longitude *10000)/10000),
IFNULL(oc.`year`,''),IFNULL(oc.`month`,''),IFNULL(DATE_FORMAT(oc.occurrence_date,'%Y-%m-%dT%H:%i:%sZ'),''),
IFNULL(oc.basis_of_record,''),IFNULL(bor.description,''),IFNULL(ror.basis_of_record,''),
IFNULL(get_type_status(oc.id),''),IFNULL(get_identifier_type(oc.id),''),IFNULL(get_identifier_name(oc.id),''),
IFNULL(ror.identifier_name,''),IFNULL(DATE_FORMAT(ror.identification_date,'%Y-%m-%dT%H:%i:%sZ'),''),
IFNULL(ror.collector_name,''),IFNULL(oc.taxonomic_issue,''),IFNULL(oc.geospatial_issue,''),
IFNULL(oc.other_issue,''),IFNULL(DATE_FORMAT(ror.created,'%Y-%m-%dT%H:%i:%sZ'),''),IFNULL(DATE_FORMAT(ror.modified,'%Y-%m-%dT%H:%i:%sZ'),'')
FROM occurrence_record oc
INNER JOIN raw_occurrence_record ror ON ror.id = oc.id
INNER JOIN taxon_name tn ON tn.id = oc.taxon_name_id
INNER JOIN taxon_concept tc ON tc.id = oc.nub_concept_id
INNER JOIN rank rnk ON rnk.id = tc.rank
INNER JOIN data_provider dp ON dp.id = oc.data_provider_id
INNER JOIN data_resource dr ON dr.id = oc.data_resource_id
INNER JOIN institution_code ic ON ic.id = oc.institution_code_id
INNER JOIN collection_code cc ON cc.id = oc.collection_code_id
INNER JOIN catalogue_number cn ON cn.id = oc.catalogue_number_id
INNER JOIN basis_of_record bor ON bor.id = oc.basis_of_record
--WHERE oc.data_resource_id = 56
INTO outfile '/data/bie-staging/biocache/occurrences.csv'
-- INTO outfile '/data/bie-staging/biocache/occurrences.56.csv'
FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY '"' LINES TERMINATED BY '\n'; -- ESCAPED BY '"';