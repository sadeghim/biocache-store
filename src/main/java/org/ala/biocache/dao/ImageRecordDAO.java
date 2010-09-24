package org.ala.biocache.dao;

import java.util.List;

import org.ala.biocache.model.ImageRecord;

public interface ImageRecordDAO {
	
	/**
	 * Gets the records associated with the specified OccurrenceRecord
	 * @param occurrenceRecordId identifier of OccurrenceRecord
	 * @return The records or an empty list
	 */
	public List<ImageRecord> findByOccurrenceId(long occurrenceId);
}
