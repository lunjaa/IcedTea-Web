/*   Copyright (C) 2013 Red Hat, Inc.

 This file is part of IcedTea.

 IcedTea is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License as published by
 the Free Software Foundation, version 2.

 IcedTea is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with IcedTea; see the file COPYING.  If not, write to
 the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 02110-1301 USA.

 Linking this library statically or dynamically with other modules is
 making a combined work based on this library.  Thus, the terms and
 conditions of the GNU General Public License cover the whole
 combination.

 As a special exception, the copyright holders of this library give you
 permission to link this library with independent modules to produce an
 executable, regardless of the license terms of these independent
 modules, and to copy and distribute the resulting executable under
 terms of your choice, provided that you also meet, for each linked
 independent module, the terms and conditions of the license of that
 module.  An independent module is a module which is not derived from
 or based on this library.  If you modify this library, you may extend
 this exception to your version of the library, but you are not
 obligated to do so.  If you do not wish to do so, delete this
 exception statement from your version.
 */
package net.adoptopenjdk.icedteaweb.client.parts.dialogs.security.appletextendedsecurity;

import net.adoptopenjdk.icedteaweb.i18n.Translator;

public enum AppletSecurityLevel {

    DENY_ALL, DENY_UNSIGNED, ASK_UNSIGNED, ALLOW_UNSIGNED;

    public static String allToString() {
        return DENY_ALL.toChars() + " " + DENY_UNSIGNED.toChars() + " " + ASK_UNSIGNED.toChars() + " " + ALLOW_UNSIGNED.toChars();
    }

    public String toChars() {
       return this.name();
    }

    public String toExplanation() {
        switch (this) {
            case DENY_ALL:
                return Translator.R("APPEXTSECappletSecurityLevelExtraHighId") + " - " + Translator.R("APPEXTSECappletSecurityLevelExtraHighExplanation");
            case DENY_UNSIGNED:
                return Translator.R("APPEXTSECappletSecurityLevelVeryHighId") + " - " + Translator.R("APPEXTSECappletSecurityLevelVeryHighExplanation");
            case ASK_UNSIGNED:
                return Translator.R("APPEXTSECappletSecurityLevelHighId") + " - " + Translator.R("APPEXTSECappletSecurityLevelHighExplanation");
            case ALLOW_UNSIGNED:
                return Translator.R("APPEXTSECappletSecurityLevelLowId") + " - " + Translator.R("APPEXTSECappletSecurityLevelLowExplanation");
        }
        throw new RuntimeException("Unknown AppletSecurityLevel");
    }

    public static AppletSecurityLevel fromString(String s) {
        // see https://docs.oracle.com/javase/7/docs/technotes/guides/jweb/jcp/properties.html
        if ("MEDIUM".equalsIgnoreCase(s)) {
            return ALLOW_UNSIGNED;
        }
        if ("HIGH".equalsIgnoreCase(s)) {
            return ASK_UNSIGNED;
        }
        if ("VERY_HIGH".equalsIgnoreCase(s)) {
            return DENY_UNSIGNED;
        }
        return AppletSecurityLevel.valueOf(s.toUpperCase());
    }

    @Override
    public String toString() {
        return toExplanation();
    }

    public static AppletSecurityLevel getDefault() {
        return ASK_UNSIGNED;
    }
}
