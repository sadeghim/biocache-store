/* *************************************************************************
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

package org.ala.biocache.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author "Nick dos Remedios <Nick.dosRemedios@csiro.au>"
 */
@Entity
@Table(name = "raw_occurrence_record")
@NamedQueries({
    @NamedQuery(name = "RawOccurrenceRecord.findAll", query = "SELECT r FROM RawOccurrenceRecord r"),
    @NamedQuery(name = "RawOccurrenceRecord.findById", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.id = :id"),
    @NamedQuery(name = "RawOccurrenceRecord.findByDataProviderId", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.dataProviderId = :dataProviderId"),
    @NamedQuery(name = "RawOccurrenceRecord.findByDataResourceId", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.dataResourceId = :dataResourceId"),
    @NamedQuery(name = "RawOccurrenceRecord.findByResourceAccessPointId", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.resourceAccessPointId = :resourceAccessPointId"),
    @NamedQuery(name = "RawOccurrenceRecord.findByInstitutionCode", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.institutionCode = :institutionCode"),
    @NamedQuery(name = "RawOccurrenceRecord.findByCollectionCode", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.collectionCode = :collectionCode"),
    @NamedQuery(name = "RawOccurrenceRecord.findByCatalogueNumber", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.catalogueNumber = :catalogueNumber"),
    @NamedQuery(name = "RawOccurrenceRecord.findByScientificName", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.scientificName = :scientificName"),
    @NamedQuery(name = "RawOccurrenceRecord.findByAuthor", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.author = :author"),
    @NamedQuery(name = "RawOccurrenceRecord.findByRank", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.rank = :rank"),
    @NamedQuery(name = "RawOccurrenceRecord.findByKingdom", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.kingdom = :kingdom"),
    @NamedQuery(name = "RawOccurrenceRecord.findByPhylum", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.phylum = :phylum"),
    @NamedQuery(name = "RawOccurrenceRecord.findByClass1", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.class1 = :class1"),
    @NamedQuery(name = "RawOccurrenceRecord.findByOrderRank", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.orderRank = :orderRank"),
    @NamedQuery(name = "RawOccurrenceRecord.findByFamily", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.family = :family"),
    @NamedQuery(name = "RawOccurrenceRecord.findByGenus", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.genus = :genus"),
    @NamedQuery(name = "RawOccurrenceRecord.findBySpecies", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.species = :species"),
    @NamedQuery(name = "RawOccurrenceRecord.findBySubspecies", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.subspecies = :subspecies"),
    @NamedQuery(name = "RawOccurrenceRecord.findByLatitude", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.latitude = :latitude"),
    @NamedQuery(name = "RawOccurrenceRecord.findByLongitude", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.longitude = :longitude"),
    @NamedQuery(name = "RawOccurrenceRecord.findByLatLongPrecision", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.latLongPrecision = :latLongPrecision"),
    @NamedQuery(name = "RawOccurrenceRecord.findByMaxAltitude", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.maxAltitude = :maxAltitude"),
    @NamedQuery(name = "RawOccurrenceRecord.findByMinAltitude", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.minAltitude = :minAltitude"),
    @NamedQuery(name = "RawOccurrenceRecord.findByAltitudePrecision", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.altitudePrecision = :altitudePrecision"),
    @NamedQuery(name = "RawOccurrenceRecord.findByMinDepth", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.minDepth = :minDepth"),
    @NamedQuery(name = "RawOccurrenceRecord.findByMaxDepth", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.maxDepth = :maxDepth"),
    @NamedQuery(name = "RawOccurrenceRecord.findByDepthPrecision", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.depthPrecision = :depthPrecision"),
    @NamedQuery(name = "RawOccurrenceRecord.findByContinentOcean", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.continentOcean = :continentOcean"),
    @NamedQuery(name = "RawOccurrenceRecord.findByCountry", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.country = :country"),
    @NamedQuery(name = "RawOccurrenceRecord.findByStateProvince", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.stateProvince = :stateProvince"),
    @NamedQuery(name = "RawOccurrenceRecord.findByCounty", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.county = :county"),
    @NamedQuery(name = "RawOccurrenceRecord.findByCollectorName", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.collectorName = :collectorName"),
    @NamedQuery(name = "RawOccurrenceRecord.findByYear", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.year = :year"),
    @NamedQuery(name = "RawOccurrenceRecord.findByMonth", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.month = :month"),
    @NamedQuery(name = "RawOccurrenceRecord.findByDay", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.day = :day"),
    @NamedQuery(name = "RawOccurrenceRecord.findByBasisOfRecord", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.basisOfRecord = :basisOfRecord"),
    @NamedQuery(name = "RawOccurrenceRecord.findByIdentifierName", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.identifierName = :identifierName"),
    @NamedQuery(name = "RawOccurrenceRecord.findByIdentificationDate", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.identificationDate = :identificationDate"),
    @NamedQuery(name = "RawOccurrenceRecord.findByUnitQualifier", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.unitQualifier = :unitQualifier"),
    @NamedQuery(name = "RawOccurrenceRecord.findByCreated", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.created = :created"),
    @NamedQuery(name = "RawOccurrenceRecord.findByModified", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.modified = :modified"),
    @NamedQuery(name = "RawOccurrenceRecord.findByDeleted", query = "SELECT r FROM RawOccurrenceRecord r WHERE r.deleted = :deleted")})
public class RawOccurrenceRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "data_provider_id")
    private short dataProviderId;
    @Basic(optional = false)
    @Column(name = "data_resource_id")
    private short dataResourceId;
    @Basic(optional = false)
    @Column(name = "resource_access_point_id")
    private short resourceAccessPointId;
    @Basic(optional = false)
    @Column(name = "institution_code")
    private String institutionCode;
    @Basic(optional = false)
    @Column(name = "collection_code")
    private String collectionCode;
    @Basic(optional = false)
    @Column(name = "catalogue_number")
    private String catalogueNumber;
    @Column(name = "scientific_name")
    private String scientificName;
    @Column(name = "author")
    private String author;
    @Column(name = "rank")
    private String rank;
    @Column(name = "kingdom")
    private String kingdom;
    @Column(name = "phylum")
    private String phylum;
    @Column(name = "class")
    private String class1;
    @Column(name = "order_rank")
    private String orderRank;
    @Column(name = "family")
    private String family;
    @Column(name = "genus")
    private String genus;
    @Column(name = "species")
    private String species;
    @Column(name = "subspecies")
    private String subspecies;
    @Column(name = "latitude")
    private String latitude;
    @Column(name = "longitude")
    private String longitude;
    @Column(name = "lat_long_precision")
    private String latLongPrecision;
    @Column(name = "max_altitude")
    private String maxAltitude;
    @Column(name = "min_altitude")
    private String minAltitude;
    @Column(name = "altitude_precision")
    private String altitudePrecision;
    @Column(name = "min_depth")
    private String minDepth;
    @Column(name = "max_depth")
    private String maxDepth;
    @Column(name = "depth_precision")
    private String depthPrecision;
    @Column(name = "continent_ocean")
    private String continentOcean;
    @Column(name = "country")
    private String country;
    @Column(name = "state_province")
    private String stateProvince;
    @Column(name = "county")
    private String county;
    @Column(name = "collector_name")
    private String collectorName;
    @Lob
    @Column(name = "locality")
    private String locality;
    @Column(name = "year")
    private String year;
    @Column(name = "month")
    private String month;
    @Column(name = "day")
    private String day;
    @Column(name = "basis_of_record")
    private String basisOfRecord;
    @Column(name = "identifier_name")
    private String identifierName;
    @Column(name = "identification_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date identificationDate;
    @Column(name = "unit_qualifier")
    private String unitQualifier;
    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
    @Column(name = "modified")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;
    @Column(name = "deleted")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deleted;

    public RawOccurrenceRecord() {
    }

    public RawOccurrenceRecord(Integer id) {
        this.id = id;
    }

    public RawOccurrenceRecord(Integer id, short dataProviderId, short dataResourceId, short resourceAccessPointId, String institutionCode, String collectionCode, String catalogueNumber) {
        this.id = id;
        this.dataProviderId = dataProviderId;
        this.dataResourceId = dataResourceId;
        this.resourceAccessPointId = resourceAccessPointId;
        this.institutionCode = institutionCode;
        this.collectionCode = collectionCode;
        this.catalogueNumber = catalogueNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public short getDataProviderId() {
        return dataProviderId;
    }

    public void setDataProviderId(short dataProviderId) {
        this.dataProviderId = dataProviderId;
    }

    public short getDataResourceId() {
        return dataResourceId;
    }

    public void setDataResourceId(short dataResourceId) {
        this.dataResourceId = dataResourceId;
    }

    public short getResourceAccessPointId() {
        return resourceAccessPointId;
    }

    public void setResourceAccessPointId(short resourceAccessPointId) {
        this.resourceAccessPointId = resourceAccessPointId;
    }

    public String getInstitutionCode() {
        return institutionCode;
    }

    public void setInstitutionCode(String institutionCode) {
        this.institutionCode = institutionCode;
    }

    public String getCollectionCode() {
        return collectionCode;
    }

    public void setCollectionCode(String collectionCode) {
        this.collectionCode = collectionCode;
    }

    public String getCatalogueNumber() {
        return catalogueNumber;
    }

    public void setCatalogueNumber(String catalogueNumber) {
        this.catalogueNumber = catalogueNumber;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getKingdom() {
        return kingdom;
    }

    public void setKingdom(String kingdom) {
        this.kingdom = kingdom;
    }

    public String getPhylum() {
        return phylum;
    }

    public void setPhylum(String phylum) {
        this.phylum = phylum;
    }

    public String getClass1() {
        return class1;
    }

    public void setClass1(String class1) {
        this.class1 = class1;
    }

    public String getOrderRank() {
        return orderRank;
    }

    public void setOrderRank(String orderRank) {
        this.orderRank = orderRank;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getGenus() {
        return genus;
    }

    public void setGenus(String genus) {
        this.genus = genus;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getSubspecies() {
        return subspecies;
    }

    public void setSubspecies(String subspecies) {
        this.subspecies = subspecies;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatLongPrecision() {
        return latLongPrecision;
    }

    public void setLatLongPrecision(String latLongPrecision) {
        this.latLongPrecision = latLongPrecision;
    }

    public String getMaxAltitude() {
        return maxAltitude;
    }

    public void setMaxAltitude(String maxAltitude) {
        this.maxAltitude = maxAltitude;
    }

    public String getMinAltitude() {
        return minAltitude;
    }

    public void setMinAltitude(String minAltitude) {
        this.minAltitude = minAltitude;
    }

    public String getAltitudePrecision() {
        return altitudePrecision;
    }

    public void setAltitudePrecision(String altitudePrecision) {
        this.altitudePrecision = altitudePrecision;
    }

    public String getMinDepth() {
        return minDepth;
    }

    public void setMinDepth(String minDepth) {
        this.minDepth = minDepth;
    }

    public String getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(String maxDepth) {
        this.maxDepth = maxDepth;
    }

    public String getDepthPrecision() {
        return depthPrecision;
    }

    public void setDepthPrecision(String depthPrecision) {
        this.depthPrecision = depthPrecision;
    }

    public String getContinentOcean() {
        return continentOcean;
    }

    public void setContinentOcean(String continentOcean) {
        this.continentOcean = continentOcean;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCollectorName() {
        return collectorName;
    }

    public void setCollectorName(String collectorName) {
        this.collectorName = collectorName;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getBasisOfRecord() {
        return basisOfRecord;
    }

    public void setBasisOfRecord(String basisOfRecord) {
        this.basisOfRecord = basisOfRecord;
    }

    public String getIdentifierName() {
        return identifierName;
    }

    public void setIdentifierName(String identifierName) {
        this.identifierName = identifierName;
    }

    public Date getIdentificationDate() {
        return identificationDate;
    }

    public void setIdentificationDate(Date identificationDate) {
        this.identificationDate = identificationDate;
    }

    public String getUnitQualifier() {
        return unitQualifier;
    }

    public void setUnitQualifier(String unitQualifier) {
        this.unitQualifier = unitQualifier;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RawOccurrenceRecord)) {
            return false;
        }
        RawOccurrenceRecord other = (RawOccurrenceRecord) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ala.biocache.model.RawOccurrenceRecord[id=" + id + "]";
    }

}
