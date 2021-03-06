/**
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license.txt included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package org.python.pydev.debug.core;

import java.io.File;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.python.pydev.core.bundle.ImageCache;
import org.python.pydev.debug.newconsole.prefs.ColorManager;
import org.python.pydev.plugin.PydevPlugin;

/**
 * The main plugin for Python Debugger.
 * 
 * Standard plugin functionality: preferences, logging, some utility functions
 */
public class PydevDebugPlugin extends AbstractUIPlugin {
    //The shared instance.
    private static PydevDebugPlugin plugin;

    public ImageCache imageCache;

    public PydevDebugPlugin() {
        plugin = this;
    }

    public void start(BundleContext context) throws Exception {
        super.start(context);
        imageCache = new ImageCache(PydevDebugPlugin.getDefault().getBundle().getEntry("/"));
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
        ColorManager.getDefault().dispose();
        imageCache.dispose();
    }

    public static PydevDebugPlugin getDefault() {
        return plugin;
    }

    public static String getPluginID() {
        PydevDebugPlugin d = getDefault();
        if (d == null) {
            return "Unable to get id";
        }
        return d.getBundle().getSymbolicName();
    }

    public static IWorkspace getWorkspace() {
        return ResourcesPlugin.getWorkspace();
    }

    public static ImageCache getImageCache() {
        return plugin.imageCache;
    }

    /**
     * Returns the active workbench window or <code>null</code> if none
     */
    public static IWorkbenchWindow getActiveWorkbenchWindow() {
        return getDefault().getWorkbench().getActiveWorkbenchWindow();
    }

    public static Status makeStatus(int errorLevel, String message, Throwable e) {
        return new Status(errorLevel, getPluginID(), errorLevel, message, e);
    }

    /**
     * @param errorLevel  IStatus.[OK|INFO|WARNING|ERROR]
     */
    public static void log(int errorLevel, String message, Throwable e) {
        Status s = makeStatus(errorLevel, message, e);
        getDefault().getLog().log(s);
    }

    public static void errorDialog(final String message, final Throwable t) {
        Display disp = Display.getDefault();
        disp.asyncExec(new Runnable() {
            public void run() {
                IWorkbenchWindow window = getDefault().getWorkbench().getActiveWorkbenchWindow();
                Shell shell = window == null ? null : window.getShell();
                if (shell != null) {
                    IStatus status = makeStatus(IStatus.ERROR, "Error logged from Pydev Debug: ", t);
                    ErrorDialog.openError(shell, "Its an error", message, status);
                }
            }
        });
        log(IStatus.ERROR, message, t);
    }

    /**
     * 
     * @return the script to get the variables.
     * 
     * @throws CoreException
     */
    public static File getScriptWithinPySrc(String targetExec) throws CoreException {
        return PydevPlugin.getScriptWithinPySrc(targetExec);
    }

    /**
     * 
     * @return the script to get the variables.
     * 
     * @throws CoreException
     */
    public static File getPySrcPath() throws CoreException {
        return PydevPlugin.getPySrcPath();
    }

}
