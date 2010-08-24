/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ala.biocache.dto;

/**
 * A generic DTO for results
 *
 * @author 
 */
public class FieldResultDTO {
    String label;
    String prefix;
    long count;

    public FieldResultDTO(String fieldValue, long count) {
        setFieldValue(fieldValue) ;
        this.count = count;
    }
    public void setFieldValue(String fieldValue)
    {
        //Currently not using the prefix, currently
        //the field values are expected to shortened during
        //the fedora indexing process. Full-stops are replaced
        //here by uderscores as the jsp processor interprets full-stops
        //as special characters and doesn't forward to the correct action
        this.label = fieldValue.replace('.', '_');
    }
    public String getFieldValue() {
    	if(prefix!=null){
    		return prefix+label;
    	}else {
    		return label;
    	}
    }
    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FieldResultDTO [count=" + count + ", label=" + label
				+ ", prefix=" + prefix + "]";
	}
}
