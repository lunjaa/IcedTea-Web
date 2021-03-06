// Copyright (C) 2009 Red Hat, Inc.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
package net.sourceforge.jnlp.util;

import mslinks.ShellLink;
import net.adoptopenjdk.icedteaweb.IcedTeaWebConstants;
import net.adoptopenjdk.icedteaweb.LazyLoaded;
import net.adoptopenjdk.icedteaweb.io.FileUtils;
import net.adoptopenjdk.icedteaweb.jvm.JvmUtils;
import net.adoptopenjdk.icedteaweb.logging.Logger;
import net.adoptopenjdk.icedteaweb.logging.LoggerFactory;
import net.adoptopenjdk.icedteaweb.ui.swing.dialogresults.AccessWarningPaneComplexReturn;
import net.sourceforge.jnlp.JNLPFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.adoptopenjdk.icedteaweb.IcedTeaWebConstants.DOUBLE_QUOTE;
import static net.sourceforge.jnlp.util.WindowsShortcutManager.getWindowsShortcutsFile;

/**
 * Based on https://github.com/DmitriiShamrikov/mslinks
 */
public class WindowsDesktopEntry implements GenericDesktopEntry {

    private static final Logger LOG = LoggerFactory.getLogger(WindowsDesktopEntry.class);

    private final JNLPFile file;
    private final LazyLoaded<String> iconLocation;

    public WindowsDesktopEntry(JNLPFile file) {
        this.file = file;
        this.iconLocation = new LazyLoaded<>(() -> new XDesktopEntry(file).cacheAndGetIconLocation());
    }

    private String getShortcutFileName() {
        return getShortcutName() + ".lnk";
    }

    private String getShortcutName() {
        return sanitize(file.getShortcutName());
    }

    private String sanitize(String fileName) {
        if (fileName != null) {/* key=value pairs must be a single line */
            //return first line or replace new lines by space?
            return FileUtils.sanitizeFileName(fileName, '-').split("\\R")[0];
        }
        return "";
    }

    private String getDesktopLnkPath() {
        return System.getenv("userprofile") + "/Desktop/" + getShortcutFileName();
    }

    @Override
    public File getDesktopIconFile() {
        return new File(getDesktopLnkPath());
    }

    @Override
    public void createShortcutOnWindowsDesktop() throws IOException {
        ShellLink sl = ShellLink.createLink(getJavaWsBin()).setCMDArgs(quoted(file.getSourceLocation()));
        if (iconLocation.get() != null) {
            sl.setIconLocation(iconLocation.get());
        }
        final String path = getDesktopLnkPath();
        sl.saveTo(path);
        // write shortcut path to list
        manageShortcutList(ManageMode.A, path);
    }

    private String getJavaWsBin() throws FileNotFoundException {
        final String javaWsBin = JvmUtils.getJavaWsBin();

        // first look for exe
        final String javaWsBinExe = javaWsBin + ".exe";
        if (new File(javaWsBinExe).exists()) {
            LOG.debug("For Shortcut Returning EXE : {}", javaWsBinExe);
            return javaWsBinExe;
        }

        if (new File(javaWsBin).exists()) {
            LOG.debug("For Shortcut Returning {}", javaWsBin);
            return javaWsBin;
        }

        LOG.debug("Could not find the javaws binary to create desktop shortcut");
        throw new FileNotFoundException("Could not find the javaws binary to create desktop shortcut");
    }


    @Override
    public void createWindowsMenu() throws IOException {
        // create menu item
        String pathSuffix;
        try {
            pathSuffix = file.getInformation().getShortcut().getMenu().getSubMenu();
        }
        catch (NullPointerException npe) {
            LOG.error(IcedTeaWebConstants.DEFAULT_ERROR_MESSAGE, npe);
            pathSuffix = null;
        }        
        if (pathSuffix == null) {
            pathSuffix = getShortcutName();
        }
                        
        final String path = System.getenv("userprofile") + "/AppData/Roaming/Microsoft/Windows/Start Menu/Programs/" + pathSuffix;        
        // check to see if menu dir exists and create if not
        final File menuDir = new File(path);
        if (!menuDir.exists()) {
            menuDir.mkdir();
        }
        final String JavaWsBin = getJavaWsBin();
        final ShellLink sl = ShellLink.createLink(JavaWsBin).setCMDArgs(quoted(file.getSourceLocation()));
        // setup uninstall shortcut
        final ShellLink ul = ShellLink.createLink(JavaWsBin).setCMDArgs("-Xclearcache " + quoted(file.getFileLocation()));
        if (iconLocation.get() != null) {
            sl.setIconLocation(iconLocation.get());
            ul.setIconLocation(iconLocation.get());
        }
        final String link = getShortcutFileName();
        sl.saveTo(path + "/" + link);
        ul.saveTo(path + "/Uninstall " + link);
        // write shortcuts to list
        manageShortcutList(ManageMode.A, path + "/" + link);
        manageShortcutList(ManageMode.A, path + "/Uninstall " + link);
    }

    private void manageShortcutList(ManageMode mode, String path) throws IOException {
        if (!getWindowsShortcutsFile().exists()) {
            getWindowsShortcutsFile().createNewFile();
        }
        LOG.debug("Using WindowsShortCutManager {}", getWindowsShortcutsFile().toString());
        if (ManageMode.A == mode) {
            List<String> lines = null;
            // if UTF-8 fails, try ISO-8859-1
            try {
                LOG.debug("Reading Shortcuts with UTF-8");
                lines = Files.readAllLines(getWindowsShortcutsFile().toPath(), UTF_8);
            } catch (MalformedInputException me) {
                LOG.debug("Fallback to reading Shortcuts with default encoding {}", Charset.defaultCharset().name());
                lines = Files.readAllLines(getWindowsShortcutsFile().toPath(), Charset.defaultCharset());
            }
            Iterator it = lines.iterator();
            String sItem = "";
            String sPath;
            Boolean fAdd = true;
            // check to see if line exists, if not add it
            while (it.hasNext()) {
                sItem = it.next().toString();
                String[] sArray = sItem.split(",");
                String application = sArray[0]; //??
                sPath = sArray[1];
                if (sPath.equalsIgnoreCase(path)) {
                    // it exists don't add
                    fAdd = false;
                    break;
                }
            }
            if (fAdd) {
                LOG.debug("Default encoding is {}", Charset.defaultCharset().name());
                LOG.debug("Adding Shortcut to list = {} with UTF-8 encoding", sItem);
                String scInfo = file.getFileLocation().toString() + ",";
                scInfo += path + "\r\n";
                Files.write(getWindowsShortcutsFile().toPath(), scInfo.getBytes(UTF_8), StandardOpenOption.APPEND);
            }
        }
    }

    @Override
    public void createDesktopShortcuts(AccessWarningPaneComplexReturn.ShortcutResult menu, AccessWarningPaneComplexReturn.ShortcutResult desktop) {
        throw new UnsupportedOperationException("not supported on windows like systems");
    }

    @Override
    public void refreshExistingShortcuts(boolean desktop, boolean menu) {
        throw new UnsupportedOperationException("not supported on windows like systems");
    }

    @Override
    public File getGeneratedJnlpFileName() {
        throw new UnsupportedOperationException("not supported on windows like systems");
    }

    @Override
    public File getLinuxMenuIconFile() {
        throw new UnsupportedOperationException("not supported on windows like systems");
    }

    private static enum ManageMode {
        //append?
        A
    }

    private String quoted(URL url) {
        return DOUBLE_QUOTE + url.toExternalForm() + DOUBLE_QUOTE;
    }
}
