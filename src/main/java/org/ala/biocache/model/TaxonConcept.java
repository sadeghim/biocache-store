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
    protected String kingdom;
    protected String kingdomGuid;
    protected String phylum;
    protected String phylumGuid;
    protected String theClass;
    protected String theClassGuid;
    protected String order;
    protected String orderGuid;
    protected String family;
    protected String familyGuid;
    protected String genus;
    protected String genusGuid;
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
	/**
	 * @return the kingdom
	 */
	public String getKingdom() {
		return kingdom;
	}
	/**
	 * @param kingdom the kingdom to set
	 */
	public void setKingdom(String kingdom) {
		this.kingdom = kingdom;
	}
	/**
	 * @return the kingdomGuid
	 */
	public String getKingdomGuid() {
		return kingdomGuid;
	}
	/**
	 * @param kingdomGuid the kingdomGuid to set
	 */
	public void setKingdomGuid(String kingdomGuid) {
		this.kingdomGuid = kingdomGuid;
	}
	/**
	 * @return the phylum
	 */
	public String getPhylum() {
		return phylum;
	}
	/**
	 * @param phylum the phylum to set
	 */
	public void setPhylum(String phylum) {
		this.phylum = phylum;
	}
	/**
	 * @return the phylumGuid
	 */
	public String getPhylumGuid() {
		return phylumGuid;
	}
	/**
	 * @param phylumGuid the phylumGuid to set
	 */
	public void setPhylumGuid(String phylumGuid) {
		this.phylumGuid = phylumGuid;
	}
	/**
	 * @return the theClass
	 */
	public String getTheClass() {
		return theClass;
	}
	/**
	 * @param theClass the theClass to set
	 */
	public void setTheClass(String theClass) {
		this.theClass = theClass;
	}
	/**
	 * @return the theClassGuid
	 */
	public String getTheClassGuid() {
		return theClassGuid;
	}
	/**
	 * @param theClassGuid the theClassGuid to set
	 */
	public void setTheClassGuid(String theClassGuid) {
		this.theClassGuid = theClassGuid;
	}
	/**
	 * @return the order
	 */
	public String getOrder() {
		return order;
	}
	/**
	 * @param order the order to set
	 */
	public void setOrder(String order) {
		this.order = order;
	}
	/**
	 * @return the orderGuid
	 */
	public String getOrderGuid() {
		return orderGuid;
	}
	/**
	 * @param orderGuid the orderGuid to set
	 */
	public void setOrderGuid(String orderGuid) {
		this.orderGuid = orderGuid;
	}
	/**
	 * @return the family
	 */
	public String getFamily() {
		return family;
	}
	/**
	 * @param family the family to set
	 */
	public void setFamily(String family) {
		this.family = family;
	}
	/**
	 * @return the familyGuid
	 */
	public String getFamilyGuid() {
		return familyGuid;
	}
	/**
	 * @param familyGuid the familyGuid to set
	 */
	public void setFamilyGuid(String familyGuid) {
		this.familyGuid = familyGuid;
	}
	/**
	 * @return the genus
	 */
	public String getGenus() {
		return genus;
	}
	/**
	 * @param genus the genus to set
	 */
	public void setGenus(String genus) {
		this.genus = genus;
	}
	/**
	 * @return the genusGuid
	 */
	public String getGenusGuid() {
		return genusGuid;
	}
	/**
	 * @param genusGuid the genusGuid to set
	 */
	public void setGenusGuid(String genusGuid) {
		this.genusGuid = genusGuid;
	}
}