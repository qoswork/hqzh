/*
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 * 
 * Copyright (C) [2004, 2005, 2006], Hyperic, Inc.
 * This file is part of HQ.
 * 
 * HQ is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 */

package org.hyperic.hq.plugin.vmware;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperic.hq.product.Collector;
import org.hyperic.hq.product.PluginException;
import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.ProcMem;
import org.hyperic.sigar.ProcState;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.vmware.VMwareException;

public class VMCollector extends Collector {

    private String _config;
    private Sigar _sigar;
    private static Map pidMap = new HashMap();
    private final static String VMWARE_VMX = "vmware-vmx";
    private static final String VMKLOAD_APP = "vmkload_app";
    private static final Log log =
        LogFactory.getLog(VMCollector.class.getName());

    //create a map of VM .vmx config file to process pid
    private void getPids() {
        pidMap.clear();

        try {
            long[] pids = _sigar.getProcList();
            for (int i=0; i<pids.length; i++) {
                try {
                    ProcState state =
                        _sigar.getProcState(pids[i]);
                    String name = state.getName();
                    if (!(name.equals(VMWARE_VMX) ||
                          name.equals(VMKLOAD_APP)))
                    {
                        continue;
                    }

                    String[] args = _sigar.getProcArgs(pids[i]);
                    for (int j=0; j<args.length; j++) {
                        String arg = args[j];
                        if (arg.endsWith(".vmx")) {
                            arg = arg.toLowerCase();
                            pidMap.put(arg, new Long(pids[i]));
                            log.debug(arg + " pid=" + pids[i]);
                        }
                    }
                } catch (SigarException e) {
                }
            }
        } catch (SigarException e) {
        }

        log.debug("Found " + pidMap.size() + " VM processes");
    }

    static Long getPid(String vmx) {
        return (Long)pidMap.get(vmx.toLowerCase());
    }

    public void collect() {
        getPids(); //XXX we should call this less often

        try {
            Map values =
                VMwareMetrics.getInstance(getProperties(), _config);

            addValues(values);
        } catch (VMwareException e) {
            setErrorMessage(e.getMessage());
        }

        //vm.getPid() is gone in 3.0
        Long pid = getPid(_config);
        if (pid != null) {
            getProcessMetrics(pid.longValue());
        }
    }

    private void getProcessMetrics(long pid) {
        try {
            ProcMem mem = _sigar.getProcMem(pid);

            setValue("ProcSize", mem.getSize());
            setValue("ProcResident", mem.getResident());
            setValue("ProcPageFaults", mem.getPageFaults());
        } catch (SigarException e) {
            String msg = "getProcMem(" + pid + "): " + e.getMessage();
            log.error(msg);
        }

        try {
            ProcCpu cpu = _sigar.getProcCpu(pid);            
            long now = System.currentTimeMillis();
            setValue("ProcUptime",
                     now - cpu.getStartTime());
            setValue("ProcSysTime", cpu.getSys());
            setValue("ProcUserTime", cpu.getUser());
            setValue("ProcTotalTime", cpu.getTotal());
            setValue("ProcCpuUsage", cpu.getPercent());
        } catch (SigarException e) {
            String msg = "getProcCpu(" + pid + "): " + e.getMessage();
            log.error(msg);
        }
    }

    protected void init() throws PluginException {
        VMwareProductPlugin.checkIsLoaded();

        _config = getProperties().getProperty("Config");
        setSource(new File(_config).getName());
        _sigar = new Sigar();
    }
}
