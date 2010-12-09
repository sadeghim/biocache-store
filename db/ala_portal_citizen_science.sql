-- Run this script on the portal database

DROP TABLE sequences;

CREATE TABLE sequences (
`name` varchar(24) not null primary key,
`curvalue` int(11) unsigned not null
)engine=MyISAM default charset=utf8;

insert into sequences values('cs', 0);

update sequences set curvalue= (select IFNULL(max(id),0) from raw_occurrence_record where id < 34000000);



delimiter $$

-- function to get next value in a sequence table
DROP FUNCTION IF EXISTS nextval$$
create function nextval(seq_name text)
returns int(11)
not deterministic
reads sql data
begin
declare r_curvalue int;

select curvalue +1 into r_curvalue from sequences where `name` = seq_name;

update sequences set curvalue = r_curvalue where `name` = seq_name;

return r_curvalue;
end$$

-- A procedure that adds a citizen science record to the raw occurrence record table.
-- It performs a lock
-- Obtains the ID for the next citizen science record
-- Inserts the new record into the table.
-- NB For now on Citizen science records will be given id's from 1 to 34 million
-- If it looks like we are going to reach this threshold something else will need to be done.
--
-- Natasha Carter 2010-11-11
--
-- args:
-- o_id - is the out param that will be used to return the identification of the new record
-- all other param represent the input vcalues for the various columns of the raw_occurrence_record table.
DROP PROCEDURE IF EXISTS addCitizenScienceRecord$$
CREATE PROCEDURE addCitizenScienceRecord(OUT o_id INT(11),
                                   IN i_data_provider_id INT(11),
                                   IN i_data_resource_id INT(11),
                                   IN i_resource_access_point_id INT(11),
                                   IN i_institution_code VARCHAR(255),
                                   IN i_collection_code VARCHAR(255),
                                   IN i_catalogue_number VARCHAR(255),
                                   IN i_scientific_name VARCHAR(255),
                                   IN i_author VARCHAR(255),
                                   IN i_rank VARCHAR(50),
                                   IN i_vernacular_name varchar(255),
                                   IN i_kingdom varchar(150),
                                   IN i_phylum varchar(150),
                                   IN i_class varchar(250),
                                   IN i_order_rank varchar(50),
                                   IN i_family varchar(250),
                                   IN i_genus varchar(150),
                                   IN i_species varchar(150),
                                   IN i_subspecies varchar(150),
                                   IN i_latitude varchar(50) ,
                                   IN i_longitude varchar(50),
                                   IN i_lat_long_precision varchar(50),
                                   IN i_min_altitude varchar(50),
                                   in i_max_altitude varchar(50),
                                   IN i_altitude_precision varchar(50),
                                   in i_min_depth varchar(50),
                                   IN i_max_depth varchar(50),
                                   IN i_depth_precision varchar(50),
                                   IN i_continent_ocean varchar(100),
                                   IN i_country varchar(100),
                                   IN i_state_province varchar(100),
                                   IN i_county varchar(100),
                                   IN i_collector_name varchar(255),
                                   IN i_locality text,
                                   IN i_year varchar(50),
                                   IN i_month varchar(50),
                                   IN i_day varchar(50),
                                   IN i_event_date varchar(255),
                                   IN i_event_time char(5),
                                   IN i_basis_of_record varchar(100),
                                   IN i_identifier_name varchar(255),
                                   IN i_identification_date datetime,
                                   IN i_unit_qualifier varchar(255),
                                   In i_created timestamp,
                                   IN i_modified timestamp,
                                   in i_taxon_concept_guid varchar(255),
                                   IN i_user_id varchar(255),
                                   IN i_occurrence_remarks text,
                                   IN i_location_remarks text,
                                   IN i_individual_count INT(11),
                                   IN i_citation text,
                                   IN i_geodetic_datum varchar(50),
                                   IN i_generalised_metres int(11),
                                   IN i_guid varchar(255)
                                   
                                    )
BEGIN

   IF get_lock('cit_science', 5) THEN
        -- get the next id
         select nextval('cs') into o_id;

        -- insert the new record

        INSERT INTO raw_occurrence_record(
            id,
            data_provider_id,
            data_resource_id,
            resource_access_point_id,
            institution_code,
            collection_code,
            catalogue_number,
            scientific_name,
            author,
            rank,
            vernacular_name,
kingdom,
phylum,
class,
order_rank,
family,
genus,
species,
subspecies,
latitude,
longitude,
lat_long_precision,
min_altitude,
max_altitude,
altitude_precision,
min_depth,
max_depth,
depth_precision,
continent_ocean,
country,
state_province,
county,
collector_name,
locality,
year,
month,
day,
event_date,
event_time,
basis_of_record,
identifier_name,
identification_date,
unit_qualifier,
created,
modified,
taxon_concept_guid,
user_id,
occurrence_remarks,
location_remarks,
individual_count,
citation,
geodetic_datum,
generalised_metres,
guid
        )
        VALUES(
            o_id,
            i_data_provider_id,
i_data_resource_id,
i_resource_access_point_id,
i_institution_code,
i_collection_code,
i_catalogue_number,
i_scientific_name,
i_author,
i_rank,
i_vernacular_name,
i_kingdom,
i_phylum,
i_class,
i_order_rank,
i_family,
i_genus,
i_species,
i_subspecies,
i_latitude,
i_longitude,
i_lat_long_precision,
i_min_altitude,
i_max_altitude,
i_altitude_precision,
i_min_depth,
i_max_depth,
i_depth_precision,
i_continent_ocean,
i_country,
i_state_province,
i_county,
i_collector_name,
i_locality,
i_year,
i_month,
i_day,
i_event_date,
i_event_time,
i_basis_of_record,
i_identifier_name,
i_identification_date,
i_unit_qualifier,
i_created,
i_modified,
i_taxon_concept_guid,
i_user_id,
i_occurrence_remarks,
i_location_remarks,
i_individual_count,
i_citation,
i_geodetic_datum,
i_generalised_metres,
i_guid
        );
        -- release the lock
        SELECT release_lock('cit_science');
   ELSE
       set  o_id = -1;
   END IF;

END$$

delimiter ;


-- grant the necessary permissions to the biocache user

grant execute on procedure portal.addCitizenScienceRecord TO biocache;

grant execute on function portal.nextval TO biocache;

flush privileges;