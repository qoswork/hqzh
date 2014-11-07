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
package org.hyperic.hq.plugin.websphere;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.exception.ConnectorException;
import java.util.Map;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.j2ee.statistics.CountStatistic;
import javax.management.j2ee.statistics.RangeStatistic;
import javax.management.j2ee.statistics.Statistic;
import javax.management.j2ee.statistics.Stats;
import javax.management.j2ee.statistics.TimeStatistic;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperic.hq.product.Collector;
import org.hyperic.hq.product.CollectorResult;
import org.hyperic.hq.product.Metric;
import org.hyperic.hq.product.MetricUnreachableException;
import org.hyperic.hq.product.MetricValue;
import org.hyperic.hq.product.PluginException;

public abstract class WebsphereCollector extends Collector {

    private final Log log = LogFactory.getLog(this.getClass());
    private ObjectName name;
    private String domain;

    protected final ObjectName getObjectName() {
        return this.name;
    }

    protected final void setObjectName(ObjectName name) {
        this.name = name;
    }

    protected final ObjectName newObjectNamePattern(String attrs)
            throws PluginException {

        try {
            return new ObjectName(this.domain + ":" + attrs + ",*");
        } catch (MalformedObjectNameException e) {
            throw new PluginException(e.getMessage());
        }
    }

    protected final String getServerAttributes() {
        return "J2EEServer=" + getServerName() + ","
                + "node=" + getNodeName();
    }

    protected final String getProcessAttributes() {
        return "process=" + getServerName() + ","
                + "node=" + getNodeName();
    }

    protected final void init() throws PluginException {
        if (log.isDebugEnabled()) {
            log.debug("[init] (" + getClass().getSimpleName() + ") props=" + getProperties());
        }

        AdminClient mServer = getMBeanServer();
        assert mServer != null;
        if (mServer == null) {
            return;
        }

        try {
            this.domain = mServer.getDomainName();
        } catch (ConnectorException e) {
            throw new PluginException(e.getMessage(), e);
        }

        //resource specific stuff
        init(mServer);
        if (this.name != null) {
            setSource(this.name.toString());
        }
    }

    protected abstract void init(AdminClient mServer) throws PluginException;

    private final String getNodeName() {
        return getProperties().getProperty(WebsphereProductPlugin.PROP_SERVER_NODE);
    }

    protected final String getServerName() {
        return getProperties().getProperty(WebsphereProductPlugin.PROP_SERVER_NAME);
    }

    protected final String getModuleName() {
        return getProperties().getProperty("Module");
    }

    private AdminClient getMBeanServer() {
        try {
            return WebsphereUtil.getMBeanServer(getProperties());
        } catch (MetricUnreachableException e) {
            setAvailability(false);
            setErrorMessage(e.getMessage());
            if (log.isDebugEnabled()) {
                log.error(e.getMessage(), e);
            }
            return null;
        }

    }

    protected ObjectName resolve(AdminClient mServer, ObjectName name)
            throws PluginException {
        try {
            return WebsphereUtil.resolve(mServer, name);
        } catch (PluginException e) {
            if (log.isDebugEnabled()) {
                log.error(e.getMessage(), e);
            }
            throw e;
        }
    }

    protected final Object getAttribute(AdminClient mServer,
            ObjectName name,
            String attr)
            throws PluginException {

        try {
            WebsphereStopWatch timer = new WebsphereStopWatch();
            Object o = mServer.getAttribute(name, attr);
            if (log.isDebugEnabled()) {
                String call = "getAttribute(" + name + ", " + attr + ")";
                if (o == null) {
                    log.debug(call + "==null");
                }
                if (timer.isTooLong()) {
                    log.debug(call + " took: "
                            + timer.getElapsedSeconds() + " seconds");
                }
            }
            return o;
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("getAttribute(" + name + ", " + attr
                        + "): " + e.getMessage(), e);
            }
            throw new PluginException(e.getMessage());
        }
    }

    protected final Stats getStats(AdminClient mServer, ObjectName name) throws PluginException {
        Stats stats = (Stats) getAttribute(mServer, name, "stats");
        log.info("[getStats] name = " + name);
        for (int i = 0; i < stats.getStatistics().length; i++) {
            Statistic statistic = stats.getStatistics()[i];
            log.info("[getStats] [" + getModuleName() + "] => statistic: " + statistic);
        }
        return stats;
    }

    private double getStatCount(Statistic stat) {
        if (stat instanceof CountStatistic) {
            return ((CountStatistic) stat).getCount();
        } else if (stat instanceof RangeStatistic) {
            return ((RangeStatistic) stat).getCurrent();
        } else if (stat instanceof TimeStatistic) {
            // get the average time (same as MxUtil)
            double value;
            long count = ((TimeStatistic) stat).getCount();
            if (count == 0) {
                value = 0;
            } else {
                value = ((TimeStatistic) stat).getTotalTime() / count;
            }
            return value;
        } else {
            log.error("Unsupported stat type: "
                    + stat.getName() + "/" + stat.getClass().getName());
            return MetricValue.VALUE_NONE;
        }
    }

    protected final double getStatCount(Stats stats, String metric) {
        Statistic stat = stats.getStatistic(metric);
        if (stat == null) {
            return MetricValue.VALUE_NONE;
        }
        return getStatCount(stat);
    }

    protected final void collectStatCount(Stats stats, String[][] attrs) {
        Stats[] _stats = {stats};
        collectStatCount(_stats, attrs);
    }

    protected final void collectStatCount(Stats[] stats, String[][] attrs) {
        for (int i = 0; i < attrs.length; i++) {
            String[] entry = attrs[i];
            String statKey = entry[0];
            String pmiKey = entry.length == 1 ? statKey : entry[1];
            double val = 0;
            for (Stats stat : stats) {
                double _val = getStatCount(stat, statKey);
                if (_val != MetricValue.VALUE_NONE) {
                    val += _val;
                }
            }
            setValue(pmiKey, val);
        }
    }

    @Override
    public final MetricValue getValue(Metric metric, CollectorResult result) {
        return super.getValue(metric, result);
    }

    protected final boolean collectStats(ObjectName name) throws PluginException {
        AdminClient mServer = getMBeanServer();
        if (mServer == null) {
            return false;
        }
        return collectStats(mServer, name);
    }

    private boolean collectStats(AdminClient mServer, ObjectName oname) throws PluginException {
        Stats stats = getStats(mServer, oname);
        if (stats == null) {
            setAvailability(false);
            return false;
        }
        setAvailability(true);

        String[] names = stats.getStatisticNames();
        Map values = getResult().getValues();
        for (int i = 0; i < names.length; i++) {
            double val = getStatCount(stats, names[i]);

            //pmi names have lowercase 1st char
            String name =
                    Character.toLowerCase(names[i].charAt(0))
                    + names[i].substring(1);
            values.put(name, new Double(val));
        }

        return true;
    }

    public final void collect() {
        String serverName = getProperties().getProperty("server.name");
        String module = getProperties().getProperty("Module");

        if (log.isDebugEnabled()) {
            log.debug("[collect] [" + serverName + "] name=" + getObjectName());
            log.debug("[collect] [" + serverName + "] module=" + module);
            log.debug("[collect] [" + serverName + "] getProperties=" + getProperties());
        }
        setAvailability(false);

        if (getObjectName() == null) {
            try {
                init();
            } catch (PluginException ex) {
                log.debug("[collect] [" + serverName + "] error!!! " + ex.getMessage());
                return;
            }
        }

        AdminClient mServer = getMBeanServer();
        if (mServer == null) {
            return;
        }

        setAvailability(true);

        try {
            collect(mServer);
        } catch (PluginException e) {
            setAvailability(false);
            setMessage(e.getMessage());
            log.debug("[collect] [" + serverName + "] error:" + e.getMessage(), e);
        }
    }

    protected abstract void collect(AdminClient mServer) throws PluginException;
}
