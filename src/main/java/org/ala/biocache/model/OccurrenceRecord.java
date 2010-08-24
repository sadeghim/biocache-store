package org.ala.biocache.model;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Represents the parsed occurrence record
 * 
 * @author tim
 */
public class OccurrenceRecord extends ModelObject {
	/**
	 * Generated
	 */
	private static final long serialVersionUID = 7946352755274147512L;
	protected Log logger = LogFactory.getLog(OccurrenceRecord.class);
	protected Long dataProviderId = -1l;
	protected Long dataResourceId = -1l;
	protected Long institutionCodeId;
	protected Long collectionCodeId;
	protected Long catalogueNumberId;
	protected Long taxonConceptId;
	protected Long taxonNameId;
	protected Long nubConceptId;
	protected String isoCountryCode;
	protected Float latitude;
	protected Float longitude;
	protected Integer cellId;
	protected Integer centiCellId;
	protected Integer mod360CellId;
	protected Integer altitudeInMetres;
	protected Integer depthInCentimetres;
	protected Integer year;
	protected Integer month;
	protected Date occurrenceDate;
	protected int basisOfRecord;
	protected int taxonomicIssue;
	protected int geospatialIssue;
	protected int otherIssue;
	protected Date modified;
	protected Date deleted;
	protected Integer left;

	/**
	 * @return the dataProviderId
	 */
	public long getDataProviderId() {
		return dataProviderId;
	}

	/**
	 * @param dataProviderId
	 *            the dataProviderId to set
	 */
	public void setDataProviderId(long dataProviderId) {
		this.dataProviderId = dataProviderId;
	}

	/**
	 * @return the dataResourceId
	 */
	public long getDataResourceId() {
		return dataResourceId;
	}

	/**
	 * @param dataResourceId
	 *            the dataResourceId to set
	 */
	public void setDataResourceId(long dataResourceId) {
		this.dataResourceId = dataResourceId;
	}

	/**
	 * @return the deleted
	 */
	public Date getDeleted() {
		return deleted;
	}

	/**
	 * @param deleted
	 *            the deleted to set
	 */
	public void setDeleted(Date deleted) {
		this.deleted = deleted;
	}

	/**
	 * @return the isoCountryCode
	 */
	public String getIsoCountryCode() {
		return isoCountryCode;
	}

	/**
	 * @param isoCountryCode
	 *            the isoCountryCode to set
	 */
	public void setIsoCountryCode(String isoCountryCode) {
		this.isoCountryCode = isoCountryCode;
	}

	/**
	 * @return the latitude
	 */
	public Float getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude
	 *            the latitude to set
	 */
	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public Float getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude
	 *            the longitude to set
	 */
	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the nubConceptId
	 */
	public Long getNubConceptId() {
		return nubConceptId;
	}

	/**
	 * @param nubConceptId
	 *            the nubConceptId to set
	 */
	public void setNubConceptId(Long nubConceptId) {
		this.nubConceptId = nubConceptId;
	}

	/**
	 * @return the taxonConceptId
	 */
	public Long getTaxonConceptId() {
		return taxonConceptId;
	}

	/**
	 * @param taxonConceptId
	 *            the taxonConceptId to set
	 */
	public void setTaxonConceptId(Long taxonConceptId) {
		this.taxonConceptId = taxonConceptId;
	}

	/**
	 * @return the taxonNameId
	 */
	public Long getTaxonNameId() {
		return taxonNameId;
	}

	/**
	 * @param taxonNameId
	 *            the taxonNameId to set
	 */
	public void setTaxonNameId(Long taxonNameId) {
		this.taxonNameId = taxonNameId;
	}

	/**
	 * @param basisOfRecord
	 *            the basisOfRecord to set
	 */
	public void setBasisOfRecord(int basisOfRecord) {
		this.basisOfRecord = basisOfRecord;
	}

	/**
	 * @return the basisOfRecord
	 */
	public int getBasisOfRecord() {
		return basisOfRecord;
	}

	/**
	 * @return the cellId
	 */
	public Integer getCellId() {
		return cellId;
	}

	/**
	 * @param cellId
	 *            the cellId to set
	 */
	public void setCellId(Integer cellId) {
		this.cellId = cellId;
	}

	/**
	 * @return the centiCellId
	 */
	public Integer getCentiCellId() {
		return centiCellId;
	}

	/**
	 * @param centiCellId
	 *            the centiCellId to set
	 */
	public void setCentiCellId(Integer centiCellId) {
		this.centiCellId = centiCellId;
	}

	/**
	 * @return the month
	 */
	public Integer getMonth() {
		return month;
	}

	/**
	 * @param month
	 *            the month to set
	 */
	public void setMonth(Integer month) {
		this.month = month;
	}

	/**
	 * @return the year
	 */
	public Integer getYear() {
		return year;
	}

	/**
	 * @param year
	 *            the year to set
	 */
	public void setYear(Integer year) {
		this.year = year;
	}

	/**
	 * @return the geospatialIssue
	 */
	public int getGeospatialIssue() {
		return geospatialIssue;
	}

	/**
	 * @param geospatialIssue
	 *            the geospatialIssue to set
	 */
	public void setGeospatialIssue(int geospatialIssue) {
		this.geospatialIssue = geospatialIssue;
	}

	/**
	 * @param bits
	 *            bits to include in geospatialIssue
	 * @see org.gbif.portal.util.db.OccurrenceRecordUtils
	 */
	public void setGeospatialIssueBits(int bits) {
		this.geospatialIssue |= bits;
	}

	/**
	 * @param bits
	 *            bits to clear in geospatialIssue
	 * @see org.gbif.portal.util.db.OccurrenceRecordUtils
	 */
	public void clearGeospatialIssueBits(int bits) {
		this.geospatialIssue &= (OccurrenceRecordUtils.GEOSPATIAL_MASK ^ bits);
	}

	/**
	 * @return the occurrenceDate
	 */
	public Date getOccurrenceDate() {
		return occurrenceDate;
	}

	/**
	 * @param occurrenceDate
	 *            the occurrenceDate to set
	 */
	public void setOccurrenceDate(Date occurrenceDate) {
		this.occurrenceDate = occurrenceDate;
	}

	/**
	 * @return the otherIssue
	 */
	public int getOtherIssue() {
		return otherIssue;
	}

	/**
	 * @param otherIssue
	 *            the otherIssue to set
	 */
	public void setOtherIssue(int otherIssue) {
		this.otherIssue = otherIssue;
	}

	/**
	 * @param bits
	 *            bits to include in otherIssue
	 * @see org.gbif.portal.util.db.OccurrenceRecordUtils
	 */
	public void setOtherIssueBits(int bits) {
		this.otherIssue |= bits;
	}

	/**
	 * @param bits
	 *            bits to clear in otherIssue
	 * @see org.gbif.portal.util.db.OccurrenceRecordUtils
	 */
	public void clearOtherIssueBits(int bits) {
		this.otherIssue &= (OccurrenceRecordUtils.OTHER_MASK ^ bits);
	}

	/**
	 * @return the taxonomicIssue
	 */
	public int getTaxonomicIssue() {
		return taxonomicIssue;
	}

	/**
	 * @param taxonomicIssue
	 *            the taxonomicIssue to set
	 */
	public void setTaxonomicIssue(int taxonomicIssue) {
		this.taxonomicIssue = taxonomicIssue;
	}

	/**
	 * @param bits
	 *            bits to include in taxonomicIssue
	 * @see org.gbif.portal.util.db.OccurrenceRecordUtils
	 */
	public void setTaxonomicIssueBits(int bits) {
		this.taxonomicIssue |= bits;
	}

	/**
	 * @param bits
	 *            bits to clear in taxonomicIssue
	 * @see org.gbif.portal.util.db.OccurrenceRecordUtils
	 */
	public void clearTaxonomicIssueBits(int bits) {
		this.taxonomicIssue &= (OccurrenceRecordUtils.TAXONOMIC_MASK ^ bits);
	}

	/**
	 * @return Returns the catalogueNumberId.
	 */
	public Long getCatalogueNumberId() {
		return catalogueNumberId;
	}

	/**
	 * @param catalogueNumberId
	 *            The catalogueNumberId to set.
	 */
	public void setCatalogueNumberId(Long catalogueNumberId) {
		this.catalogueNumberId = catalogueNumberId;
	}

	/**
	 * @return Returns the collectionCodeId.
	 */
	public Long getCollectionCodeId() {
		return collectionCodeId;
	}

	/**
	 * @param collectionCodeId
	 *            The collectionCodeId to set.
	 */
	public void setCollectionCodeId(Long collectionCodeId) {
		this.collectionCodeId = collectionCodeId;
	}

	/**
	 * @return Returns the institutionCodeId.
	 */
	public Long getInstitutionCodeId() {
		return institutionCodeId;
	}

	/**
	 * @param institutionCodeId
	 *            The institutionCodeId to set.
	 */
	public void setInstitutionCodeId(Long institutionCodeId) {
		this.institutionCodeId = institutionCodeId;
	}

	/**
	 * @return Returns the mod360CellId.
	 */
	public Integer getMod360CellId() {
		return mod360CellId;
	}

	/**
	 * @param mod360CellId
	 *            The mod360CellId to set.
	 */
	public void setMod360CellId(Integer mod360CellId) {
		this.mod360CellId = mod360CellId;
	}

	/**
	 * @return the altitudeInMetres
	 */
	public Integer getAltitudeInMetres() {
		return altitudeInMetres;
	}

	/**
	 * @param altitudeInMetres
	 *            the altitudeInMetres to set
	 */
	public void setAltitudeInMetres(Integer altitudeInMetres) {
		this.altitudeInMetres = altitudeInMetres;
	}

	/**
	 * @return the depthInCentimetres
	 */
	public Integer getDepthInCentimetres() {
		return depthInCentimetres;
	}

	/**
	 * @param depthInCentimetres
	 *            the depthInCentimetres to set
	 */
	public void setDepthInCentimetres(Integer depthInCentimetres) {
		this.depthInCentimetres = depthInCentimetres;
	}

	/**
	 * @return the modified
	 */
	public Date getModified() {
		return modified;
	}

	/**
	 * @param modified
	 *            the modified to set
	 */
	public void setModified(Date modified) {
		this.modified = modified;
	}

	/**
	 * @return the left
	 */
	public Integer getLeft() {
		return left;
	}

	/**
	 * @param left the left to set
	 */
	public void setLeft(Integer left) {
		this.left = left;
	}

	/**
	 * @param dataProviderId the dataProviderId to set
	 */
	public void setDataProviderId(Long dataProviderId) {
		this.dataProviderId = dataProviderId;
	}

	/**
	 * @param dataResourceId the dataResourceId to set
	 */
	public void setDataResourceId(Long dataResourceId) {
		this.dataResourceId = dataResourceId;
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final OccurrenceRecord other = (OccurrenceRecord) obj;
		if (id != other.id)
			return false;
		if (altitudeInMetres == null) {
			if (other.altitudeInMetres != null)
				return false;
		} else if (!altitudeInMetres.equals(other.altitudeInMetres))
			return false;
		if (basisOfRecord != other.basisOfRecord)
			return false;
		if (catalogueNumberId != other.catalogueNumberId)
			return false;
		if (cellId == null) {
			if (other.cellId != null)
				return false;
		} else if (!cellId.equals(other.cellId))
			return false;
		if (centiCellId == null) {
			if (other.centiCellId != null)
				return false;
		} else if (!centiCellId.equals(other.centiCellId))
			return false;
		if (collectionCodeId != other.collectionCodeId)
			return false;
		if (dataProviderId != other.dataProviderId)
			return false;
		if (dataResourceId != other.dataResourceId)
			return false;
		if (depthInCentimetres == null) {
			if (other.depthInCentimetres != null)
				return false;
		} else if (!depthInCentimetres.equals(other.depthInCentimetres))
			return false;
		if (geospatialIssue != other.geospatialIssue)
			return false;
		if (institutionCodeId != other.institutionCodeId)
			return false;
		if (isoCountryCode == null) {
			if (other.isoCountryCode != null)
				return false;
		} else if (!isoCountryCode.equals(other.isoCountryCode))
			return false;
		if (latitude == null) {
			if (other.latitude != null)
				return false;
		} else if (!latitude.equals(other.latitude))
			return false;
		if (longitude == null) {
			if (other.longitude != null)
				return false;
		} else if (!longitude.equals(other.longitude))
			return false;
		if (mod360CellId == null) {
			if (other.mod360CellId != null)
				return false;
		} else if (!mod360CellId.equals(other.mod360CellId))
			return false;
		if (month == null) {
			if (other.month != null)
				return false;
		} else if (!month.equals(other.month))
			return false;
		if (nubConceptId == null) {
			if (other.nubConceptId != null)
				return false;
		} else if (!nubConceptId.equals(other.nubConceptId))
			return false;
		if (occurrenceDate == null) {
			if (other.occurrenceDate != null)
				return false;
		} else if (!occurrenceDate.equals(other.occurrenceDate))
			return false;
		if (otherIssue != other.otherIssue)
			return false;
		if (taxonConceptId == null) {
			if (other.taxonConceptId != null)
				return false;
		} else if (!taxonConceptId.equals(other.taxonConceptId))
			return false;
		if (taxonNameId == null) {
			if (other.taxonNameId != null)
				return false;
		} else if (!taxonNameId.equals(other.taxonNameId))
			return false;
		if (taxonomicIssue != other.taxonomicIssue)
			return false;
		if (year == null) {
			if (other.year != null)
				return false;
		} else if (!year.equals(other.year))
			return false;
		return true;
	}
}