package thomasl86.bitbucket.org.androidremoteclient;

/**
 * Created by thomas on 29.01.16.
 */
/*
 * Copyright (C) 2006-2008 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * and Open Source Software ("FLOSS") applications as described in Alfresco's
 * FLOSS exception.  You should have recieved a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * http://www.alfresco.com/legal/licensing"
 */

//package net.gqu.utils;

import java.util.StringTokenizer;

/**
 * TCP/IP Address Utility Class
 *
 * @author gkspencer
 */
public class IPAddress {



    /**
     * Convert a raw IP address array as a String
     *
     * @param ipaddr byte[]
     * @return String
     */
    public final static String asString(byte[] ipaddr) {

        //  Check if the address is valid

        if ( ipaddr == null || ipaddr.length != 4)
            return null;

        //  Convert the raw IP address to a string

        StringBuffer str = new StringBuffer();

        str.append((int) ( ipaddr[0] & 0xFF));
        str.append(".");
        str.append((int) ( ipaddr[1] & 0xFF));
        str.append(".");
        str.append((int) ( ipaddr[2] & 0xFF));
        str.append(".");
        str.append((int) ( ipaddr[3] & 0xFF));

        //  Return the address string

        return str.toString();
    }

    /**
     * Convert a raw IP address array as a String
     *
     * @param ipaddr int
     * @return String
     */
    public final static String asString(int ipaddr) {

        byte[] ipbyts = new byte[4];
        ipbyts[0] = (byte) ((ipaddr >> 24) & 0xFF);
        ipbyts[1] = (byte) ((ipaddr >> 16) & 0xFF);
        ipbyts[2] = (byte) ((ipaddr >> 8) & 0xFF);
        ipbyts[3] = (byte) (ipaddr & 0xFF);

        return asString(ipbyts);
    }


}
