package org.ala.biocache.model;

import java.util.Date;

/**
 * Represents the raw data captured for a single 
 * occurrence record
 * 
 * @author tim
 */
public class RawOccurrenceRecord extends ModelObject {
/**
     * Generated
     */
    private static final long serialVersionUID = 45185631988755787L;
    protected long dataProviderId;
	protected long dataResourceId;
	protected long resourceAccessPointId;
	protected String institutionCode;
	protected String collectionCode;
	protected String catalogueNumber;
	protected String scientificName;
	protected String author;
	protected String rank;
	protected String vernacularName;
	protected String kingdom;
	protected String phylum;
	protected String klass;
	protected String order;
	protected String family;
	protected String genus;
	protected String species;
	protected String subspecies;
	protected String latitude;
	protected String longitude;
	protected String latLongPrecision;
	protected String minAltitude;
	protected String maxAltitude;
	protected String altitudePrecision;
	protected String minDepth;
	protected String maxDepth;
	protected String depthPrecision;
	protected String continentOrOcean;
	protected String country;
	protected String stateOrProvince;
	protected String county;
	protected String collectorName;
	protected String locality;
	protected String year;
	protected String month;
	protected String day;
	protected String basisOfRecord;
	protected String identifierName;
	protected Date dateIdentified;
	protected String unitQualifier;
	protected Date created;
	protected Date modifed;
	protected Date deleted;
	protected String taxonConceptGuid;
	protected String userId;
	protected String occurrenceRemarks;
	protected String locationRemarks;
	protected Integer individualCount;
	protected String citation;
	protected String geodeticDatum;
	protected Integer generalisedInMetres;

	/**
	 * Default
	 */
	public RawOccurrenceRecord() {}

    /**
     * Convenience
     */
    public RawOccurrenceRecord(long dataProviderId, long dataResourceId, long resourceAccessPointId, String institutionCode, String collectionCode, String catalogueNumber, String scientificName, String author, String rank, String vernacularName, String kingdom, String phylum, String klass, String order, String family, String genus, String species, String subspecies, String latitude, String longitude, String latLongPrecision, String minAltitude, String maxAltitude, String altitudePrecision, String minDepth, String maxDepth, String depthPrecision, String continentOrOcean, String country, String stateOrProvince, String county, String collectorName, String locality, String year, String month, String day, String basisOfRecord, String identifierName, Date dateIdentified, String unitQualifier, Date created, Date modifed, Date deleted, String taxonConceptGuid, String userId, String occurrenceRemarks, String locationRemarks, Integer individualCount, String citation, String geodeticDatum, Integer generalisedInMetres) {
            this.dataProviderId = dataProviderId;
            this.dataResourceId = dataResourceId;
            this.resourceAccessPointId = resourceAccessPointId;
            this.institutionCode = institutionCode;
            this.collectionCode = collectionCode;
            this.catalogueNumber = catalogueNumber;
            this.scientificName = scientificName;
            this.author = author;
            this.rank = rank;
            this.vernacularName = vernacularName;
            this.kingdom = kingdom;
            this.phylum = phylum;
            this.klass = klass;
            this.order = order;
            this.family = family;
            this.genus = genus;
            this.species = species;
            this.subspecies = subspecies;
            this.latitude = latitude;
            this.longitude = longitude;
            this.latLongPrecision = latLongPrecision;
            this.minAltitude = minAltitude;
            this.maxAltitude = maxAltitude;
            this.altitudePrecision = altitudePrecision;
            this.minDepth = minDepth;
            this.maxDepth = maxDepth;
            this.depthPrecision = depthPrecision;
            this.continentOrOcean = continentOrOcean;
            this.country = country;
            this.stateOrProvince = stateOrProvince;
            this.county = county;
            this.collectorName = collectorName;
            this.locality = locality;
            this.year = year;
            this.month = month;
            this.day = day;
            this.basisOfRecord = basisOfRecord;
            this.identifierName = identifierName;
            this.dateIdentified = dateIdentified;
            this.unitQualifier = unitQualifier;
            this.created = created;
            this.modifed = modifed;
            this.deleted = deleted;
            this.taxonConceptGuid = taxonConceptGuid;
            this.userId = userId;
            this.occurrenceRemarks = occurrenceRemarks;
            this.locationRemarks = locationRemarks;
            this.individualCount = individualCount;
        	this.citation = citation;
        	this.geodeticDatum = geodeticDatum;
        	this.generalisedInMetres = generalisedInMetres;
    }
    /**
     * Convenience
     */
    public RawOccurrenceRecord(long id, long dataProviderId, long dataResourceId, long resourceAccessPointId, String institutionCode, String collectionCode, String catalogueNumber, String scientificName, String author, String rank, String vernacularName, String kingdom, String phylum, String klass, String order, String family, String genus, String species, String subspecies, String latitude, String longitude, String latLongPrecision, String minAltitude, String maxAltitude, String altitudePrecision, String minDepth, String maxDepth, String depthPrecision, String continentOrOcean, String country, String stateOrProvince, String county, String collectorName, String locality, String year, String month, String day, String basisOfRecord, String identifierName, Date dateIdentified, String unitQualifier, Date created, Date modifed, Date deleted, String taxonConceptGuid, String userId, String occurrenceRemarks, String locationRemarks, Integer individualCount, String citation, String geodeticDatum, Integer generalisedInMetres) {
            this.id = id;
            this.dataProviderId = dataProviderId;
            this.dataResourceId = dataResourceId;
            this.resourceAccessPointId = resourceAccessPointId;
            this.institutionCode = institutionCode;
            this.collectionCode = collectionCode;
            this.catalogueNumber = catalogueNumber;
            this.scientificName = scientificName;
            this.author = author;
            this.rank = rank;
            this.vernacularName = vernacularName;
            this.kingdom = kingdom;
            this.phylum = phylum;
            this.klass = klass;
            this.order = order;
            this.family = family;
            this.genus = genus;
            this.species = species;
            this.subspecies = subspecies;
            this.latitude = latitude;
            this.longitude = longitude;
            this.latLongPrecision = latLongPrecision;
            this.minAltitude = minAltitude;
            this.maxAltitude = maxAltitude;
            this.altitudePrecision = altitudePrecision;
            this.minDepth = minDepth;
            this.maxDepth = maxDepth;
            this.depthPrecision = depthPrecision;
            this.continentOrOcean = continentOrOcean;
            this.country = country;
            this.stateOrProvince = stateOrProvince;
            this.county = county;
            this.collectorName = collectorName;
            this.locality = locality;
            this.year = year;
            this.month = month;
            this.day = day;
            this.basisOfRecord = basisOfRecord;
            this.identifierName = identifierName;
            this.dateIdentified = dateIdentified;
            this.created = created;
            this.modifed = modifed;
            this.deleted = deleted;
            this.taxonConceptGuid = taxonConceptGuid;
            this.userId = userId;
            this.occurrenceRemarks = occurrenceRemarks;
            this.locationRemarks = locationRemarks;
            this.individualCount = individualCount;
        	this.citation = citation;
        	this.geodeticDatum = geodeticDatum;
        	this.generalisedInMetres = generalisedInMetres;

    }
    /**
     * @return the maxAltitude
     */
    public String getMaxAltitude() {
            return maxAltitude;
    }

    /**
     * @param maxAltitude the maxAltitude to set
     */
    public void setMaxAltitude(String maxAltitude) {
            this.maxAltitude = maxAltitude;
    }

    /**
     * @return the minAltitude
     */
    public String getMinAltitude() {
            return minAltitude;
    }

    /**
     * @param minAltitude the minAltitude to set
     */
    public void setMinAltitude(String minAltitude) {
            this.minAltitude = minAltitude;
    }

    /**
     * @return Returns the altitudePrecision.
     */
    public String getAltitudePrecision() {
            return altitudePrecision;
    }
    /**
     * @param altitudePrecision The altitudePrecision to set.
     */
    public void setAltitudePrecision(String altitudePrecision) {
            this.altitudePrecision = altitudePrecision;
    }
    /**
     * @return Returns the author.
     */
    public String getAuthor() {
            return author;
    }
    /**
     * @param author The author to set.
     */
    public void setAuthor(String author) {
            this.author = author;
    }
    /**
     * @return Returns the basisOfRecord.
     */
    public String getBasisOfRecord() {
            return basisOfRecord;
    }
    /**
     * @return the day
     */
    public String getDay() {
            return day;
    }

    /**
     * @param day the day to set
     */
    public void setDay(String day) {
            this.day = day;
    }

    /**
     * @return the month
     */
    public String getMonth() {
            return month;
    }

    /**
     * @param month the month to set
     */
    public void setMonth(String month) {
            this.month = month;
    }

    /**
     * @return the year
     */
    public String getYear() {
            return year;
    }

    /**
     * @param year the year to set
     */
    public void setYear(String year) {
            this.year = year;
    }

    /**
     * @param basisOfRecord The basisOfRecord to set.
     */
    public void setBasisOfRecord(String basisOfRecord) {
            this.basisOfRecord = basisOfRecord;
    }
    /**
     * @return Returns the catalogueNumber.
     */
    public String getCatalogueNumber() {
            return catalogueNumber;
    }
    /**
     * @param catalogueNumber The catalogueNumber to set.
     */
    public void setCatalogueNumber(String catalogueNumber) {
            this.catalogueNumber = catalogueNumber;
    }
    /**
     * @return Returns the collectionCode.
     */
    public String getCollectionCode() {
            return collectionCode;
    }
    /**
     * @param collectionCode The collectionCode to set.
     */
    public void setCollectionCode(String collectionCode) {
            this.collectionCode = collectionCode;
    }
    /**
     * @return Returns the collectorName.
     */
    public String getCollectorName() {
            return collectorName;
    }
    /**
     * @param collectorName The collectorName to set.
     */
    public void setCollectorName(String collectorName) {
            this.collectorName = collectorName;
    }
    /**
     * @return Returns the country.
     */
    public String getCountry() {
            return country;
    }
    /**
     * @param country The country to set.
     */
    public void setCountry(String country) {
            this.country = country;
    }
    /**
     * @return the county
     */
    public String getCounty() {
            return county;
    }

    /**
     * @param county the county to set
     */
    public void setCounty(String county) {
            this.county = county;
    }

    /**
     * @return the stateOrProvince
     */
    public String getStateOrProvince() {
            return stateOrProvince;
    }

    /**
     * @param stateOrProvince the stateOrProvince to set
     */
    public void setStateOrProvince(String stateOrProvince) {
            this.stateOrProvince = stateOrProvince;
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
     * @return the maxDepth
     */
    public String getMaxDepth() {
            return maxDepth;
    }

    /**
     * @param maxDepth the maxDepth to set
     */
    public void setMaxDepth(String maxDepth) {
            this.maxDepth = maxDepth;
    }

    /**
     * @return the minDepth
     */
    public String getMinDepth() {
            return minDepth;
    }

    /**
     * @param minDepth the minDepth to set
     */
    public void setMinDepth(String minDepth) {
            this.minDepth = minDepth;
    }

    /**
     * @return Returns the depthPrecision.
     */
    public String getDepthPrecision() {
            return depthPrecision;
    }
    /**
     * @param depthPrecision The depthPrecision to set.
     */
    public void setDepthPrecision(String depthPrecision) {
            this.depthPrecision = depthPrecision;
    }
    /**
     * @return Returns the family.
     */
    public String getFamily() {
            return family;
    }
    /**
     * @param family The family to set.
     */
    public void setFamily(String family) {
            this.family = family;
    }
    /**
     * @return Returns the genus.
     */
    public String getGenus() {
            return genus;
    }
    /**
     * @param genus The genus to set.
     */
    public void setGenus(String genus) {
            this.genus = genus;
    }
    /**
     * @return Returns the institutionCode.
     */
    public String getInstitutionCode() {
            return institutionCode;
    }
    /**
     * @param institutionCode The institutionCode to set.
     */
    public void setInstitutionCode(String institutionCode) {
            this.institutionCode = institutionCode;
    }
    /**
     * @return Returns the kingdom.
     */
    public String getKingdom() {
            return kingdom;
    }
    /**
     * @param kingdom The kingdom to set.
     */
    public void setKingdom(String kingdom) {
            this.kingdom = kingdom;
    }
    /**
     * @return Returns the klass.
     */
    public String getKlass() {
            return klass;
    }
    /**
     * @param klass The klass to set.
     */
    public void setKlass(String klass) {
            this.klass = klass;
    }
    /**
     * @return Returns the latitude.
     */
    public String getLatitude() {
            return latitude;
    }
    /**
     * @param latitude The latitude to set.
     */
    public void setLatitude(String latitude) {
            this.latitude = latitude;
    }
    /**
     * @return Returns the latLongPrecision.
     */
    public String getLatLongPrecision() {
            return latLongPrecision;
    }
    /**
     * @param latLongPrecision The latLongPrecision to set.
     */
    public void setLatLongPrecision(String latLongPrecision) {
            this.latLongPrecision = latLongPrecision;
    }
    /**
     * @return Returns the locality.
     */
    public String getLocality() {
            return locality;
    }
    /**
     * @param locality The locality to set.
     */
    public void setLocality(String locality) {
            this.locality = locality;
    }
    /**
     * @return Returns the longitude.
     */
    public String getLongitude() {
            return longitude;
    }
    /**
     * @param longitude The longitude to set.
     */
    public void setLongitude(String longitude) {
            this.longitude = longitude;
    }
    /**
     * @return Returns the order.
     */
    public String getOrder() {
            return order;
    }
    /**
     * @param order The order to set.
     */
    public void setOrder(String order) {
            this.order = order;
    }
    /**
     * @return Returns the phylum.
     */
    public String getPhylum() {
            return phylum;
    }
    /**
     * @param phylum The phylum to set.
     */
    public void setPhylum(String phylum) {
            this.phylum = phylum;
    }
    /**
     * @return Returns the rank.
     */
    public String getRank() {
            return rank;
    }
    /**
     * @param rank The rank to set.
     */
    public void setRank(String rank) {
            this.rank = rank;
    }
    /**
     * @return Returns the resourceAccessPointId.
     */
    public long getResourceAccessPointId() {
            return resourceAccessPointId;
    }
    /**
     * @param resourceAccessPointId The resourceAccessPointId to set.
     */
    public void setResourceAccessPointId(long resourceAccessPointId) {
            this.resourceAccessPointId = resourceAccessPointId;
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
     * @return Returns the species.
     */
    public String getSpecies() {
            return species;
    }
    /**
     * @param species The species to set.
     */
    public void setSpecies(String species) {
            this.species = species;
    }
    /**
     * @return the subspecies
     */
    public String getSubspecies() {
            return subspecies;
    }

    /**
     * @param subspecies the subspecies to set
     */
    public void setSubspecies(String subspecies) {
            this.subspecies = subspecies;
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
     * @return Returns the modifed.
     */
    public Date getModifed() {
            return modifed;
    }
    /**
     * @param modifed The modifed to set.
     */
    public void setModifed(Date modifed) {
            this.modifed = modifed;
    }

    /**
     * @return the dataProviderId
     */
    public long getDataProviderId() {
            return dataProviderId;
    }

    /**
     * @param dataProviderId the dataProviderId to set
     */
    public void setDataProviderId(long dataProviderId) {
            this.dataProviderId = dataProviderId;
    }

    /**
     * @return the continentOrOcean
     */
    public String getContinentOrOcean() {
            return continentOrOcean;
    }

    /**
     * @param continentOrOcean the continentOrOcean to set
     */
    public void setContinentOrOcean(String continentOrOcean) {
            this.continentOrOcean = continentOrOcean;
    }

    /**
     * @return the dateIdentified
     */
    public Date getDateIdentified() {
            return dateIdentified;
    }

    /**
     * @param dateIdentified the dateIdentified to set
     */
    public void setDateIdentified(Date dateIdentified) {
            this.dateIdentified = dateIdentified;
    }

    /**
     * @return the identifierName
     */
    public String getIdentifierName() {
            return identifierName;
    }

    /**
     * @param identifierName the identifierName to set
     */
    public void setIdentifierName(String identifierName) {
            this.identifierName = identifierName;
    }

    /**
     * @return the unitQualifier
     */
    public String getUnitQualifier() {
            return unitQualifier;
    }

    /**
     * @param unitQualifier the unitQualifier to set
     */
    public void setUnitQualifier(String unitQualifier) {
            this.unitQualifier = unitQualifier;
    }

	/**
	 * @return the vernacularName
	 */
	public String getVernacularName() {
		return vernacularName;
	}

	/**
	 * @param vernacularName the vernacularName to set
	 */
	public void setVernacularName(String vernacularName) {
		this.vernacularName = vernacularName;
	}

	/**
	 * @return the taxonConceptGuid
	 */
	public String getTaxonConceptGuid() {
		return taxonConceptGuid;
	}

	/**
	 * @param taxonConceptGuid the taxonConceptGuid to set
	 */
	public void setTaxonConceptGuid(String taxonConceptGuid) {
		this.taxonConceptGuid = taxonConceptGuid;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the occurrenceRemarks
	 */
	public String getOccurrenceRemarks() {
		return occurrenceRemarks;
	}

	/**
	 * @param occurrenceRemarks the occurrenceRemarks to set
	 */
	public void setOccurrenceRemarks(String occurrenceRemarks) {
		this.occurrenceRemarks = occurrenceRemarks;
	}

	/**
	 * @return the locationRemarks
	 */
	public String getLocationRemarks() {
		return locationRemarks;
	}

	/**
	 * @param locationRemarks the locationRemarks to set
	 */
	public void setLocationRemarks(String locationRemarks) {
		this.locationRemarks = locationRemarks;
	}

	/**
	 * @return the individualCount
	 */
	public Integer getIndividualCount() {
		return individualCount;
	}

	/**
	 * @param individualCount the individualCount to set
	 */
	public void setIndividualCount(Integer individualCount) {
		this.individualCount = individualCount;
	}

	/**
	 * @return the citation
	 */
	public String getCitation() {
		return citation;
	}

	/**
	 * @param citation the citation to set
	 */
	public void setCitation(String citation) {
		this.citation = citation;
	}

	/**
	 * @return the geodeticDatum
	 */
	public String getGeodeticDatum() {
		return geodeticDatum;
	}

	/**
	 * @param geodeticDatum the geodeticDatum to set
	 */
	public void setGeodeticDatum(String geodeticDatum) {
		this.geodeticDatum = geodeticDatum;
	}

	/**
	 * @return the generalisedInMetres
	 */
	public Integer getGeneralisedInMetres() {
		return generalisedInMetres;
	}

	/**
	 * @param generalisedInMetres the generalisedInMetres to set
	 */
	public void setGeneralisedInMetres(Integer generalisedInMetres) {
		this.generalisedInMetres = generalisedInMetres;
	}
}