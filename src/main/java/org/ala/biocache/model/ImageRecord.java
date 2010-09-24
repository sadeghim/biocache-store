package org.ala.biocache.model;

public class ImageRecord extends ModelObject {
	/**
	 * Generated 
	 */
	private static final long serialVersionUID = -636487016795482473L;

    protected long dataResourceId;
    protected Long occurrenceId;
    protected long taxonConceptId;
    protected String rawImageType;
    protected int imageType;
    protected String url;
    protected String description;
    protected String rights;
    protected String htmlForDisplay;
        
    /**
     * Default
     */
    public ImageRecord() {    	
    }
    
	/**
	 * Convenience
	 */
	public ImageRecord(long dataResourceId, Long occurrenceId, long taxonConceptId, String rawImageType, int imageType, String url, String description, String rights, String htmlForDisplay) {
		this.dataResourceId = dataResourceId;
		this.occurrenceId = occurrenceId;
		this.taxonConceptId = taxonConceptId;
		this.rawImageType = rawImageType;
		this.imageType = imageType;
		this.url = url;
		this.description = description;
		this.rights = rights;
		this.htmlForDisplay = htmlForDisplay;
	}

	/**
	 * Convenience
	 */
	public ImageRecord(long id, long dataResourceId, Long occurrenceId, long taxonConceptId, String rawImageType, int imageType, String url, String description, String rights, String htmlForDisplay) {
		this.id = id;
		this.dataResourceId = dataResourceId;
		this.occurrenceId = occurrenceId;
		this.taxonConceptId = taxonConceptId;
		this.rawImageType = rawImageType;
		this.imageType = imageType;
		this.url = url;
		this.description = description;
		this.rights = rights;
		this.htmlForDisplay = htmlForDisplay;
	}

	/**
	 * @return Returns the dataResourceId.
	 */
	public long getDataResourceId() {
		return dataResourceId;
	}

	/**
	 * @param dataResourceId The dataResourceId to set.
	 */
	public void setDataResourceId(long dataResourceId) {
		this.dataResourceId = dataResourceId;
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
		this.description = description;
	}

	/**
	 * @return Returns the htmlForDisplay.
	 */
	public String getHtmlForDisplay() {
		return htmlForDisplay;
	}

	/**
	 * @param htmlForDisplay The htmlForDisplay to set.
	 */
	public void setHtmlForDisplay(String htmlForDisplay) {
		this.htmlForDisplay = htmlForDisplay;
	}

	/**
	 * @return Returns the imageType.
	 */
	public int getImageType() {
		return imageType;
	}

	/**
	 * @param imageType The imageType to set.
	 */
	public void setImageType(int imageType) {
		this.imageType = imageType;
	}

	/**
	 * @return Returns the occurrenceId.
	 */
	public Long getOccurrenceId() {
		return occurrenceId;
	}

	/**
	 * @param occurrenceId The occurrenceId to set.
	 */
	public void setOccurrenceId(Long occurrenceId) {
		this.occurrenceId = occurrenceId;
	}

	/**
	 * @return Returns the rawImageType.
	 */
	public String getRawImageType() {
		return rawImageType;
	}

	/**
	 * @param rawImageType The rawImageType to set.
	 */
	public void setRawImageType(String rawImageType) {
		this.rawImageType = rawImageType;
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
	 * @return Returns the taxonConceptId.
	 */
	public long getTaxonConceptId() {
		return taxonConceptId;
	}

	/**
	 * @param taxonConceptId The taxonConceptId to set.
	 */
	public void setTaxonConceptId(long taxonConceptId) {
		this.taxonConceptId = taxonConceptId;
	}

	/**
	 * @return Returns the url.
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url The url to set.
	 */
	public void setUrl(String url) {
		this.url = url;
	}
}