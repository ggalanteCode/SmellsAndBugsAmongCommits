/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Parsers;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Strategy pattern to extrapolate data from the xmlParser into objects
 * @author mattia
 */
public interface Extrapolator {
    /**
    * Method to extrapolate the data of a xml node into an object
    * @param n the node
    * @param attributes map of attributes of the node
    * @author mattia
    */
    public void extrapolate(Node n , NamedNodeMap attributes);
    /**
    * Method that must be used to do the final operations at the end of the parsing
    * @author mattia
    */
    public void finalOperations();
}
