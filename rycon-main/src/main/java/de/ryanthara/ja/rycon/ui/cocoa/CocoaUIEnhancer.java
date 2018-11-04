/*
 * License: GPL. Copyright 2016- (C) by Sebastian Aust (https://www.ryanthara.de/)
 *
 * This file is part of the package de.ryanthara.ja.rycon.ui.cocoa
 *
 * This package is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This package is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this package. If not, see <http://www.gnu.org/licenses/>.
 */
package de.ryanthara.ja.rycon.ui.cocoa;

import org.eclipse.swt.SWT;
import org.eclipse.swt.internal.C;
import org.eclipse.swt.internal.Callback;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.stream.IntStream;

/**
 * Provide a hook to connecting the 'Preference', 'About' and 'Quit' menu items of the Mac OS X
 * Application menu when using the SWT Cocoa bindings.
 * <p>
 * This code does not require the Cocoa SWT JAR in order to be compiled as it uses reflection to
 * access the Cocoa specific API methods. It does, however, depends on SWT Listeners, but you
 * could easily modify the code to use on JFace (for IAction) instead in order to use this class
 * in SWT only applications.
 * <p>
 * This code was influenced by the <a
 * href="http://www.simidude.com/blog/2008/macify-a-swt-application-in-a-cross-platform-way/"
 * >CarbonUIEnhancer from Agynami</a> with the implementation being modified from the <a href="http://dev.eclipse.org/viewcvs/index.cgi/org.eclipse.ui.cocoa/src/org/eclipse/ui/internal/cocoa/CocoaUIEnhancer.java"
 * >org.eclipse.ui.internal.cocoa.CocoaUIEnhancer</a>.
 * <p>
 * This class works with both the 32-bit and 64-bit versions of the SWT Cocoa bindings.
 * <p>
 * This class was released under the Eclipse Public License (<a href="http://www.eclipse.org/legal/epl-v10.html">EPL</a>).
 */
@SuppressWarnings("restriction")
public class CocoaUIEnhancer {

    private static final Logger logger = LoggerFactory.getLogger(CocoaUIEnhancer.class.getName());

    private static final long kAboutMenuItem = 0;
    private static final long kPreferencesMenuItem = 2;
    // private static final long kServicesMenuItem = 4;
    // private static final long kHideApplicationMenuItem = 6;
    private static final long kQuitMenuItem = 10;

    private static long sel_toolbarButtonClicked_;
    private static long sel_preferencesMenuItemSelected_;
    private static long sel_aboutMenuItemSelected_;
    private static Callback proc3Args;

    final private String appName;

    /**
     * Construct a new CocoaUIEnhancer.
     *
     * @param appName The name of the application. It will be used to customize the About and Quit menu
     *                items. If you do not wish to customize the About and Quit menu items, just pass
     *                null here.
     */
    public CocoaUIEnhancer(String appName) {
        this.appName = appName;
    }

    private static Object invoke(Class<?> clazz, String methodName, Object[] args) {
        return invoke(clazz, null, methodName, args);
    }

    private static Object invoke(Class<?> clazz, Object target, String methodName, Object[] args) {
        try {
            Class<?>[] signature = new Class<?>[args.length];
            IntStream.range(0, args.length).forEach(i -> {
                Class<?> thisClass = args[i].getClass();
                if (thisClass == Integer.class)
                    signature[i] = int.class;
                else if (thisClass == Long.class)
                    signature[i] = long.class;
                else if (thisClass == Byte.class)
                    signature[i] = byte.class;
                else if (thisClass == Boolean.class)
                    signature[i] = boolean.class;
                else
                    signature[i] = thisClass;
            });
            Method method = clazz.getMethod(methodName, signature);
            return method.invoke(target, args);
        } catch (Exception e) {
            logger.error("Throws illegal state exception.", e.getCause());
            throw new IllegalStateException(e);
        }
    }

    private static Object wrapPointer(long value) {
        Class<?> PTR_CLASS = C.PTR_SIZEOF == 8 ? long.class : int.class;
        if (PTR_CLASS == long.class) {
            return Long.valueOf(value);
        } else {
            return Integer.valueOf((int) value);
        }
    }

    /**
     * Hooks the given Listener to the Mac OS X application Quit menu and the {@code Listeners}
     * to the About and Preference menus.
     *
     * @param display             The Display to use.
     * @param quitListener        The listener to invoke when the Quit menu is invoked.
     * @param aboutListener       The listener to invoke when the About menu is invoked.
     * @param preferencesListener The listener to invoke when the Preference menu is invoked.
     */
    public void hookApplicationMenu(Display display, Listener quitListener,
                                    Listener aboutListener, Listener preferencesListener) {
        // This is our callbackObject whose 'actionProc' method will be called when the About or
        // Preference menuItem is invoked.

        MenuHookObject target = new MenuHookObject(aboutListener, preferencesListener);

        try {
            // Initialize the menuItems.
            initialize(target);
        } catch (Exception e) {
            logger.error("Throws illegal state exception.", e.getCause());
            throw new IllegalStateException(e);
        }

        // Connect the quit/exit menu.
        if (!display.isDisposed()) {
            display.addListener(SWT.Close, quitListener);
        }

        // Schedule disposal of callback object
        display.disposeExec(() -> invoke(proc3Args, "dispose"));
    }

    private Class<?> classForName(String classname) {
        try {
            return Class.forName(classname);
        } catch (ClassNotFoundException e) {
            logger.error("Throws illegal state exception.", e.getCause());
            throw new IllegalStateException(e);
        }
    }

    private long convertToLong(Object object) {
        if (object instanceof Integer) {
            Integer i = (Integer) object;
            return i.longValue();
        }
        if (object instanceof Long) {
            Long l = (Long) object;
            return l.longValue();
        }
        return 0;
    }

    private void initialize(Object callbackObject) throws Exception {
        Class<?> osCls = classForName("org.eclipse.swt.internal.cocoa.OS");

        // Register names in objective-c.
        if (sel_toolbarButtonClicked_ == 0) {
            // sel_toolbarButtonClicked_ = registerName( osCls, "toolbarButtonClicked:" );
            sel_preferencesMenuItemSelected_ = registerName(osCls, "preferencesMenuItemSelected:");
            sel_aboutMenuItemSelected_ = registerName(osCls, "aboutMenuItemSelected:");
        }

        // Create an SWT Callback object that will invoke the actionProc method of our internal
        // callbackObject.
        proc3Args = new Callback(callbackObject, "actionProc", 3);
        Method getAddress = Callback.class.getMethod("getAddress");
        Object object = getAddress.invoke(proc3Args, (Object[]) null);
        long proc3 = convertToLong(object);
        if (proc3 == 0) {
            SWT.error(SWT.ERROR_NO_MORE_CALLBACKS);
        }

        Class<?> nsmenuCls = classForName("org.eclipse.swt.internal.cocoa.NSMenu");
        Class<?> nsmenuitemCls = classForName("org.eclipse.swt.internal.cocoa.NSMenuItem");
        Class<?> nsstringCls = classForName("org.eclipse.swt.internal.cocoa.NSString");
        Class<?> nsapplicationCls = classForName("org.eclipse.swt.internal.cocoa.NSApplication");

        // Instead of creating a new delegate class in objective-c,
        // just use the current SWTApplicationDelegate. An instance of this
        // is a field of the Cocoa Display object and is already the target
        // for the menuItems. So just get this class and add the new methods
        // to it.
        object = invoke(osCls, "objc_lookUpClass", new Object[]{"SWTApplicationDelegate"});
        long cls = convertToLong(object);

        // Add the action callbacks for Preference and About menu items.
        invoke(osCls, "class_addMethod", new Object[]{
                wrapPointer(cls),
                wrapPointer(sel_preferencesMenuItemSelected_),
                wrapPointer(proc3),
                "@:@"});
        invoke(osCls, "class_addMethod", new Object[]{
                wrapPointer(cls),
                wrapPointer(sel_aboutMenuItemSelected_),
                wrapPointer(proc3),
                "@:@"});

        // Get the Mac OS X Application menu.
        Object sharedApplication = invoke(nsapplicationCls, "sharedApplication");
        Object mainMenu = invoke(sharedApplication, "mainMenu");
        Object mainMenuItem = invoke(nsmenuCls, mainMenu, "itemAtIndex", new Object[]{wrapPointer(0)});
        Object appMenu = invoke(mainMenuItem, "submenu");

        // Create the About <application-name> menu command
        Object aboutMenuItem =
                invoke(nsmenuCls, appMenu, "itemAtIndex", new Object[]{wrapPointer(kAboutMenuItem)});
        if (appName != null) {
            Object nsStr = invoke(nsstringCls, "stringWith", new Object[]{"About " + appName});
            invoke(nsmenuitemCls, aboutMenuItem, "setTitle", new Object[]{nsStr});
        }
        // Rename the quit action.
        if (appName != null) {
            Object quitMenuItem =
                    invoke(nsmenuCls, appMenu, "itemAtIndex", new Object[]{wrapPointer(kQuitMenuItem)});
            Object nsStr = invoke(nsstringCls, "stringWith", new Object[]{"Quit " + appName});
            invoke(nsmenuitemCls, quitMenuItem, "setTitle", new Object[]{nsStr});
        }

        // Enable the Preference menuItem.
        Object prefMenuItem =
                invoke(nsmenuCls, appMenu, "itemAtIndex", new Object[]{wrapPointer(kPreferencesMenuItem)});
        invoke(nsmenuitemCls, prefMenuItem, "setEnabled", new Object[]{true});

        // Set the action to execute when the About or Preference menuItem is invoked.
        //
        // We don't need to set the target here as the current target is the SWTApplicationDelegate
        // and we have registered the new selectors on it. So just set the new action to invoke the
        // selector.
        invoke(nsmenuitemCls, prefMenuItem, "setAction",
                new Object[]{wrapPointer(sel_preferencesMenuItemSelected_)});
        invoke(nsmenuitemCls, aboutMenuItem, "setAction",
                new Object[]{wrapPointer(sel_aboutMenuItemSelected_)});
    }

    private Object invoke(Class<?> cls, String methodName) {
        return invoke(cls, methodName, (Class<?>[]) null, (Object[]) null);
    }

    private Object invoke(Class<?> cls, String methodName, Class<?>[] paramTypes, Object... arguments) {
        try {
            Method m = cls.getDeclaredMethod(methodName, paramTypes);
            return m.invoke(null, arguments);
        } catch (Exception e) {
            logger.error("Throws illegal state exception.", e.getCause());
            throw new IllegalStateException(e);
        }
    }

    private Object invoke(Object obj, String methodName) {
        return invoke(obj, methodName, null, (Object[]) null);
    }

    private Object invoke(Object obj, String methodName, Class<?>[] paramTypes, Object... arguments) {
        try {
            Method m = obj.getClass().getDeclaredMethod(methodName, paramTypes);
            return m.invoke(obj, arguments);
        } catch (Exception e) {
            logger.error("Throws illegal state exception.", e.getCause());
            throw new IllegalStateException(e);
        }
    }

    private long registerName(Class<?> osCls, String name) throws IllegalArgumentException, SecurityException {
        Object object = invoke(osCls, "sel_registerName", new Object[]{name});
        return convertToLong(object);
    }

    /**
     * Class invoked via the Callback object to run the about and preferences actions by Listeners.
     * <p>
     * If this will be used in a JFace application, change the {@link org.eclipse.swt.widgets.Listener}s
     * to {@code org.eclipse.jface.action.IAction}s.
     */
    private static class MenuHookObject {
        final Listener about;
        final Listener pref;

        /**
         * Constructs a new {@code MenuHookObject}.
         *
         * @param about reference to the {@code about} listener
         * @param pref  reference to the {@code pref} listener
         */
        MenuHookObject(Listener about, Listener pref) {
            this.about = about;
            this.pref = pref;
        }

        /**
         * Will be called on 32bit SWT.
         */
        @SuppressWarnings("unused")
        public int actionProc(int id, int sel, int arg0) {
            return (int) actionProc((long) id, (long) sel, (long) arg0);
        }

        /**
         * Will be called on 64bit SWT.
         */
        long actionProc(long id, long sel, long arg0) {
            if (sel == sel_aboutMenuItemSelected_) {
                // SWT usage
                about.handleEvent(null);

                // JFace usage
                // about.run();
            } else if (sel == sel_preferencesMenuItemSelected_) {
                // SWT usage
                pref.handleEvent(null);

                // JFace usage
                // pref.run();
            } else {
                // Unknown selection!
            }
            // unused return value
            return 99;
        }
    }

}
