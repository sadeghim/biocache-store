package org.ala.biocache.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.ala.biocache.model.ImageRecord;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;


public class ImageRecordDAOImpl extends JdbcDaoSupport implements ImageRecordDAO {
	/**
	 * The create SQL 
	 */	
	protected static final String CREATE_SQL = "insert into image_record (" +
												"data_resource_id," +
												"occurrence_id," +
												"taxon_concept_id," +
												"raw_image_type," +
												"image_type," +
												"url," +
												"description," +
												"rights," +
												"html_for_display) " +
												"values (?,?,?,?,?,?,?,?,?)";
	
	/**
	 * The update SQL 
	 */	
	protected static final String UPDATE_SQL = "update image_record set " +
												"data_resource_id=?," +
												"occurrence_id=?," +
												"taxon_concept_id=?," +
												"raw_image_type=?," +
												"image_type=?," +
												"url=?," +
												"description=?," +
												"rights=?," +
												"html_for_display=? where id=?";
	
	/**
	 * The query by occurrence record id
	 */
	protected static final String QUERY_BY_OCCURRENCE_ID_SQL = 
		"select id,data_resource_id,occurrence_id,taxon_concept_id,raw_image_type,image_type,url,description,rights,html_for_display " + 
		"from image_record rir " +
		"where occurrence_id=?";
	
	/**
	 * The query by data resource id, taxon concept id and url
	 */
	protected static final String QUERY_BY_DATA_RESOURCE_TAXON_CONCEPT_URL_SQL = 
		"select id,data_resource_id,occurrence_id,taxon_concept_id,raw_image_type,image_type,url,description,rights,html_for_display " + 
		"from image_record rir " +
		"where data_resource_id=? and taxon_concept_id=? and url=?";
	
	/**
	 * The delete
	 */
	protected static final String DELETE_SQL = 
		"delete from image_record where id=?";
	
	/**
	 * The "has some" for resource access point 
	 */
	protected static final String RAP_HAS_RECORDS_SQL = 
		"select r.id as id from image_record i inner join raw_occurrence_record r on i.occurrence_id=r.id where r.resource_access_point_id=? limit 1";
	
	/**
	 * ImageRecord row mapper
	 */
	protected ImageRecordRowMapper ImageRecordRowMapper = new ImageRecordRowMapper(); 
	protected LongRowMapper longRowMapper = new LongRowMapper();
	
	/**
	 * Utility to create a long for a row 
	 * @author trobertson
	 */
	protected class LongRowMapper implements RowMapper {
		/**
		 * The factory
		 */
		public Long mapRow(ResultSet rs, int rowNumber) throws SQLException {
			return new Long(rs.getLong("id"));
		}
	}	
	/**
	 * Utility to create a ImageRecord for a row 
	 * @author trobertson
	 */
	protected class ImageRecordRowMapper implements RowMapper {
		/**
		 * The factory
		 */
		public ImageRecord mapRow(ResultSet rs, int rowNumber) throws SQLException {
			return new ImageRecord(
					rs.getLong("id"),
					rs.getLong("data_resource_id"),
					rs.getLong( "occurrence_id"),
					rs.getLong("taxon_concept_id"),
					rs.getString("raw_image_type"),
					rs.getInt("image_type"),
					rs.getString("url"),
					rs.getString("description"),
					rs.getString("rights"),
					rs.getString("html_for_display"));
		}
	}	
	
	/**
	 * @see org.gbif.portal.dao.ImageRecordDAO#create(org.gbif.portal.model.ImageRecord)
	 */
	public long create(final ImageRecord imageRecord) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(
			new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
					PreparedStatement ps = conn.prepareStatement(ImageRecordDAOImpl.CREATE_SQL);
					ps.setLong(1, imageRecord.getDataResourceId());
					ps.setObject(2, imageRecord.getOccurrenceId());
					ps.setLong(3, imageRecord.getTaxonConceptId());
					ps.setString(4, imageRecord.getRawImageType());
					ps.setInt(5, imageRecord.getImageType());
					ps.setString(6, imageRecord.getUrl());
					ps.setString(7, imageRecord.getDescription());
					ps.setString(8, imageRecord.getRights());
					ps.setString(9, imageRecord.getHtmlForDisplay());
					return ps;
				}					
			},
			keyHolder
		);
		imageRecord.setId(keyHolder.getKey().longValue());
		return keyHolder.getKey().longValue();
	}

	/**
	 * @see org.gbif.portal.dao.ImageRecordDAO#updateOrCreate(org.gbif.portal.model.ImageRecord)
	 */
	public long updateOrCreate(final ImageRecord imageRecord) {
		if (imageRecord.getId()<=0) {
			return create(imageRecord);
		}  else {
			getJdbcTemplate().update(
				new PreparedStatementCreator() {
					public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
					   PreparedStatement ps = conn.prepareStatement(ImageRecordDAOImpl.UPDATE_SQL);
						ps.setLong(1, imageRecord.getDataResourceId());
						ps.setLong(2, imageRecord.getOccurrenceId());
						ps.setLong(3, imageRecord.getTaxonConceptId());
						ps.setString(4, imageRecord.getRawImageType());
						ps.setInt(5, imageRecord.getImageType());
						ps.setString(6, imageRecord.getUrl());
						ps.setString(7, imageRecord.getDescription());
						ps.setString(8, imageRecord.getRights());
						ps.setString(9, imageRecord.getHtmlForDisplay());
					    ps.setLong(10, imageRecord.getId());
					   return ps;
					}					
				}
			);
			return imageRecord.getId();	
		}
	}
	
	/**
	 * @see org.gbif.portal.dao.ImageRecordDAO#findByOccurrenceId(long)
	 */
	@SuppressWarnings("unchecked")
	public List<ImageRecord> findByOccurrenceId(final long occurrenceId) {
		List<ImageRecord> results = (List<ImageRecord>) getJdbcTemplate()
			.query(ImageRecordDAOImpl.QUERY_BY_OCCURRENCE_ID_SQL,
				new Object[] {occurrenceId},
				new RowMapperResultSetExtractor(ImageRecordRowMapper));
		return results;
	}

	/**
	 * @see org.gbif.portal.dao.ImageRecordDAO#getCreatedOrModifiedSince(java.util.Date)
	 */
	@SuppressWarnings("unchecked")
	public void delete(final ImageRecord ImageRecord) {
		if (ImageRecord != null && ImageRecord.getId()>0) {
			getJdbcTemplate()
				.update(ImageRecordDAOImpl.DELETE_SQL,
					new Object[] {ImageRecord.getId()});
		}
	}

	/**
	 * @return Returns the ImageRecordRowMapper.
	 */
	public ImageRecordRowMapper getImageRecordRowMapper() {
		return ImageRecordRowMapper;
	}

	/**
	 * @param ImageRecordRowMapper The ImageRecordRowMapper to set.
	 */
	public void setImageRecordRowMapper(
			ImageRecordRowMapper ImageRecordRowMapper) {
		this.ImageRecordRowMapper = ImageRecordRowMapper;
	}

	@SuppressWarnings("unchecked")
	public ImageRecord findByDataResourceAndTaxonConceptAndUrl(long dataResourceId, long taxonConceptId, String url) {
		List<ImageRecord> results = (List<ImageRecord>) getJdbcTemplate()
		.query(ImageRecordDAOImpl.QUERY_BY_DATA_RESOURCE_TAXON_CONCEPT_URL_SQL,
			new Object[] {dataResourceId, taxonConceptId, url},
			new RowMapperResultSetExtractor(ImageRecordRowMapper));
		if (results.size() == 0) {
			return null;
		} else if (results.size()>1) {
			logger.warn("Found multiple matching ImageRecords for data resource id: " + dataResourceId
				    + " taxon concept id: " + taxonConceptId
				    + " url: " + url);
		}
		return results.get(0);
	}

	@SuppressWarnings("unchecked")
	public boolean hasImageRecords(long resourceAccessPointId) {
		List<Long> results = (List<Long>) getJdbcTemplate()
		.query(ImageRecordDAOImpl.RAP_HAS_RECORDS_SQL,
			new Object[] {resourceAccessPointId},
			new RowMapperResultSetExtractor(longRowMapper));
		if (results.size() == 0) {
			return false;
		} else  {
			return true;
		}
	}
}