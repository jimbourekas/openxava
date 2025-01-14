package org.openxava.web;

import org.openxava.model.meta.*;
import org.openxava.view.*;

/**
 * Utilities to help in web events of editors used from JSP and taglibs. <br>
 * 
 * This class cannot be called just Editors because there is a package called
 * editors, and that does not like to JSP processors. 
 * 
 * @since 7.2
 * @author Javier Paniza 
 */

public class EditorsEvents { 
	
	/** @since 7.2 */
	public static String onChangeCalculateDataAttributes(String application, String module, View rootView, String changedProperty) {  
		String calculatedProperty = rootView.getDependentCalculationPropertyNameFor(changedProperty);
		String calculatedPropertyKey = org.openxava.web.Ids.decorate(application, module, calculatedProperty);
		MetaProperty calculatedMetaProperty = rootView.getMetaProperty(calculatedProperty);
		StringBuffer sb = new StringBuffer();
		sb.append("data-calculated-property='");
		sb.append(calculatedPropertyKey);
		sb.append("' data-scale='");
		sb.append(calculatedMetaProperty.getScale());
		sb.append("'");
		return sb.toString();
	}

}
