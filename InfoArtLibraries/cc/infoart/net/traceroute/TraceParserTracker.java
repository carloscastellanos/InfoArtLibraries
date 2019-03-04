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
public interface TraceParserTracker
{
	public abstract void addTPOutputListener(TPOutputListener tpol);

    	public abstract void removeTPOutputListener(TPOutputListener tpol);
}
