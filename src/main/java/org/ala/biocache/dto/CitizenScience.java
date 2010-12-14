package org.ala.biocache.dto;

import org.ala.biocache.dto.Sighting;

public class CitizenScience extends Sighting{
	String[] associatedMedia;

	public String[] getAssociatedMedia() {
		return associatedMedia;
	}

	public void setAssociatedMedia(String[] associatedMedia) {
		this.associatedMedia = associatedMedia;
	}		
}
