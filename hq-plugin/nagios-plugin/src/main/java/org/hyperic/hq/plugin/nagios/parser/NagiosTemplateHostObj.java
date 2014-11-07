/*
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 * 
 * Copyright (C) [2004-2007], Hyperic, Inc.
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

package org.hyperic.hq.plugin.nagios.parser;

import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class NagiosTemplateHostObj
    extends NagiosObj
{
    private static final Pattern _nameEx = Pattern.compile("^\\s*name"),
                                 _checkCmdEx = Pattern.compile("^\\s*check_command"),
                                 _contactsEx = Pattern.compile("^\\s*contacts"),
                                 _contactGroupsEx =
                                    Pattern.compile("^\\s*contact_groups");
    private List _contactGroups,
                 _contacts;

    private String _name,
                   _cmdName,
                   _address;

    NagiosCommandObj _cmdObj;
    private Set _resources;

    protected NagiosTemplateHostObj()
    {
        super();
    }

    protected void parseCfg(String cfgBlock)
        throws NagiosParserException
    {
        String[] lines = cfgBlock.split("\\n");
        for (int i=0; i<lines.length; i++)
        {
            String line = lines[i];
            if (_blankLine.matcher(line).find() ||
                _comment.matcher(line).find()) {
                continue;
            }
            if (_checkCmdEx.matcher(line).find()) {
                setCheckCmd(line);
            } else if (_nameEx.matcher(line).find()) {
                setName(line);
            } else if (_contactsEx.matcher(line).find()) {
                setContacts(line);
            } else if (_contactGroupsEx.matcher(line).find()) {
                setContactGroups(line);
            }
        }
        if (_name == null)
        {
            debug("ERROR:  config -> " +cfgBlock);
            throw new NagiosParserException(cfgBlock);
        }
    }

    String getCmdName()
    {
        return _cmdName;
    }

    NagiosCommandObj getCmdObj()
    {
        return _cmdObj;
    }

    public String getChkAliveCmd()
    {
        return _cmdObj.getCmdExec();
    }

    public String getKey()
    {
        return _name;
    }

    private void setName(String line)
    {
        line = removeInlineComments(line);
        String[] name = line.split("\\s+");
        _name = name[name.length-1];
    }

    private void setCheckCmd(String line)
    {
        line = removeInlineComments(line);
        String[] cmd = line.split("\\s+");
        _cmdName = cmd[cmd.length-1];
    }

    private void setContacts(String line)
    {
        String[] contacts = line.trim().split(",");
        _contacts = Arrays.asList(contacts);
    }

    private void setContactGroups(String line)
    {
        String[] contacts = line.trim().split(",");
        _contactGroups = Arrays.asList(contacts);
    }

    public int getType()
    {
        return HOST_TEMPL_TYPE;
    }

    public String toString()
    {
        return "\nName -> "+_name+
               "\nContacts -> "+_contacts+
               "\nCmdName   -> "+_cmdName+
               "\nContactGroups -> "+_contactGroups;
    }

    public int hashCode()
    {
        return _name.hashCode();
    }

    void resolveDependencies(NagiosParser parser)
    {
        try
        {
            if (_resources == null)
            {
                Integer type = new Integer(RESOURCE_TYPE);
                _resources = parser.get(type);
            }
            if (_cmdObj == null && _cmdName != null)
            {
                Integer type = new Integer(COMMAND_TYPE);
                _cmdObj = (NagiosCommandObj)parser.get(type, _cmdName);
            }
        }
        catch (NagiosParserInternalException e) {
            debug(e);
        }
        catch (NagiosTypeNotSupportedException e) {
            debug(e);
        }
    }

    public boolean equals(Object rhs)
    {
        if (this == rhs)
            return true;
        if (rhs instanceof NagiosTemplateHostObj)
            return equals((NagiosTemplateHostObj)rhs);
        return false;
    }

    private boolean equals(NagiosTemplateHostObj rhs)
    {
        if (rhs._name.equals(_name))
            return true;
        return false;
    }

    public int compareTo(Object rhs)
        throws ClassCastException
    {
        if (rhs instanceof NagiosTemplateHostObj) {
            return _name.compareTo(((NagiosTemplateHostObj)rhs)._name);
        } else {
            throw new ClassCastException();
        }
    }
}
