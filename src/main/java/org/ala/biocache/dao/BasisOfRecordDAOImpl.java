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

import javax.inject.Inject;
import javax.sql.DataSource;

import org.ala.biocache.model.BasisOfRecord;
import org.ala.biocache.model.DataProvider;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

/**
 * Pure JDBC implementation
 * @author Tommy Wang (Tommy.wang@csiro.au)
 */
public class BasisOfRecordDAOImpl extends JdbcDaoSupport implements
	BasisOfRecordDAO {	
	
	/**
	 * The query by ID sql
	 */
	protected static final String QUERY_BY_ID_SQL = "select id,version,description from basis_of_record where id=?";
	
	/**
	 * The query all sql
	 */
	protected static final String QUERY_ALL_SQL = "select id,version,description from basis_of_record";
	
	/**
	 * Reusable row mapper
	 */
	protected BasisOfRecordRowMapper basisOfRecordRowMapper = new BasisOfRecordRowMapper();
	
	/**
	 * Utility to create a DataProvider for a row 
	 * @author trobertson
	 */
	protected class BasisOfRecordRowMapper implements RowMapper {
		/**
		 * The factory
		 */
		public BasisOfRecord mapRow(ResultSet rs, int rowNumber) throws SQLException {
			return new BasisOfRecord(rs.getLong("id"),
					rs.getLong("version"),
					rs.getString("description"));
		}
	}

	/**
	 * @see org.gbif.portal.dao.BasisOfRecordDAO#getById(long)
	 */
	@SuppressWarnings("unchecked") 
	public BasisOfRecord getById(long id) {
		List<BasisOfRecord> results = (List<BasisOfRecord>) getJdbcTemplate()
		.query(BasisOfRecordDAOImpl.QUERY_BY_ID_SQL,
				new Object[] {id},
				new RowMapperResultSetExtractor(basisOfRecordRowMapper, 1));
		if (results.size() == 0) {
			return null;
		} else if (results.size()>1) {
			logger.warn("Found multiple BasisOfRecord with ID: " + id);
		}
		return results.get(0);
	}
	
	/**
	 * @see org.gbif.portal.dao.BasisOfRecordDAO#getAll()
	 */
	@SuppressWarnings("unchecked") 
	public List<BasisOfRecord> getAll() {
		List<BasisOfRecord> results = (List<BasisOfRecord>) getJdbcTemplate()
		.query(BasisOfRecordDAOImpl.QUERY_ALL_SQL,
				new RowMapperResultSetExtractor(basisOfRecordRowMapper, 1));
		if (results.size() == 0) {
			return null;
		} else {
			return results;
		}
		
	}
}
