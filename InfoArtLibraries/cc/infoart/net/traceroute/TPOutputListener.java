/*
 * Created on May 18, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package cc.infoart.net.traceroute;

/**
 * @author carlos
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

import java.util.EventListener;
import java.util.Map;

public interface TPOutputListener extends EventListener
{
	public abstract void resultsReceived(Map[] m);
}
