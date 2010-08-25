package org.ala.biocache.dao;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.ala.biocache.model.TaxonConcept;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 * Pure JDBC implementation
 * @author trobertson
 */
public class TaxonConceptDAOImpl extends JdbcDaoSupport implements TaxonConceptDAO {

    protected static final String QUERY_BY_GUID = 
            "select tc.id,tc.guid,tc.lft,tc.rgt,tc.taxon_name_id,tc.parent_concept_id,tc.rank,tc.is_accepted,tc.partner_concept_id," +
            "tc.data_provider_id,tc.data_resource_id,tc.is_nub_concept,tc.is_secondary,tc.priority, " +
            "tn.canonical, cn.name, r.name " +
            "from taxon_concept tc " +
            "left join rank r ON r.id=tc.rank " +
            "left join taxon_name tn ON tn.id=tc.taxon_name_id " +
            "left join common_name cn ON tc.id=cn.taxon_concept_id " +
            "where tc.guid=? group by tc.id";
    
    /**
     * Reusable row mappers
     */
    protected TaxonConceptRowMapper tcRowMapper = new TaxonConceptRowMapper();
    
    /**
     * @see org.gbif.portal.dao.TaxonConceptDAO#getTaxonConcept(long)
     */
    @SuppressWarnings("unchecked")
    public TaxonConcept getByGuid(final String guid) {
        List<TaxonConcept> results = (List<TaxonConcept>)
         getJdbcTemplate().query(TaxonConceptDAOImpl.QUERY_BY_GUID,
            new Object[]{guid},
            new RowMapperResultSetExtractor(tcRowMapper, 1));
        if (results.size()>0) {
            return results.get(0);
        } else {
            return null;
        }
    }

	public class TaxonConceptRowMapper implements RowMapper {
	    /**
	     * The factory
	     */
	    public TaxonConcept mapRow(ResultSet rs, int rowNumber) throws SQLException {
            TaxonConcept tc = new TaxonConcept();
            tc.setId(rs.getLong("tc.id"));
            tc.setParentId(rs.getLong("tc.parent_concept_id"));
            tc.setRank(rs.getInt("tc.rank"));
            tc.setRankString(rs.getString("r.name"));
            tc.setCommonName(rs.getString("cn.name"));
            tc.setScientificName(rs.getString("tn.canonical"));
            tc.setTaxonNameId(rs.getLong("tc.taxon_name_id"));
            tc.setGuid(rs.getString("tc.guid"));
            tc.setLeft(rs.getInt("tc.lft"));
            tc.setRight(rs.getInt("tc.rgt"));
            return tc;
	    }
	    
	    protected Long getLongOrNull(ResultSet rs, String column) throws SQLException {
            Long value = rs.getLong(column);
            if (value.longValue()>0) {
                return value;
            } else {
            	return null;
            }
	    }
	}
}