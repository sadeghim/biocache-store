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
import org.ala.biocache.model.DataResource;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

/**
 * Pure JDBC implementation taken from the GBIF portal code base.
 * 
 * @author tim
 */

public class DataResourceDAOImpl extends JdbcDaoSupport implements
		DataResourceDAO {
	
	/**
	 * The create sql
	 */
	protected static final String CREATE_SQL = "insert into data_resource(" +
												"data_provider_id," +
												"description," +
												"name," +
												"display_name," +
												"rights," +
												"citation," +
												"citable_agent," +
												"website_url," +
												"logo_url," +
												"basis_of_record," +
												"root_taxon_rank," +
												"root_taxon_name," +
												"scope_continent_code," +
												"scope_country_code," +
												"provider_record_count, " +
												"taxonomic_priority, " +
												"created," +
												"modified," +
												"lock_display_name, " +
												"lock_citable_agent, " +
												"lock_basis_of_record) " +
												"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	/**
	 * The update sql
	 */
	protected static final String UPDATE_SQL = "update data_resource set " +
												"data_provider_id=?," +
												"description=?," +
												"name=?," +
												"display_name=?," +
												"rights=?," +
												"citation=?," +
												"citable_agent=?," +
												"website_url=?," +
												"logo_url=?," +
												"basis_of_record=?," +
												"root_taxon_rank=?," +
												"root_taxon_name=?," +
												"scope_continent_code=?," +
												"scope_country_code=?," +
												"provider_record_count=?, " +
												"taxonomic_priority=?, " +
												"modified=?, " +
												"lock_display_name=?, " +
												"lock_citable_agent=?, " +
												"lock_basis_of_record=? " +
												"where id=?";

	/**
	 * The query all sql
	 */
	protected static final String QUERY_ALL_SQL = "select id, data_provider_id, name, display_name, description, rights, citation, citable_agent, website_url, logo_url, basis_of_record, root_taxon_rank, root_taxon_name, scope_continent_code, scope_country_code, provider_record_count, taxonomic_priority, created, modified, deleted, lock_display_name, lock_citable_agent, lock_basis_of_record " +
		"from data_resource";
	
	/**
	 * The query by ID sql
	 */
	protected static final String QUERY_BY_ID = 
		"select id, data_provider_id, name, display_name, description, rights, citation, citable_agent, website_url, logo_url, basis_of_record, root_taxon_rank, root_taxon_name, scope_continent_code, scope_country_code, provider_record_count, taxonomic_priority, created, modified, deleted, lock_display_name, lock_citable_agent, lock_basis_of_record " +
		"from data_resource where id=?";

	/**
	 * The get by data provider id 
	 */
	protected static final String QUERY_BY_ID_PROVIDER = 
		"select id, data_provider_id, name, display_name, description, rights, citation, citable_agent, website_url, logo_url, basis_of_record, root_taxon_rank, root_taxon_name, scope_continent_code, scope_country_code, provider_record_count, taxonomic_priority, created, modified, deleted, lock_display_name, lock_citable_agent, lock_basis_of_record " +
		"from data_resource where data_provider_id=?";
	

	/**
	 * The get by name and provider
	 */
	protected static final String QUERY_BY_NAME_PROVIDER = 
		"select id, data_provider_id, name, display_name, description, rights, citation, citable_agent, website_url, logo_url, basis_of_record, root_taxon_rank, root_taxon_name, scope_continent_code, scope_country_code, provider_record_count, taxonomic_priority, created, modified, deleted, lock_display_name, lock_citable_agent, lock_basis_of_record " +
		"from data_resource where name=? and data_provider_id=?";
	
	/**
	 * The get by name, url and provider
	 */
	protected static final String QUERY_BY_NAME_URL_PROVIDER = 
		"select dr.id, dr.data_provider_id, dr.name, dr.display_name, dr.description, dr.rights, dr.citation, dr.citable_agent, dr.website_url, dr.logo_url, dr.basis_of_record, dr.root_taxon_rank, dr.root_taxon_name, dr.scope_continent_code, dr.scope_country_code, dr.provider_record_count, dr.taxonomic_priority, dr.created, dr.modified, dr.deleted, dr.lock_display_name, dr.lock_citable_agent, dr.lock_basis_of_record " +
		"from data_resource dr inner join resource_access_point rap on rap.data_resource_id = dr.id where dr.name=? and dr.data_provider_id=? and rap.url=?";

	/**
	 * The get by remote_id_at_url, url and provider
	 */
	protected static final String QUERY_BY_REMOTE_ID_URL_PROVIDER = 
		"select dr.id, dr.data_provider_id, dr.name, dr.display_name, dr.description, dr.rights, dr.citation, dr.citable_agent, dr.website_url, dr.logo_url, dr.basis_of_record, dr.root_taxon_rank, dr.root_taxon_name, dr.scope_continent_code, dr.scope_country_code, dr.provider_record_count, dr.taxonomic_priority, dr.created, dr.modified, dr.deleted, dr.lock_display_name, dr.lock_citable_agent, dr.lock_basis_of_record " +
		"from data_resource dr inner join resource_access_point rap on rap.data_resource_id = dr.id where rap.remote_id_at_url=? and dr.data_provider_id=? and rap.url=?";

	/**
	 * Reusable row mapper
	 */
	protected DataResourceRowMapper dataResourceRowMapper = new DataResourceRowMapper();
	
	/**
	 * Utility to create a DataResource for a row 
	 * @author trobertson
	 */
	protected class DataResourceRowMapper implements RowMapper {
		/**
		 * The factory
		 */
		public DataResource mapRow(ResultSet rs, int rowNumber) throws SQLException {
			return new DataResource(rs.getLong("id"),
					rs.getLong("data_provider_id"),
					rs.getString("name"),
					rs.getString("display_name"),
					rs.getString("description"),
					rs.getString("rights"),
					rs.getString("citation"),
					rs.getString("citable_agent"),
					rs.getString("website_url"),
					rs.getString("logo_url"),
					rs.getInt("basis_of_record"),
					rs.getInt("root_taxon_rank"),
					rs.getString("root_taxon_name"),
					rs.getString("scope_continent_code"),
					rs.getString("scope_country_code"),
					(Integer) rs.getObject("provider_record_count"),
					rs.getInt("taxonomic_priority"),
					rs.getDate("created"),
					rs.getDate("modified"),
					rs.getDate("deleted"),
					rs.getBoolean("lock_display_name"),
					rs.getBoolean("lock_citable_agent"),
					rs.getBoolean("lock_basis_of_record"));			
		}
	}

	/**
	 * @see org.gbif.portal.dao.DataResourceDAO#create(org.gbif.portal.model.DataResource)
	 */
	public long create(final DataResource dataResource) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getJdbcTemplate().update(
			new PreparedStatementCreator() {
				Timestamp createTime = new Timestamp(System.currentTimeMillis());
				public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
					   PreparedStatement ps = conn.prepareStatement(DataResourceDAOImpl.CREATE_SQL);
					   ps.setLong(1, dataResource.getDataProviderId());
					   ps.setString(2, dataResource.getDescription());
					   ps.setString(3, dataResource.getName());
					   ps.setString(4, dataResource.getDisplayName() != null ? dataResource.getDisplayName() : dataResource.getName());
					   ps.setString(5, dataResource.getRights());
					   ps.setString(6, dataResource.getCitation());
					   ps.setString(7, dataResource.getCitableAgent());
					   ps.setString(8, dataResource.getWebsiteUrl());
					   ps.setString(9, dataResource.getLogoUrl());
					   ps.setInt(10, dataResource.getBasisOfRecord());
					   ps.setInt(11, dataResource.getRootTaxonRank());
					   ps.setString(12, dataResource.getRootTaxonName());
					   ps.setString(13, dataResource.getScopeContinentCode());
					   ps.setString(14, dataResource.getScopeCountryCode());
					   ps.setObject(15, dataResource.getProviderRecordCount());
					   ps.setObject(16, dataResource.getTaxonomicPriority());
					   ps.setTimestamp(17, createTime);
					   ps.setTimestamp(18, createTime);
					   ps.setBoolean(19, dataResource.getLockDisplayName());
					   ps.setBoolean(20, dataResource.getLockCitableAgent());
					   ps.setBoolean(21, dataResource.getLockBasisOfRecord());
					   return ps;
				}					
			},
			keyHolder
		);
		dataResource.setId(keyHolder.getKey().longValue());
		return keyHolder.getKey().longValue();
	}

	/**
	 * @see org.gbif.portal.dao.DataProviderDAO#getById(long)
	 */
	@SuppressWarnings("unchecked")
	public DataResource getById(long id) {
		List<DataResource> results = (List<DataResource>) getJdbcTemplate()
		.query(DataResourceDAOImpl.QUERY_BY_ID,
				new Object[] {id},
				new RowMapperResultSetExtractor(dataResourceRowMapper, 1));
		if (results.size() == 0) {
			return null;
		} else if (results.size()>1) {
			logger.warn("Found multiple DataResources with ID: " + id);
		}
		return results.get(0);
	}
	
	/**
	 * @see org.gbif.portal.dao.DataResourceDAO#getAll()
	 */
	@SuppressWarnings("unchecked")
	public List<DataResource> getAll() {
		List<DataResource> results = (List<DataResource>) getJdbcTemplate()
		.query(DataResourceDAOImpl.QUERY_ALL_SQL,
				new RowMapperResultSetExtractor(dataResourceRowMapper, 1));
		if (results.size() == 0) {
			return null;
		} else {
			return results;
		}
	}
	
	/**
	 * @see org.gbif.portal.dao.DataProviderDAO#getByDataProviderId(long)
	 */
	@SuppressWarnings("unchecked")
	public List<DataResource> getByDataProviderId(long id) {
		List<DataResource> results = (List<DataResource>) getJdbcTemplate()
		.query(DataResourceDAOImpl.QUERY_BY_ID_PROVIDER,
				new Object[] {id},
				new RowMapperResultSetExtractor(dataResourceRowMapper, 1));
		if (results.size() == 0) {
			return null;
		} else {
			return results;
		}
	}

	/**
	 * @see org.gbif.portal.dao.DataResourceDAO#getByNameForProvider(java.lang.String, long)
	 */
	@SuppressWarnings("unchecked")
	public DataResource getByNameForProvider(String name, long dataProviderId) {
		List<DataResource> results = (List<DataResource>) getJdbcTemplate()
		.query(DataResourceDAOImpl.QUERY_BY_NAME_PROVIDER,
				new Object[] {name, dataProviderId},
				new RowMapperResultSetExtractor(dataResourceRowMapper, 1));
		if (results.size() == 0) {
			return null;
		} else if (results.size()>1) {
			logger.warn("Found multiple DataResources with name[" + name + "] and dataProviderId[" + dataProviderId + "]");
		}
		return results.get(0);
	}

	/**
	 * @see org.gbif.portal.dao.DataResourceDAO#getByNameAndUrlForProvider(java.lang.String, long)
	 */
	@SuppressWarnings("unchecked")
	public DataResource getByNameAndUrlForProvider(final String name, final String url, final long dataProviderId) {
		List<DataResource> results = (List<DataResource>) getJdbcTemplate()
		.query(DataResourceDAOImpl.QUERY_BY_NAME_URL_PROVIDER,
				new Object[] {name, dataProviderId, url},
				new RowMapperResultSetExtractor(dataResourceRowMapper, 1));
		if (results.size() == 0) {
			return null;
		} else if (results.size()>1) {
			logger.warn("Found multiple DataResources with name[" + name + "], url[" + url + "] and dataProviderId[" + dataProviderId + "]");
		}
		return results.get(0);
	}

	public long updateOrCreate(final DataResource dataResource) {
		if (dataResource.getId() <= 0) {
			return create(dataResource);
		} else {
			getJdbcTemplate().update(
					new PreparedStatementCreator() {
						public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
							   PreparedStatement ps = conn.prepareStatement(DataResourceDAOImpl.UPDATE_SQL);
							   ps.setLong(1, dataResource.getDataProviderId());
							   ps.setString(2, dataResource.getDescription());
							   ps.setString(3, dataResource.getName());
							   ps.setString(4, dataResource.getDisplayName() != null ? dataResource.getDisplayName() : dataResource.getName());
							   ps.setString(5, dataResource.getRights());
							   ps.setString(6, dataResource.getCitation());
							   ps.setString(7, dataResource.getCitableAgent());
							   ps.setString(8, dataResource.getWebsiteUrl());
							   ps.setString(9, dataResource.getLogoUrl());
							   ps.setInt(10, dataResource.getBasisOfRecord());
							   ps.setInt(11, dataResource.getRootTaxonRank());
							   ps.setString(12, dataResource.getRootTaxonName());
							   ps.setString(13, dataResource.getScopeContinentCode());
							   ps.setString(14, dataResource.getScopeCountryCode());
							   ps.setObject(15, dataResource.getProviderRecordCount());
							   ps.setObject(16, dataResource.getTaxonomicPriority());
							   ps.setTimestamp(17, new Timestamp(System.currentTimeMillis()));
							   ps.setBoolean(18, dataResource.getLockDisplayName());
							   ps.setBoolean(19, dataResource.getLockCitableAgent());
							   ps.setBoolean(20, dataResource.getLockBasisOfRecord());
							   ps.setLong(21, dataResource.getId());							   
							   return ps;
						}					
					}
			);
			
			return dataResource.getId();
		}
	}

	/**
	 * @see org.gbif.portal.dao.DataResourceDAO#getByRemoteIdAtUrlAndUrlForProvider(java.lang.String, java.lang.String, long)
	 */
	@SuppressWarnings("unchecked")
	public DataResource getByRemoteIdAtUrlAndUrlForProvider(final String remoteId, final String url, final long dataProviderId) {
		List<DataResource> results = (List<DataResource>) getJdbcTemplate()
		.query(DataResourceDAOImpl.QUERY_BY_REMOTE_ID_URL_PROVIDER,
				new Object[] {remoteId, dataProviderId, url},
				new RowMapperResultSetExtractor(dataResourceRowMapper, 1));
		if (results.size() == 0) {
			return null;
		} else if (results.size()>1) {
			logger.warn("Found multiple DataResources with remoteIdAtUrl[" + remoteId + "], url[" + url + "] and dataProviderId[" + dataProviderId + "]");
		}
		return results.get(0);
	}
}