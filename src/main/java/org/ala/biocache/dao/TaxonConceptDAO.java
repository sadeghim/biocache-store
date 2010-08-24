package org.ala.biocache.dao;

import org.ala.biocache.model.TaxonConcept;

public interface TaxonConceptDAO {

    public TaxonConcept getByGuid(final String guid);
}
