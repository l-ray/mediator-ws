/**
 * 
 */
package de.clubspot.mediator;

import java.net.URL;

/**
 * @author l-ray
 *
 */
public interface SourceAdapter {
	
	public void setSource(URL sourceURL);
	
	public String getResult();
	
}
