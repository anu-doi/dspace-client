//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.06.19 at 03:37:53 PM AEST 
//


package au.edu.anu.dspace.client.swordv2.digitisation.iiirecord;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "subfieldindicator",
    "subfielddata"
})
@XmlRootElement(name = "MARCSUBFLD")
public class MARCSUBFLD {

    @XmlElement(name = "SUBFIELDINDICATOR", required = true)
    protected String subfieldindicator;
    @XmlElement(name = "SUBFIELDDATA", required = true)
    protected String subfielddata;

    /**
     * Gets the value of the subfieldindicator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSUBFIELDINDICATOR() {
        return subfieldindicator;
    }

    /**
     * Sets the value of the subfieldindicator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSUBFIELDINDICATOR(String value) {
        this.subfieldindicator = value;
    }

    /**
     * Gets the value of the subfielddata property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSUBFIELDDATA() {
        return subfielddata;
    }

    /**
     * Sets the value of the subfielddata property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSUBFIELDDATA(String value) {
        this.subfielddata = value;
    }

}
