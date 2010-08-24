package org.ala.biocache.dao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.ala.biocache.model.OccurrenceRecord;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

/**
 * A pure jdbc implementation
 * @author trobertson
 */
public class OccurrenceRecordDAOImpl extends JdbcDaoSupport implements OccurrenceRecordDAO {
	/**
	 * The create SQL 
	 */	
	protected static final String CREATE_SQL = "insert into occurrence_record(" +
		"id,"+
		"data_provider_id," +
		"data_resource_id," +
		"institution_code_id," +
		"collection_code_id," +
		"catalogue_number_id," +
		"taxon_concept_id," +
		"taxon_name_id," +
		"nub_concept_id," +
		"iso_country_code," +
		"latitude," +
		"longitude," +
		"altitude_metres," +
		"depth_centimetres," +												
		"cell_id," +
		"centi_cell_id," +
		"mod360_cell_id," +
		"year," +
		"month," +
		"occurrence_date," +
		"basis_of_record," +
		"taxonomic_issue," +
		"geospatial_issue," +
		"other_issue) " +
		"values (" +
		":id," +
		":dataProviderId," +
		":dataResourceId," +
		":institutionCodeId," +
		":collectionCodeId," +
		":catalogueNumberId," +
		":taxonConceptId," +
		":taxonNameId," +
		":nubConceptId," +
		":isoCountryCode," +
		":latitude," +
		":longitude," +
		":altitudeInMetres," +
		":depthInCentimetres," +												
		":cellId," +
		":centiCellId,"+
		":mod360CellId,"+
		":year," +
		":month," +
		":occurrenceDate," +
		":basisOfRecord," +
		":taxonomicIssue," +
		":geospatialIssue," +
		":otherIssue)";
	
	/**
	 * The update SQL 
	 */	
	protected static final String UPDATE_SQL = "update occurrence_record set " +
		"id=:id,"+
		"data_provider_id=:dataProviderId," +
		"data_resource_id=:dataResourceId," +
		"institution_code_id=:institutionCodeId," +
		"collection_code_id=:collectionCodeId," +
		"catalogue_number_id=:catalogueNumberId," +
		"taxon_concept_id=:taxonConceptId," +
		"taxon_name_id=:taxonNameId," +
		"nub_concept_id=:nubConceptId," +
		"iso_country_code=:isoCountryCode," +
		"latitude=:latitude," +
		"longitude=:longitude," +
		"altitude_metres=:altitudeInMetres," +
		"depth_centimetres=:depthInCentimetres," +												
		"cell_id=:cellId," +
		"centi_cell_id=:centiCellId," +
		"mod360_cell_id=:mod360CellId," +
		"year=:year," +
		"month=:month," +
		"occurrence_date=:occurrenceDate," +
		"modified=:modified," +
		"basis_of_record=:basisOfRecord," +
		"taxonomic_issue=:taxonomicIssue," +
		"geospatial_issue=:geospatialIssue," +
		"other_issue=:otherIssue where id=:id";
	
	/**
	 * The query by id
	 */
	protected static final String QUERY_BY_ID_SQL = 
		"select ore.id,ore.data_provider_id,ore.data_resource_id," +
			"ore.institution_code_id,ore.collection_code_id,ore.catalogue_number_id," +
			"taxon_concept_id,ore.taxon_name_id,ore.nub_concept_id,ore.iso_country_code," +
			"ore.latitude,ore.longitude,ore.altitude_metres, ore.depth_centimetres," +
			"ore.cell_id,ore.centi_cell_id,ore.mod360_cell_id,"+
			"ore.year,ore.month,ore.occurrence_date," +
			"ore.basis_of_record,ore.taxonomic_issue,ore.geospatial_issue,ore.other_issue," +
			"ore.modified,ore.deleted " +
		"from occurrence_record ore " +
		"where ore.id=?";

	/**
	 * OccurrenceRecord row mapper
	 */
	protected OccurrenceRecordRowMapper occurrenceRecordRowMapper = new OccurrenceRecordRowMapper();
	
	/**
	 * To make use of bean naming
	 */
	protected NamedParameterJdbcTemplate namedParameterTemplate;
	
	/**
	 * Utility to create a OccurrenceRecord for a row 
	 * @author trobertson
	 */
	protected class OccurrenceRecordRowMapper implements RowMapper {
		/**
		 * The factory
		 */
		public OccurrenceRecord mapRow(ResultSet rs, int rowNumber) throws SQLException {
			OccurrenceRecord or =  new OccurrenceRecord();
			or.setId(rs.getLong("ore.id"));
			or.setDataProviderId(rs.getLong("ore.data_provider_id"));
			or.setDataResourceId(rs.getLong("ore.data_resource_id"));
			or.setInstitutionCodeId(rs.getLong("ore.institution_code_id"));
			or.setCollectionCodeId(rs.getLong("ore.collection_code_id"));
			or.setCatalogueNumberId(rs.getLong("ore.catalogue_number_id"));
			or.setTaxonConceptId(rs.getLong("ore.taxon_concept_id"));
			or.setTaxonNameId(rs.getLong("ore.taxon_name_id"));
			or.setNubConceptId((Long) rs.getObject("ore.nub_concept_id"));
			or.setIsoCountryCode(rs.getString("ore.iso_country_code"));
			or.setLatitude((Float) rs.getObject("ore.latitude"));
			or.setLongitude((Float) rs.getObject("ore.longitude"));
			or.setAltitudeInMetres((Integer) rs.getObject("ore.altitude_metres"));
			or.setDepthInCentimetres((Integer) rs.getObject("ore.depth_centimetres"));
			or.setCellId((Integer) rs.getObject("ore.cell_id"));
			or.setCentiCellId((Integer) rs.getObject("ore.centi_cell_id"));
			or.setMod360CellId((Integer) rs.getObject("ore.mod360_cell_id"));
			or.setYear((Integer) rs.getObject("ore.year"));
			or.setMonth((Integer) rs.getObject("ore.month"));
			or.setOccurrenceDate(rs.getDate("ore.occurrence_date"));
			or.setBasisOfRecord(rs.getInt("ore.basis_of_record"));
			or.setTaxonomicIssue(rs.getInt("ore.taxonomic_issue"));
			or.setGeospatialIssue(rs.getInt("ore.geospatial_issue"));
			or.setOtherIssue(rs.getInt("ore.other_issue"));
			or.setDeleted(rs.getDate("deleted"));
			or.setModified(rs.getDate("modified"));
			return or;
		}
	}	
	
	/**
	 * @see org.gbif.portal.dao.OccurrenceRecordDAO#create(org.gbif.portal.model.OccurrenceRecord)
	 */
	public long create(final OccurrenceRecord occurrenceRecord) {
		SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(occurrenceRecord);
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getNamedParameterTemplate().update(
			OccurrenceRecordDAOImpl.CREATE_SQL,
			namedParameters,
			keyHolder);
		return occurrenceRecord.getId();
	}

	/**
	 * @see org.gbif.portal.dao.OccurrenceRecordDAO#update(org.gbif.portal.model.OccurrenceRecord)
	 */
	public long update(final OccurrenceRecord occurrenceRecord) {
		SqlParameterSource namedParameters = new BeanPropertySqlParameterSource(occurrenceRecord);
		getNamedParameterTemplate().update(
				OccurrenceRecordDAOImpl.UPDATE_SQL,
				namedParameters);
		return occurrenceRecord.getId();	
	}
	/**
	 * @return the namedParameterTemplate
	 */
	public NamedParameterJdbcTemplate getNamedParameterTemplate() {
		return namedParameterTemplate;
	}

	/**
	 * @param namedParameterTemplate the namedParameterTemplate to set
	 */
	public void setNamedParameterTemplate(
			NamedParameterJdbcTemplate namedParameterTemplate) {
		this.namedParameterTemplate = namedParameterTemplate;
	}

	/** 
	 * @see org.gbif.portal.dao.OccurrenceRecordDAO#getById(long)
	 */
	@SuppressWarnings("unchecked")
	public OccurrenceRecord getById(final long id) {
		List<OccurrenceRecord> results = (List<OccurrenceRecord>) getJdbcTemplate()
			.query(OccurrenceRecordDAOImpl.QUERY_BY_ID_SQL,
				new Object[]{id},
				new RowMapperResultSetExtractor(occurrenceRecordRowMapper, 1));
		if (results.size() == 0) {
			return null;
		} else if (results.size()>1) {
			logger.warn("Found multiple OccurrenceRecords with id[" + id + "]");
		}
		return results.get(0);
	}
}