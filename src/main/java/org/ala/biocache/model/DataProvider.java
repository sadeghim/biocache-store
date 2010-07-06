/**
 * 
 */
package org.ala.biocache.model;

import java.util.Date;


/**
 * A DataProvider represents an Entity (e.g. an organisation) that serves collections (DataResources) 
 * of BioDiversity Data.
 * 
 * @author tim
 */
public class DataProvider extends ModelObject {
	
	/**
	 * Generated id 
	 */
	private static final long serialVersionUID = -4426558606358230541L;
	
	protected String name;
	protected String description;
	protected String address;
	protected String websiteUrl;
	protected String logoUrl;
	protected String email;
	protected String telephone;
	protected String uuid;
	protected String isoCountryCode;
	protected String gbifApprover;
	protected Date created;
	protected Date modified;
	protected Date deleted;
	protected boolean lockDescription;
	protected boolean lockIsoCountryCode;
	
	/**
	 * Convienience
	 */
	public DataProvider(long id, String name, String description, String address, String websiteUrl, String logoUrl, String email, String telephone, String uuid, String isoCountryCode, String gbifApprover, Date created, Date modified, Date deleted, boolean lockDescription, boolean lockIsoCountryCode) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.address = address;
		this.websiteUrl = websiteUrl;
		this.logoUrl = logoUrl;
		this.email = email;
		this.telephone = telephone;
		this.uuid = uuid;
		this.isoCountryCode = isoCountryCode;
		this.gbifApprover = gbifApprover;
		this.created = created;
		this.modified = modified;
		this.deleted = deleted;
		this.lockDescription = lockDescription;
		this.lockIsoCountryCode = lockIsoCountryCode;
	}
	
	/**
	 * Default
	 */
	public DataProvider() {		
	}
	
	/**
	 * @return Returns the address.
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @param address The address to set.
	 */
	public void setAddress(String address) {
		this.address = address;
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
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		if (!lockDescription) {
			this.description = description;
		}
	}
	/**
	 * @return Returns the email.
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email The email to set.
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return Returns the logoUrl.
	 */
	public String getLogoUrl() {
		return logoUrl;
	}
	/**
	 * @param logoUrl The logoUrl to set.
	 */
	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
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
	 * @return Returns the telephone.
	 */
	public String getTelephone() {
		return telephone;
	}
	/**
	 * @param telephone The telephone to set.
	 */
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	/**
	 * @return Returns the uuid.
	 */
	public String getUuid() {
		return uuid;
	}
	/**
	 * @param uuid The uuid to set.
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	/**
	 * @return the isoCountryCode
	 */
	public String getIsoCountryCode() {
		return isoCountryCode;
	}

	/**
	 * @param isoCountryCode the isoCountryCode to set
	 */
	public void setIsoCountryCode(String isoCountryCode) {
		if (!lockIsoCountryCode) {
			this.isoCountryCode = isoCountryCode;
		}
	}

	/**
	 * @return Returns the websiteUrl.
	 */
	public String getWebsiteUrl() {
		return websiteUrl;
	}
	/**
	 * @param websiteUrl The websiteUrl to set.
	 */
	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}

	/**
	 * @return the gbifApprover
	 */
	public String getGbifApprover() {
		return gbifApprover;
	}

	/**
	 * @param gbifApprover the gbifApprover to set
	 */
	public void setGbifApprover(String gbifApprover) {
		this.gbifApprover = gbifApprover;
	}

	/**
	 * @return the lockDescription
	 */
	public boolean getLockDescription() {
		return lockDescription;
	}

	/**
	 * @param lockDescription the lockDescription to set
	 */
	public void setLockDescription(boolean lockDescription) {
		this.lockDescription = lockDescription;
	}

	/**
	 * @return the lockIsoCountryCode
	 */
	public boolean getLockIsoCountryCode() {
		return lockIsoCountryCode;
	}

	/**
	 * @param lockIsoCountryCode the lockIsoCountryCode to set
	 */
	public void setLockIsoCountryCode(boolean lockIsoCountryCode) {
		this.lockIsoCountryCode = lockIsoCountryCode;
	}
}