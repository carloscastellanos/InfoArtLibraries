/*
 * Created on May 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author carlos
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package cc.infoart.net.traceroute;

public interface TraceTracker
{
    public abstract void addTraceOutputListener(TraceOutputListener tol);

	public abstract void removeTraceOutputListener(TraceOutputListener tol);

}
