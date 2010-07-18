/***************************************************************************
 * Copyright (C) 2005 Global Biodiversity Information Facility Secretariat.
 * All Rights Reserved.
 *
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ***************************************************************************/
package org.ala.biocache.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.ala.biocache.model.DataProvider;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

/**
 * Pure JDBC implementation taken from the GBIF portal code base.
 * 
 * @author Tommy Wang (Tommy.wang@csiro.au)
 */
public class DataProviderDAOImpl extends JdbcDaoSupport implements
		DataProviderDAO {	
	
	/**
	 * The create sql
	 */
	protected static final String CREATE_SQL = "insert into data_provider(" +
												"name," +
												"description," +
												"address," +
												"website_url," +
												"logo_url," +
												"email," +
												"telephone," +
												"uuid," +
												"iso_country_code," +
												"gbif_approver," +
												"created," +
												"modified," +
												"lock_description, " +
												"lock_iso_country_code) " +
												"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	/**
	 * The update sql
	 */
	protected static final String UPDATE_SQL = "update data_provider set " +
												"name=?, " +
												"description=?, " +
												"address=?, " +
												"website_url=?, " +
												"logo_url=?, " +
												"email=?, " +
												"telephone=?, " +
												"uuid=?, " +
												"iso_country_code=?, " +
												"gbif_approver=?, " +
												"modified=?," +
												"lock_description=?, " +
												"lock_iso_country_code=? " +
												"where id=?";

	/**
	 * The delete sql
	 */
	protected static final String DELETE_SQL = "update data_provider set deleted=? where id = ?";
	
	/**
	 * The query by ID sql
	 */
	protected static final String QUERY_BY_ID_SQL = "select id, name, description, address, website_url, logo_url, email, " +
	"telephone, uuid, iso_country_code, gbif_approver, created, modified, deleted, lock_description, lock_iso_country_code from data_provider where id=?";
	
	/**
	 * The query all dataset by ID sql
	 */
	protected static final String QUERY_ALL_DATASET_BY_ID_SQL = "select dp.id, dp.name, dp.description, dp.address, dp.website_url, dp.logo_url, dp.email, " +
			"dp.telephone, dp.uuid, dp.iso_country_code, dp.gbif_approver, dp.created, dp.modified, dp.deleted, dp.lock_description, dp.lock_iso_country_code, " +
			"dr.name from data_provider dp LEFT JOIN data_resource dr on dr.data_provider_id=dp.id where dp.id=?";

	/**
	 * The query by UUID sql
	 */
	protected static final String QUERY_BY_UUID_SQL = "select id, name, description, address, website_url, logo_url, email, " +
	"telephone, uuid, iso_country_code, gbif_approver, created, modified, deleted, lock_description, lock_iso_country_code from data_provider where uuid=?";
	
	/**
	 * The query by NAME sql
	 */
	protected static final String QUERY_BY_NAME_SQL = "select id, name, description, address, website_url, logo_url, email, " +
	"telephone, uuid, iso_country_code, gbif_approver, created, modified, deleted, lock_description, lock_iso_country_code from data_provider where name=?";
	
	/**
	 * Reusable row mapper
	 */
	protected DataProviderRowMapper dataProviderRowMapper = new DataProviderRowMapper();
	
	/**
	 * Utility to create a DataProvider for a row 
	 * @author trobertson
	 */
	protected class DataProviderRowMapper implements RowMapper {
		/**
		 * The factory
		 */
		public DataProvider mapRow(ResultSet rs, int rowNumber) throws SQLException {
			return new DataProvider(rs.getLong("id"),
					rs.getString("name"),
					rs.getString("description"),
					rs.getString("address"),
					rs.getString("website_url"),
					rs.getString("logo_url"),
					rs.getString("email"),
					rs.getString("telephone"),
					rs.getString("uuid"),
					rs.getString("iso_country_code"),
					rs.getString("gbif_approver"),
					rs.getDate("created"),
					rs.getDate("modified"),
					rs.getDate("deleted"),
					rs.getBoolean("lock_description"),
					rs.getBoolean("lock_iso_country_code"));
		}
	}
	
	/**
	 * @see org.gbif.portal.dao.DataProviderDAO#create(org.gbif.portal.model.DataProvider)
	 */
	public long create(final DataProvider dataProvider) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(
			new PreparedStatementCreator() {
				Timestamp createTime = new Timestamp(System.currentTimeMillis());
				public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
					   PreparedStatement ps = conn.prepareStatement(DataProviderDAOImpl.CREATE_SQL);
					   ps.setString(1, dataProvider.getName());
					   ps.setString(2, dataProvider.getDescription());
					   ps.setString(3, dataProvider.getAddress());
					   ps.setString(4, dataProvider.getWebsiteUrl());
					   ps.setString(5, dataProvider.getLogoUrl());
					   ps.setString(6, dataProvider.getEmail());
					   ps.setString(7, dataProvider.getTelephone());
					   ps.setString(8, dataProvider.getUuid());
					   ps.setString(9, dataProvider.getIsoCountryCode());
					   ps.setString(10, dataProvider.getGbifApprover());
					   ps.setTimestamp(11, createTime);
					   ps.setTimestamp(12, createTime);
					   ps.setBoolean(13, dataProvider.getLockDescription());
					   ps.setBoolean(14, dataProvider.getLockIsoCountryCode());
					   return ps;
				}					
			},
			keyHolder
		);
		dataProvider.setId(keyHolder.getKey().longValue());
		return keyHolder.getKey().longValue();
	}

	/**
	 * @see org.gbif.portal.dao.DataProviderDAO#getByUuid(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public DataProvider getByUuid(String uuid) {
		List<DataProvider> results = (List<DataProvider>) getJdbcTemplate()
		.query(DataProviderDAOImpl.QUERY_BY_UUID_SQL,
				new Object[] {uuid},
				new RowMapperResultSetExtractor(dataProviderRowMapper, 1));
		if (results.size() == 0) {
			return null;
		} else if (results.size()>1) {
			logger.warn("Found multiple DataProviders with UUID: " + uuid);
		}
		return results.get(0);
	}

	/**
	 * @see org.gbif.portal.dao.DataProviderDAO#getById(long)
	 */
	@SuppressWarnings("unchecked")
	public DataProvider getById(long id) {
		List<DataProvider> results = (List<DataProvider>) getJdbcTemplate()
		.query(DataProviderDAOImpl.QUERY_BY_ID_SQL,
				new Object[] {id},
				new RowMapperResultSetExtractor(dataProviderRowMapper, 1));
		if (results.size() == 0) {
			return null;
		} else if (results.size()>1) {
			logger.warn("Found multiple DataProviders with ID: " + id);
		}
		return results.get(0);
	}
	
	/**
	 * @see org.gbif.portal.dao.DataProviderDAO#getById(long)
	 */
	@SuppressWarnings("unchecked")
	public DataProvider getAllDatasetById(long id) {
		List<DataProvider> results = (List<DataProvider>) getJdbcTemplate()
		.query(DataProviderDAOImpl.QUERY_ALL_DATASET_BY_ID_SQL,
				new Object[] {id},
				new RowMapperResultSetExtractor(dataProviderRowMapper, 1));
		if (results.size() == 0) {
			return null;
		} else if (results.size()>1) {
			logger.warn("Found multiple DataProviders with ID: " + id);
		}
		return results.get(0);
	}

	/**
	 * @see org.gbif.portal.dao.DataProviderDAO#delete(long)
	 */
	public void delete(final long id) {
		getJdbcTemplate().update(
			new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
					   PreparedStatement ps = conn.prepareStatement(DataProviderDAOImpl.DELETE_SQL);
					   ps.setLong(1, id);
					   return ps;
				}					
			}
		);
	}

	/**
	 * @see org.gbif.portal.dao.DataProviderDAO#updateOrCreate(org.gbif.portal.model.DataProvider)
	 */
	public long updateOrCreate(final DataProvider dataProvider) {
		if (dataProvider.getId() <= 0) {
			return create(dataProvider);
		} else {
			getJdbcTemplate().update(
					new PreparedStatementCreator() {
						public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
							   PreparedStatement ps = conn.prepareStatement(DataProviderDAOImpl.UPDATE_SQL);
							   ps.setString(1, dataProvider.getName());
							   ps.setString(2, dataProvider.getDescription());
							   ps.setString(3, dataProvider.getAddress());
							   ps.setString(4, dataProvider.getWebsiteUrl());
							   ps.setString(5, dataProvider.getLogoUrl());
							   ps.setString(6, dataProvider.getEmail());
							   ps.setString(7, dataProvider.getTelephone());
							   ps.setString(8, dataProvider.getUuid());
							   ps.setString(9, dataProvider.getIsoCountryCode());
							   ps.setString(10, dataProvider.getGbifApprover());
							   ps.setTimestamp(11, new Timestamp(System.currentTimeMillis()));
							   ps.setBoolean(12, dataProvider.getLockDescription());
							   ps.setBoolean(13, dataProvider.getLockIsoCountryCode());
							   ps.setLong(14, dataProvider.getId());							   
							   return ps;
						}					
					}
			);
			
			return dataProvider.getId();
		}
	}

	/**
	 * @see org.gbif.portal.dao.DataProviderDAO#getByName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public DataProvider getByName(final String name) {
		List<DataProvider> results = (List<DataProvider>) getJdbcTemplate()
		.query(DataProviderDAOImpl.QUERY_BY_NAME_SQL,
				new Object[] {name},
				new RowMapperResultSetExtractor(dataProviderRowMapper, 1));
		if (results.size() == 0) {
			return null;
		} else if (results.size()>1) {
			logger.warn("Found multiple DataProviders with name: " + name);
		}
		return results.get(0);
	}
}
