set @@session.max_sp_recursion_depth=255; 
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
-- Function to get the first common name listed for the supplied taxon_concept
-- Natasha Carter 2010-07-01
-- The args:
-- concept_id: The input concept id
DROP FUNCTION IF EXISTS get_first_taxa_cn$$
CREATE FUNCTION get_first_taxa_cn(concept_id INT) returns VARCHAR(255) 
DETERMINISTIC
READS SQL DATA
    BEGIN
        DECLARE cname VARCHAR(255);
        SELECT cn.name
        INTO cname
        FROM common_name cn
        WHERE cn.taxon_concept_id = concept_id order by id limit 1;

        RETURN cname;
    END $$


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

-- A recursive stored procedure that dumps the occurrences
-- records to mulitple temporary files. This procedure improves 
-- the dump  time.  
-- Natasha Carter 2010-08-25
-- Args:
-- low: The minimum id of the occurrence record to dump
-- high: One less than the maximum occurrence record to dump
DROP PROCEDURE IF EXISTS dump_bio_occurrences$$
CREATE PROCEDURE dump_bio_occurrences(IN low INT, IN high INT) 
	BEGIN
		DECLARE more BOOLEAN;
                DECLARE newlow INT;
		set @cmd = concat('SELECT STRAIGHT_JOIN oc.id,IFNULL(dp.uid,\'\'),IFNULL(dp.`name`,\'\'),IFNULL(dr.uid,\'\'),'
		,'IFNULL(dr.`name`,\'\'),IFNULL(icm.institution_uid,\'\'),IFNULL(ic.code,\'\'),IFNULL(ic.`name`,\'\'),'
		,'IFNULL(ic.lsid,\'\'),IFNULL(icm.collection_uid,\'\'),IFNULL(cc.code,\'\'),IFNULL(oc.catalogue_number_id,\'\'),'
		,'IFNULL(cn.code,\'\'),IFNULL(tc.guid,\'\'),IFNULL(tn.canonical,\'\'),IFNULL(tn.author,\'\'), IFNULL(get_first_taxa_cn(oc.taxon_concept_id),\'\'),'
		,'CONCAT_WS(\'|\', IFNULL(tn.canonical,\'\'), IFNULL(tc.guid,\'\'), IFNULL(get_first_taxa_cn(oc.taxon_concept_id),\'\'), IFNULL(get_taxa_canonical(oc.kingdom_concept_id),\'\'), IFNULL(get_taxa_canonical(oc.family_concept_id),\'\') ),'
		,'IFNULL(tc.rank,\'\'), IFNULL(rnk.`name`,\'\'),IFNULL(ror.scientific_name,\'\'),IFNULL(ror.author,\'\'),'
		,'IFNULL(oc.iso_country_code,\'\'), IFNULL(tc.lft,\'\'), IFNULL(tc.rgt,\'\'),'
		,'IFNULL(get_taxa_guid(oc.kingdom_concept_id),\'\'), IFNULL(get_taxa_canonical(oc.kingdom_concept_id),\'\'),'
		,'IFNULL(get_taxa_guid(oc.phylum_concept_id),\'\'), IFNULL(get_taxa_canonical(oc.phylum_concept_id),\'\'),'
		,'IFNULL(get_taxa_guid(oc.class_concept_id),\'\'), IFNULL(get_taxa_canonical(oc.class_concept_id),\'\'),'
		,'IFNULL(get_taxa_guid(oc.order_concept_id),\'\'), IFNULL(get_taxa_canonical(oc.order_concept_id),\'\'),'
		,'IFNULL(get_taxa_guid(oc.family_concept_id),\'\'), IFNULL(get_taxa_canonical(oc.family_concept_id),\'\'),'
		,'IFNULL(get_taxa_guid(oc.genus_concept_id),\'\'), IFNULL(get_taxa_canonical(oc.genus_concept_id),\'\'),'
		,'IFNULL(get_taxa_guid(oc.species_concept_id),\'\'), IFNULL(get_taxa_canonical(oc.species_concept_id),\'\'),'
		,'IFNULL(get_georegion_names(oc.id,0,2),\'\') as states,'
		,'IFNULL(get_georegion_names(oc.id,2000,3999),\'\') as bio_geo_regions,'
		,'IFNULL(get_georegion_names(oc.id,3,12),\'\') as places,'
		,'IFNULL(oc.latitude,\'\'),IFNULL(ror.raw_latitude, \'\'),IFNULL(oc.longitude,\'\'),IFNULL(ror.raw_longitude,\'\'),IFNULL(ror.lat_long_precision,\'\'),'
		,'IFNULL(CONCAT_WS(\'\,\', oc.latitude, oc.longitude),\'\') as lat_long,'
		,'IFNULL(oc.cell_id,\'\'),IFNULL(oc.centi_cell_id,\'\'),IFNULL(oc.tenmilli_cell_id,\'\'),'
		,'CONCAT(CONCAT(ROUND(round(oc.latitude *1)/1,1), \'\,\'), ROUND(round(oc.longitude *1)/1,1)),'
		,'CONCAT(CONCAT(ROUND(round(oc.latitude *10)/10,1), \'\,\'), ROUND(round(oc.longitude *10)/10,1)),'
		,'CONCAT(CONCAT(ROUND(round(oc.latitude *100)/100,2), \'\,\'), ROUND(round(oc.longitude *100)/100,2)),'
		,'CONCAT(CONCAT(ROUND(round(oc.latitude *1000)/1000,3), \'\,\'), ROUND(round(oc.longitude *1000)/1000,3)),'
		,'CONCAT(CONCAT(ROUND(round(oc.latitude *10000)/10000,4), \'\,\'), ROUND(round(oc.longitude *10000)/10000,4))'
                ,',IFNULL(oc.altitude_metres,\'\'), IFNULL(oc.depth_centimetres,\'\')'
		,',IFNULL(oc.`year`,\'\'),IFNULL(LPAD(oc.`month`,2,\'0\'),\'\'),IFNULL(DATE_FORMAT(oc.occurrence_date,\'%Y-%m-%dT%H:%i:%sZ\'),\'\'),'
		,'IFNULL(oc.basis_of_record,\'\'),IFNULL(bor.description,\'\'),IFNULL(ror.basis_of_record,\'\'),'
		,'IFNULL(get_type_status(oc.id),\'\'),IFNULL(get_identifier_type(oc.id),\'\'),IFNULL(get_identifier_name(oc.id),\'\'),'
		,'IFNULL(ror.identifier_name,\'\'),IFNULL(DATE_FORMAT(ror.identification_date,\'%Y-%m-%dT%H:%i:%sZ\'),\'\'),'
		,'IFNULL(ror.collector_name,\'\'),IFNULL(oc.taxonomic_issue,\'\'),IFNULL(oc.geospatial_issue,\'\'),'
		,'IFNULL(oc.other_issue,\'\'),IFNULL(DATE_FORMAT(ror.created,\'%Y-%m-%dT%H:%i:%sZ\'),\'\'),IFNULL(DATE_FORMAT(ror.modified,\'%Y-%m-%dT%H:%i:%sZ\'),\'\'),'
		,'IFNULL(ror.citation,\'\'), IFNULL(ror.occurrence_remarks,\'\'), IFNULL(ror.location_remarks,\'\'),'
		,'IFNULL(ror.vernacular_name,\'\'), IFNULL(ror.record_number,\'\'), IFNULL(ror.identification_qualifier,\'\'), IFNULL(ror.individual_count,\'\'),'
		,'IFNULL(ror.geodetic_datum,\'\'), IFNULL(ror.generalised_metres, \'\'), IFNULL(ror.user_id, \'\'), IFNULL(dr.confidence,\'\')'
		,' FROM occurrence_record oc'
		,' INNER JOIN raw_occurrence_record ror ON ror.id = oc.id'
		,' INNER JOIN taxon_name tn ON tn.id = oc.taxon_name_id'
		,' INNER JOIN taxon_concept tc ON tc.id = oc.nub_concept_id'
		,' INNER JOIN rank rnk ON rnk.id = tc.rank'
		,' INNER JOIN data_provider dp ON dp.id = oc.data_provider_id'
		,' INNER JOIN data_resource dr ON dr.id = oc.data_resource_id'
		,' LEFT JOIN institution_code ic ON ic.id = oc.institution_code_id'
		,' LEFT JOIN collection_code cc ON cc.id = oc.collection_code_id'
		,' LEFT JOIN catalogue_number cn ON cn.id = oc.catalogue_number_id'
		,' LEFT JOIN basis_of_record bor ON bor.id = oc.basis_of_record'
		,' LEFT JOIN inst_coll_mapping icm on icm.institution_code_id = oc.institution_code_id and icm.collection_code_id = oc.collection_code_id'
		,' where dr.release_flag and oc.id >= ',low,' and oc.id < ', high
		,' INTO OUTFILE '
		,"'",'/data/bie-staging/biocache/tmp/file.out.', low,"'"
		,' FIELDS TERMINATED BY \'\,\' OPTIONALLY ENCLOSED BY \'"\'');
		PREPARE stmt1 FROM @cmd;
		EXECUTE stmt1;
		Deallocate prepare stmt1;
                select concat('Finished processing ', low ,' ', now()) as debug;

                select IFNULL(min(id),0) into newlow
                from occurrence_record where id >= high;


                IF newlow>0 THEN
                        call dump_bio_occurrences(newlow, newlow + 700000);
                END IF;

		
	END;
	$$

	
DELIMITER ;

--start at the lowest id for an occurrence record
call dump_bio_occurrences(34588487, 35588487);
