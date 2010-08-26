/**
 * 
 */
package org.ala.biocache.model;

import java.util.Date;


/**
 * A DataResource represents a Resource on the internet that contains BioDiversity Data.
 * (A DataResource can be termed DataSet)
 * 
 * It should be noted that a DataResource is served from a DataProvider, and accessed through
 * a ResourceAccessPoint.  A DataResource is "Access Agnostic" - it can indeed be accessed through
 * multiple access methods, but the transport is effectively irrelevant.
 * 
 * @author tim
 */
public class DataResource extends ModelObject {
	/**
	 * Generated
	 */
	private static final long serialVersionUID = -8335767271612375655L;
	protected long dataProviderId;
	protected String name;
	protected String displayName;
	protected String description;
	protected String rights;
	protected String citation;
	protected String citableAgent;
	protected String websiteUrl;
	protected String logoUrl;
	protected int basisOfRecord;
	protected int rootTaxonRank;
	protected String rootTaxonName;
	protected String scopeContinentCode;
	protected String scopeCountryCode;
	protected Integer providerRecordCount;
	protected int taxonomicPriority = 100;
	protected Date created;
	protected Date modified;
	protected Date deleted;
	protected boolean lockDisplayName;
	protected boolean lockCitableAgent;
	protected boolean lockBasisOfRecord;
	protected int confidence = 1;
	
	/**
	 * Convienience
	 */
	public DataResource(long id, long dataProviderId, String name, String displayName, String description, String rights, String citation, String citableAgent, String websiteUrl, String logoUrl, int basisOfRecord, int rootTaxonRank, String rootTaxonName, String scopeContinentCode, String scopeCountryCode, Integer providerRecordCount, int taxonomicPriority, Date created, Date modified, Date deleted, boolean lockDisplayName, boolean lockCitableAgent, boolean lockBasisOfRecord, int confidence) {
		this.id = id;
		this.dataProviderId = dataProviderId;
		this.name = name;
		this.displayName = displayName;
		this.rights = rights;
		this.citation = citation;
		this.citableAgent = citableAgent;
		this.websiteUrl = websiteUrl;
		this.logoUrl = logoUrl;
		this.description = description;
		this.basisOfRecord = basisOfRecord;
		this.rootTaxonRank = rootTaxonRank;
		this.rootTaxonName = rootTaxonName;
		this.scopeContinentCode = scopeContinentCode;
		this.scopeCountryCode = scopeCountryCode;
		this.providerRecordCount = providerRecordCount;
		this.taxonomicPriority = taxonomicPriority;
		this.created = created;
		this.modified = modified;
		this.deleted = deleted;
		this.lockDisplayName = lockDisplayName;
		this.lockCitableAgent = lockCitableAgent;
		this.lockBasisOfRecord = lockBasisOfRecord;
		this.confidence = confidence;
	}
	
	/**
	 * Default
	 */
	public DataResource() {
	}
	
	/**
	 * @return Returns the citation.
	 */
	public String getCitation() {
		return citation;
	}
	/**
	 * @param citation The citation to set.
	 */
	public void setCitation(String citation) {
		this.citation = citation;
	}
	/**
	 * @return Returns the created.
	 */
	public Date getCreated() {
		return created;
	}
	/**
	 * @param created The created to set.
	 */
	public void setCreated(Date created) {
		this.created = created;
	}
	/**
	 * @return Returns the dataProviderId.
	 */
	public long getDataProviderId() {
		return dataProviderId;
	}
	/**
	 * @param dataProviderId The dataProviderId to set.
	 */
	public void setDataProviderId(long dataProviderId) {
		this.dataProviderId = dataProviderId;
	}
	/**
	 * @return Returns the deleted.
	 */
	public Date getDeleted() {
		return deleted;
	}
	/**
	 * @param deleted The deleted to set.
	 */
	public void setDeleted(Date deleted) {
		this.deleted = deleted;
	}
	/**
	 * @return Returns the modified.
	 */
	public Date getModified() {
		return modified;
	}
	/**
	 * @param modified The modified to set.
	 */
	public void setModified(Date modified) {
		this.modified = modified;
	}
	/**
	 * @return Returns the rights.
	 */
	public String getRights() {
		return rights;
	}
	/**
	 * @param rights The rights to set.
	 */
	public void setRights(String rights) {
		this.rights = rights;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the basisOfRecord
	 */
	public int getBasisOfRecord() {
		return basisOfRecord;
	}

	/**
	 * @param basisOfRecord the basisOfRecord to set
	 */
	public void setBasisOfRecord(int basisOfRecord) {
		if (!lockBasisOfRecord) {
			this.basisOfRecord = basisOfRecord;
		}
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the logoUrl
	 */
	public String getLogoUrl() {
		return logoUrl;
	}

	/**
	 * @param logoUrl the logoUrl to set
	 */
	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		if (!lockDisplayName) {
			this.displayName = displayName;
		}
	}

	/**
	 * @return the citableAgent
	 */
	public String getCitableAgent() {
		return citableAgent;
	}

	/**
	 * @param citableAgent the citableAgent to set
	 */
	public void setCitableAgent(String citableAgent) {
		if (!lockCitableAgent) {
			this.citableAgent = citableAgent;
		}
	}

	/**
	 * @return the rootTaxonName
	 */
	public String getRootTaxonName() {
		return rootTaxonName;
	}

	/**
	 * @param rootTaxonName the rootTaxonName to set
	 */
	public void setRootTaxonName(String rootTaxonName) {
		this.rootTaxonName = rootTaxonName;
	}

	/**
	 * @return the rootTaxonRank
	 */
	public int getRootTaxonRank() {
		return rootTaxonRank;
	}

	/**
	 * @param rootTaxonRank the rootTaxonRank to set
	 */
	public void setRootTaxonRank(int rootTaxonRank) {
		this.rootTaxonRank = rootTaxonRank;
	}

	/**
	 * @return the scopeContinentCode
	 */
	public String getScopeContinentCode() {
		return scopeContinentCode;
	}

	/**
	 * @param scopeContinentCode the scopeContinentCode to set
	 */
	public void setScopeContinentCode(String scopeContinentCode) {
		this.scopeContinentCode = scopeContinentCode;
	}

	/**
	 * @return the scopeCountryCode
	 */
	public String getScopeCountryCode() {
		return scopeCountryCode;
	}

	/**
	 * @param scopeCountryCode the scopeCountryCode to set
	 */
	public void setScopeCountryCode(String scopeCountryCode) {
		this.scopeCountryCode = scopeCountryCode;
	}

	/**
	 * @return the providerRecordCount
	 */
	public Integer getProviderRecordCount() {
		return providerRecordCount;
	}

	/**
	 * @param providerRecordCount the providerRecordCount to set
	 */
	public void setProviderRecordCount(Integer providerRecordCount) {
		this.providerRecordCount = providerRecordCount;
	}

	/**
	 * @return the taxonomicPriority
	 */
	public int getTaxonomicPriority() {
		return taxonomicPriority;
	}

	/**
	 * @param taxonomicPriority the taxonomicPriority to set
	 */
	public void setTaxonomicPriority(int taxonomicPriority) {
		this.taxonomicPriority = taxonomicPriority;
	}

	/**
	 * @return the websiteUrl
	 */
	public String getWebsiteUrl() {
		return websiteUrl;
	}

	/**
	 * @param websiteUrl the websiteUrl to set
	 */
	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

	/**
	 * @return the lockBasisOfRecord
	 */
	public boolean getLockBasisOfRecord() {
		return lockBasisOfRecord;
	}

	/**
	 * @param lockBasisOfRecord the lockBasisOfRecord to set
	 */
	public void setLockBasisOfRecord(boolean lockBasisOfRecord) {
		this.lockBasisOfRecord = lockBasisOfRecord;
	}

	/**
	 * @return the lockCitableAgent
	 */
	public boolean getLockCitableAgent() {
		return lockCitableAgent;
	}

	/**
	 * @param lockCitableAgent the lockCitableAgent to set
	 */
	public void setLockCitableAgent(boolean lockCitableAgent) {
		this.lockCitableAgent = lockCitableAgent;
	}

	/**
	 * @return the lockDisplayName
	 */
	public boolean getLockDisplayName() {
		return lockDisplayName;
	}

	/**
	 * @param lockDisplayName the lockDisplayName to set
	 */
	public void setLockDisplayName(boolean lockDisplayName) {
		this.lockDisplayName = lockDisplayName;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DataResource [dataProviderId=" + dataProviderId + ", name=" + name + ", id=" + id +", description=" + description
				+ name + ", displayName=" + displayName + ", description="
				+ description + ", rights=" + rights + ", citation=" + citation
				+ ", citableAgent=" + citableAgent + ", websiteUrl="
				+ websiteUrl + ", logoUrl=" + logoUrl + ", basisOfRecord="
				+ basisOfRecord + ", rootTaxonRank=" + rootTaxonRank
				+ ", rootTaxonName=" + rootTaxonName + ", scopeContinentCode="
				+ scopeContinentCode + ", scopeCountryCode=" + scopeCountryCode
				+ ", providerRecordCount=" + providerRecordCount
				+ ", taxonomicPriority=" + taxonomicPriority + ", created="
				+ created + ", modified=" + modified + ", deleted=" + deleted
				+ ", lockDisplayName=" + lockDisplayName
				+ ", lockCitableAgent=" + lockCitableAgent
				+ ", lockBasisOfRecord=" + lockBasisOfRecord + "]";
	}

	/**
	 * @return the confidence
	 */
	public int getConfidence() {
		return confidence;
	}

	/**
	 * @param confidence the confidence to set
	 */
	public void setConfidence(int confidence) {
		this.confidence = confidence;
	}
}