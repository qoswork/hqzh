/*
 *============================================================================
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of version 2.1 of the GNU Lesser General Public
 * License as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *============================================================================
 * Copyright (C) 2007 XenSource Inc.
 *============================================================================
 */
package com.xensource.xenapi;

import java.util.Map;
import java.util.Set;
import java.util.Date;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.HashSet;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.xmlrpc.XmlRpcException;

/**
 * A virtual network interface
 *
 * @author XenSource Inc.
 */
public class VIF extends XenAPIObject {

    /**
     * The XenAPI reference to this object.
     */
    protected final String ref;

    private VIF(String ref) {
       this.ref = ref;
    }

    public String toWireString() {
       return this.ref;
    }

    /**
     * This code helps ensure there is only one
     * VIF instance per XenAPI reference.
     */
    private static final Map<String,SoftReference<VIF>> cache = 
        new HashMap<String,SoftReference<VIF>>();

    protected static synchronized VIF getInstFromRef(String ref) {
        if(VIF.cache.containsKey(ref)) {
            VIF instance = 
                VIF.cache.get(ref).get();
            if(instance != null) {
                return instance;
            }
        }

        VIF instance = new VIF(ref);
        VIF.cache.put(ref, new SoftReference<VIF>(instance));
        return instance;
    }

    /**
     * Represents all the fields in a VIF
     */
    public static class Record  implements Types.Record{
        public String toString() {
            StringWriter writer = new StringWriter();
            PrintWriter print = new PrintWriter(writer);
            print.printf("%1$20s: %2$s\n", "uuid", this.uuid);
            print.printf("%1$20s: %2$s\n", "allowedOperations", this.allowedOperations);
            print.printf("%1$20s: %2$s\n", "currentOperations", this.currentOperations);
            print.printf("%1$20s: %2$s\n", "device", this.device);
            print.printf("%1$20s: %2$s\n", "network", this.network);
            print.printf("%1$20s: %2$s\n", "VM", this.VM);
            print.printf("%1$20s: %2$s\n", "MAC", this.MAC);
            print.printf("%1$20s: %2$s\n", "MTU", this.MTU);
            print.printf("%1$20s: %2$s\n", "otherConfig", this.otherConfig);
            print.printf("%1$20s: %2$s\n", "currentlyAttached", this.currentlyAttached);
            print.printf("%1$20s: %2$s\n", "statusCode", this.statusCode);
            print.printf("%1$20s: %2$s\n", "statusDetail", this.statusDetail);
            print.printf("%1$20s: %2$s\n", "runtimeProperties", this.runtimeProperties);
            print.printf("%1$20s: %2$s\n", "qosAlgorithmType", this.qosAlgorithmType);
            print.printf("%1$20s: %2$s\n", "qosAlgorithmParams", this.qosAlgorithmParams);
            print.printf("%1$20s: %2$s\n", "qosSupportedAlgorithms", this.qosSupportedAlgorithms);
            print.printf("%1$20s: %2$s\n", "metrics", this.metrics);
            return writer.toString();
        }

        /**
         * Convert a VIF.Record to a Map
         */
        public Map<String,Object> toMap() {
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("uuid", this.uuid == null ? "" : this.uuid);
            map.put("allowed_operations", this.allowedOperations == null ? new HashSet<Types.VifOperations>() : this.allowedOperations);
            map.put("current_operations", this.currentOperations == null ? new HashMap<String, Types.VifOperations>() : this.currentOperations);
            map.put("device", this.device == null ? "" : this.device);
            map.put("network", this.network == null ? com.xensource.xenapi.Network.getInstFromRef("OpaqueRef:NULL") : this.network);
            map.put("VM", this.VM == null ? com.xensource.xenapi.VM.getInstFromRef("OpaqueRef:NULL") : this.VM);
            map.put("MAC", this.MAC == null ? "" : this.MAC);
            map.put("MTU", this.MTU == null ? 0 : this.MTU);
            map.put("other_config", this.otherConfig == null ? new HashMap<String, String>() : this.otherConfig);
            map.put("currently_attached", this.currentlyAttached == null ? false : this.currentlyAttached);
            map.put("status_code", this.statusCode == null ? 0 : this.statusCode);
            map.put("status_detail", this.statusDetail == null ? "" : this.statusDetail);
            map.put("runtime_properties", this.runtimeProperties == null ? new HashMap<String, String>() : this.runtimeProperties);
            map.put("qos_algorithm_type", this.qosAlgorithmType == null ? "" : this.qosAlgorithmType);
            map.put("qos_algorithm_params", this.qosAlgorithmParams == null ? new HashMap<String, String>() : this.qosAlgorithmParams);
            map.put("qos_supported_algorithms", this.qosSupportedAlgorithms == null ? new HashSet<String>() : this.qosSupportedAlgorithms);
            map.put("metrics", this.metrics == null ? com.xensource.xenapi.VIFMetrics.getInstFromRef("OpaqueRef:NULL") : this.metrics);
            return map;
        }

        /**
         * unique identifier/object reference
         */
        public String uuid;
        /**
         * list of the operations allowed in this state. This list is advisory only and the server state may have changed by the time this field is read by a client.
         */
        public Set<Types.VifOperations> allowedOperations;
        /**
         * links each of the running tasks using this object (by reference) to a current_operation enum which describes the nature of the task.
         */
        public Map<String, Types.VifOperations> currentOperations;
        /**
         * order in which VIF backends are created by xapi
         */
        public String device;
        /**
         * virtual network to which this vif is connected
         */
        public Network network;
        /**
         * virtual machine to which this vif is connected
         */
        public VM VM;
        /**
         * ethernet MAC address of virtual interface, as exposed to guest
         */
        public String MAC;
        /**
         * MTU in octets
         */
        public Long MTU;
        /**
         * additional configuration
         */
        public Map<String, String> otherConfig;
        /**
         * is the device currently attached (erased on reboot)
         */
        public Boolean currentlyAttached;
        /**
         * error/success code associated with last attach-operation (erased on reboot)
         */
        public Long statusCode;
        /**
         * error/success information associated with last attach-operation status (erased on reboot)
         */
        public String statusDetail;
        /**
         * Device runtime properties
         */
        public Map<String, String> runtimeProperties;
        /**
         * QoS algorithm to use
         */
        public String qosAlgorithmType;
        /**
         * parameters for chosen QoS algorithm
         */
        public Map<String, String> qosAlgorithmParams;
        /**
         * supported QoS algorithms for this VIF
         */
        public Set<String> qosSupportedAlgorithms;
        /**
         * metrics associated with this VIF
         */
        public VIFMetrics metrics;
    }

    /**
     * Get a record containing the current state of the given VIF.
     *
     * @return all fields from the object
     */
    public VIF.Record getRecord(Connection c) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "VIF.get_record";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return Types.toVIFRecord(result);
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Get a reference to the VIF instance with the specified UUID.
     *
     * @param uuid UUID of object to return
     * @return reference to the object
     */
    public static VIF getByUuid(Connection c, String uuid) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "VIF.get_by_uuid";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(uuid)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return Types.toVIF(result);
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Create a new VIF instance, and return its handle.
     *
     * @param record All constructor arguments
     * @return Task
     */
    public static Task createAsync(Connection c, VIF.Record record) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "Async.VIF.create";
        String session = c.getSessionReference();
        Map<String, Object> record_map = record.toMap();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(record_map)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return Types.toTask(result);
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Create a new VIF instance, and return its handle.
     *
     * @param record All constructor arguments
     * @return reference to the newly created object
     */
    public static VIF create(Connection c, VIF.Record record) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "VIF.create";
        String session = c.getSessionReference();
        Map<String, Object> record_map = record.toMap();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(record_map)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return Types.toVIF(result);
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Destroy the specified VIF instance.
     *
     * @return Task
     */
    public Task destroyAsync(Connection c) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "Async.VIF.destroy";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return Types.toTask(result);
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Destroy the specified VIF instance.
     *
     */
    public void destroy(Connection c) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "VIF.destroy";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return;
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Get the uuid field of the given VIF.
     *
     * @return value of the field
     */
    public String getUuid(Connection c) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "VIF.get_uuid";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return Types.toString(result);
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Get the allowed_operations field of the given VIF.
     *
     * @return value of the field
     */
    public Set<Types.VifOperations> getAllowedOperations(Connection c) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "VIF.get_allowed_operations";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return Types.toSetOfVifOperations(result);
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Get the current_operations field of the given VIF.
     *
     * @return value of the field
     */
    public Map<String, Types.VifOperations> getCurrentOperations(Connection c) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "VIF.get_current_operations";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return Types.toMapOfStringVifOperations(result);
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Get the device field of the given VIF.
     *
     * @return value of the field
     */
    public String getDevice(Connection c) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "VIF.get_device";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return Types.toString(result);
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Get the network field of the given VIF.
     *
     * @return value of the field
     */
    public Network getNetwork(Connection c) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "VIF.get_network";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return Types.toNetwork(result);
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Get the VM field of the given VIF.
     *
     * @return value of the field
     */
    public VM getVM(Connection c) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "VIF.get_VM";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return Types.toVM(result);
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Get the MAC field of the given VIF.
     *
     * @return value of the field
     */
    public String getMAC(Connection c) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "VIF.get_MAC";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return Types.toString(result);
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Get the MTU field of the given VIF.
     *
     * @return value of the field
     */
    public Long getMTU(Connection c) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "VIF.get_MTU";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return Types.toLong(result);
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Get the other_config field of the given VIF.
     *
     * @return value of the field
     */
    public Map<String, String> getOtherConfig(Connection c) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "VIF.get_other_config";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return Types.toMapOfStringString(result);
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Get the currently_attached field of the given VIF.
     *
     * @return value of the field
     */
    public Boolean getCurrentlyAttached(Connection c) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "VIF.get_currently_attached";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return Types.toBoolean(result);
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Get the status_code field of the given VIF.
     *
     * @return value of the field
     */
    public Long getStatusCode(Connection c) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "VIF.get_status_code";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return Types.toLong(result);
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Get the status_detail field of the given VIF.
     *
     * @return value of the field
     */
    public String getStatusDetail(Connection c) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "VIF.get_status_detail";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return Types.toString(result);
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Get the runtime_properties field of the given VIF.
     *
     * @return value of the field
     */
    public Map<String, String> getRuntimeProperties(Connection c) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "VIF.get_runtime_properties";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return Types.toMapOfStringString(result);
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Get the qos/algorithm_type field of the given VIF.
     *
     * @return value of the field
     */
    public String getQosAlgorithmType(Connection c) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "VIF.get_qos_algorithm_type";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return Types.toString(result);
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Get the qos/algorithm_params field of the given VIF.
     *
     * @return value of the field
     */
    public Map<String, String> getQosAlgorithmParams(Connection c) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "VIF.get_qos_algorithm_params";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return Types.toMapOfStringString(result);
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Get the qos/supported_algorithms field of the given VIF.
     *
     * @return value of the field
     */
    public Set<String> getQosSupportedAlgorithms(Connection c) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "VIF.get_qos_supported_algorithms";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return Types.toSetOfString(result);
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Get the metrics field of the given VIF.
     *
     * @return value of the field
     */
    public VIFMetrics getMetrics(Connection c) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "VIF.get_metrics";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return Types.toVIFMetrics(result);
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Set the other_config field of the given VIF.
     *
     * @param otherConfig New value to set
     */
    public void setOtherConfig(Connection c, Map<String, String> otherConfig) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "VIF.set_other_config";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref), Marshalling.toXMLRPC(otherConfig)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return;
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Add the given key-value pair to the other_config field of the given VIF.
     *
     * @param key Key to add
     * @param value Value to add
     */
    public void addToOtherConfig(Connection c, String key, String value) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "VIF.add_to_other_config";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref), Marshalling.toXMLRPC(key), Marshalling.toXMLRPC(value)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return;
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Remove the given key and its corresponding value from the other_config field of the given VIF.  If the key is not in that Map, then do nothing.
     *
     * @param key Key to remove
     */
    public void removeFromOtherConfig(Connection c, String key) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "VIF.remove_from_other_config";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref), Marshalling.toXMLRPC(key)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return;
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Set the qos/algorithm_type field of the given VIF.
     *
     * @param algorithmType New value to set
     */
    public void setQosAlgorithmType(Connection c, String algorithmType) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "VIF.set_qos_algorithm_type";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref), Marshalling.toXMLRPC(algorithmType)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return;
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Set the qos/algorithm_params field of the given VIF.
     *
     * @param algorithmParams New value to set
     */
    public void setQosAlgorithmParams(Connection c, Map<String, String> algorithmParams) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "VIF.set_qos_algorithm_params";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref), Marshalling.toXMLRPC(algorithmParams)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return;
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Add the given key-value pair to the qos/algorithm_params field of the given VIF.
     *
     * @param key Key to add
     * @param value Value to add
     */
    public void addToQosAlgorithmParams(Connection c, String key, String value) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "VIF.add_to_qos_algorithm_params";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref), Marshalling.toXMLRPC(key), Marshalling.toXMLRPC(value)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return;
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Remove the given key and its corresponding value from the qos/algorithm_params field of the given VIF.  If the key is not in that Map, then do nothing.
     *
     * @param key Key to remove
     */
    public void removeFromQosAlgorithmParams(Connection c, String key) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "VIF.remove_from_qos_algorithm_params";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref), Marshalling.toXMLRPC(key)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return;
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Hotplug the specified VIF, dynamically attaching it to the running VM
     *
     * @return Task
     */
    public Task plugAsync(Connection c) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "Async.VIF.plug";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return Types.toTask(result);
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Hotplug the specified VIF, dynamically attaching it to the running VM
     *
     */
    public void plug(Connection c) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "VIF.plug";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return;
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Hot-unplug the specified VIF, dynamically unattaching it from the running VM
     *
     * @return Task
     */
    public Task unplugAsync(Connection c) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "Async.VIF.unplug";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return Types.toTask(result);
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Hot-unplug the specified VIF, dynamically unattaching it from the running VM
     *
     */
    public void unplug(Connection c) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "VIF.unplug";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session), Marshalling.toXMLRPC(this.ref)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return;
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Return a list of all the VIFs known to the system.
     *
     * @return references to all objects
     */
    public static Set<VIF> getAll(Connection c) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "VIF.get_all";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return Types.toSetOfVIF(result);
        }
        throw new Types.BadServerResponse(response);
    }

    /**
     * Return a map of VIF references to VIF records for all VIFs known to the system.
     *
     * @return records of all objects
     */
    public static Map<VIF, VIF.Record> getAllRecords(Connection c) throws
       Types.BadServerResponse,
       XmlRpcException {
        String method_call = "VIF.get_all_records";
        String session = c.getSessionReference();
        Object[] method_params = {Marshalling.toXMLRPC(session)};
        Map response = c.dispatch(method_call, method_params);
        if(response.get("Status").equals("Success")) {
            Object result = response.get("Value");
            return Types.toMapOfVIFVIFRecord(result);
        }
        throw new Types.BadServerResponse(response);
    }

}