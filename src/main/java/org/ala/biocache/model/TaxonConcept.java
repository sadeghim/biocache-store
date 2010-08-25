package org.ala.biocache.model;
import java.io.Serializable;


/**
 * A lite taxon concept used for fast classification use
 * @author trobertson
 */
public class TaxonConcept implements Serializable {
	
    /**
     * serialisable
     */
    private static final long serialVersionUID = 4949183250088693568L;

    protected String guid;
    protected Long id;
    protected Long parentId;
    protected Long taxonNameId;
    protected String scientificName;
    protected String commonName;
    protected Integer rank;
    protected String rankString;
    protected Integer left;
    protected Integer right;
    
    /**
     * @return Returns the id.
     */
    public Long getId() {
            return id;
    }
    /**
     * @param id The id to set.
     */
    public void setId(Long id) {
            this.id = id;
    }
    /**
     * @return Returns the parentId.
     */
    public Long getParentId() {
            return parentId;
    }
    /**
     * @param parentId The parentId to set.
     */
    public void setParentId(Long parentId) {
            this.parentId = parentId;
    }
    /**
     * @return Returns the rank.
     */
    public Integer getRank() {
            return rank;
    }
    /**
     * @param rank The rank to set.
     */
    public void setRank(Integer rank) {
            this.rank = rank;
    }
	/**
	 * @return the guid
	 */
	public String getGuid() {
		return guid;
	}
	/**
	 * @param guid the guid to set
	 */
	public void setGuid(String guid) {
		this.guid = guid;
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
	 * @return the right
	 */
	public Integer getRight() {
		return right;
	}
	/**
	 * @param right the right to set
	 */
	public void setRight(Integer right) {
		this.right = right;
	}
	/**
	 * @return the taxonNameId
	 */
	public Long getTaxonNameId() {
		return taxonNameId;
	}
	/**
	 * @param taxonNameId the taxonNameId to set
	 */
	public void setTaxonNameId(Long taxonNameId) {
		this.taxonNameId = taxonNameId;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TaxonConcept [guid=" + guid + ", id=" + id + ", parentId="
				+ parentId + ", taxonNameId=" + taxonNameId + ", rank=" + rank
				+ ", left=" + left + ", right=" + right + "]";
	}
	/**
	 * @return the scientificName
	 */
	public String getScientificName() {
		return scientificName;
	}
	/**
	 * @param scientificName the scientificName to set
	 */
	public void setScientificName(String scientificName) {
		this.scientificName = scientificName;
	}
	/**
	 * @return the commonName
	 */
	public String getCommonName() {
		return commonName;
	}
	/**
	 * @param commonName the commonName to set
	 */
	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}
	/**
	 * @return the rankString
	 */
	public String getRankString() {
		return rankString;
	}
	/**
	 * @param rankString the rankString to set
	 */
	public void setRankString(String rankString) {
		this.rankString = rankString;
	}
}