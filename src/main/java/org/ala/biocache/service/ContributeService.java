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
package org.ala.biocache.service;

import org.ala.biocache.dto.CitizenScience;
import org.ala.biocache.dto.Sighting;
/**
 * An interface that for recording and managing sightings.
 * 
 * @author Dave Martin
 */
public interface ContributeService {

	public boolean recordSighting(Sighting sighting);
	public long updateSighting(Sighting sighting);
	public boolean deleteSighting(String sightingId);
	
	public long recordCitizenScience(CitizenScience sighting);
	public long updateCitizenScience(CitizenScience sighting);
	public boolean deleteCitizenScience(String sightingId);
	
}
