//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.06.19 at 03:37:45 PM AEST 
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
    "varfld"
})
@XmlRootElement(name = "VARFLDALTERNATE")
public class VARFLDALTERNATE {

    @XmlElement(name = "VARFLD", required = true)
    protected VARFLD varfld;

    /**
     * Gets the value of the varfld property.
     * 
     * @return
     *     possible object is
     *     {@link VARFLD }
     *     
     */
    public VARFLD getVARFLD() {
        return varfld;
    }

    /**
     * Sets the value of the varfld property.
     * 
     * @param value
     *     allowed object is
     *     {@link VARFLD }
     *     
     */
    public void setVARFLD(VARFLD value) {
        this.varfld = value;
    }

}
