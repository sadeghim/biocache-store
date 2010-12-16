/**************************************************************************
 *  Copyright (C) 2010 Atlas of Living Australia
 *  All Rights Reserved.
 *
 *  The contents of this file are subject to the Mozilla Public
 *  License Version 1.1 (the "License"); you may not use this file
 *  except in compliance with the License. You may obtain a copy of
 *  the License at http://www.mozilla.org/MPL/
 *
 *  Software distributed under the License is distributed on an "AS
 *  IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  rights and limitations under the License.
 ***************************************************************************/

package org.ala.dao.impl;

import org.ala.dao.JdbcDao;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author mok011
 *
 */

@Repository
public class JdbcDaoImpl extends JdbcDaoSupport implements JdbcDao{
	private static final Logger logger = Logger.getLogger(JdbcDaoImpl.class);
	
	@Transactional (propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
	public void executeJsonSQL(String sql){
		try{
			this.getJdbcTemplate().execute(sql);
		}
		catch(DataAccessException ex){
			logger.error(ex);
			throw ex;
		}
	}	
}
