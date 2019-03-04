//
//  TraceOutputListener.java
//  Traceroute
//
//  Created by Carlos Castellanos on 5/14/05.
//

package cc.infoart.net.traceroute;

import java.util.EventListener;

public interface TraceOutputListener extends EventListener {
    public abstract void resultsReceived(String s);
}

