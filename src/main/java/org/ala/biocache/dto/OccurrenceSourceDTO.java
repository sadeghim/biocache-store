
package org.ala.biocache.dto;

/**
 * Stores the information about the source of an occurrence
 *
 * @author Natasha
 */
public class OccurrenceSourceDTO {
    private String name;
    private String uid;

    public OccurrenceSourceDTO() {

    }

    public OccurrenceSourceDTO(String name, String uid) {
        this.name = name;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


}
