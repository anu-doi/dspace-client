//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.06.19 at 03:37:45 PM AEST 
//


package au.edu.anu.dspace.client.swordv2.digitisation.iiirecord;

import java.util.ArrayList;
import java.util.List;
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
    "fixfld"
})
@XmlRootElement(name = "INVOICE")
public class INVOICE {

    @XmlElement(name = "FIXFLD", required = true)
    protected List<FIXFLD> fixfld;

    /**
     * Gets the value of the fixfld property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fixfld property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFIXFLD().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FIXFLD }
     * 
     * 
     */
    public List<FIXFLD> getFIXFLD() {
        if (fixfld == null) {
            fixfld = new ArrayList<FIXFLD>();
        }
        return this.fixfld;
    }

}
