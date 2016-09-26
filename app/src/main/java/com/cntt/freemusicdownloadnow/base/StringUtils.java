/*
 * Copyright (C) Global Enterprise Mobility (GEM Vietnam) - All Rights Reserved
 * Website: http://www.gemvietnam.com
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Ngo Thanh Le <lent@gemvietnam.com>, October 2014
 */

package com.cntt.freemusicdownloadnow.base;


public class StringUtils
{

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return !StringUtils.isEmpty(cs);
    }

    public static String trimToEmpty(String str) {
        return str == null ? CS.EMPTY : str.trim();
    }

    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((!Character.isWhitespace(cs.charAt(i)))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !StringUtils.isBlank(cs);
    }

    public static String trim(String str) {
        return str == null ? null : str.trim();
    }
}
