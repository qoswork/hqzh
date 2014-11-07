/**
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 *  "derived work".
 *
 *  Copyright (C) [2009-2010], VMware, Inc.
 *  This file is part of HQ.
 *
 *  HQ is free software; you can redistribute it and/or modify
 *  it under the terms version 2 of the GNU General Public License as
 *  published by the Free Software Foundation. This program is distributed
 *  in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 *  even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 *  PARTICULAR PURPOSE. See the GNU General Public License for more
 *  details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 *  USA.
 *
 */

package org.hyperic.hq.plugin.xen;

import java.util.Iterator;
import java.util.Map;

import org.hyperic.hq.product.Metric;

import com.xensource.xenapi.Connection;
import com.xensource.xenapi.VM;
import com.xensource.xenapi.VMMetrics;
import com.xensource.xenapi.Types.VmPowerState;

public class XenVmCollector extends XenCollector {

    public void collect() {
        try {
            Connection conn = connect();
            VM vm = VM.getByUuid(conn, getServerUUID());
            double avail;
            VmPowerState state = vm.getPowerState(conn);
            if (state == VmPowerState.RUNNING) {
                avail = Metric.AVAIL_UP;                
            }
            else if ((state == VmPowerState.PAUSED) ||
                     (state == VmPowerState.SUSPENDED))
            {
                avail = Metric.AVAIL_PAUSED;
            }
            else {
                avail = Metric.AVAIL_DOWN;
            }
            setAvailability(avail);
            VMMetrics metrics = vm.getMetrics(conn);
            VMMetrics.Record record = metrics.getRecord(conn);
            setValue("MemoryActual", record.memoryActual);
            long startTime = record.startTime.getTime();
            setValue("StartTime", startTime);
            setValue("Uptime", System.currentTimeMillis() - startTime);
            Map<Long, Double> cpus = record.VCPUsUtilisation;
            double usage = 0;
            for (Iterator it = cpus.values().iterator();
                 it.hasNext();)
            {
                usage += ((Double)it.next()).doubleValue();
            }
            setValue("CPUUsage", usage);
        } catch (Exception e) {
            setAvailability(false);
            setErrorMessage(e.getMessage());
        }
    }
}
