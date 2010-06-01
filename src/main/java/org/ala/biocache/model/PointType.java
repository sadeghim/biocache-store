package org.ala.biocache.model;

/**
 * Enum to store the groupings or lat/long accuracy level (for zoom levels)
 *
 * Labels correspond to SOLR fields in $SOLR_HOME/conf/schema.xml
 *
 * @author "Nick dos Remedios <Nick.dosRemedios@csiro.au>"
 */
public enum PointType {
    POINT_1("point-1"),
    POINT_01("point-0.1"),
    POINT_001("point-0.01"),
    POINT_0001("point-0.001"),
    POINT_00001("point-0.0001");
    private String label;

    PointType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
