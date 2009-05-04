/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * Sun designates this particular file as subject to the "Classpath" 
 * exception as provided by Sun in the License file that accompanied 
 * this code.
 */
package org.jdesktop.wonderland.modules.appbase.client.view;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import java.awt.Point;
import java.awt.Dimension;
import org.jdesktop.mtgame.EntityComponent;
import org.jdesktop.wonderland.client.input.EventListener;
import org.jdesktop.wonderland.client.jme.input.MouseEvent3D;
import org.jdesktop.wonderland.modules.appbase.client.Window2D;
import org.jdesktop.wonderland.common.ExperimentalAPI;

@ExperimentalAPI
public interface View2D {

    /** The type of the view. */
    public enum Type { UNKNOWN, PRIMARY, SECONDARY, POPUP };

    /** A view can be moved above or below another in the stack. */
    public enum RestackOp { ABOVE, BELOW };

    /** Clean up resources. */
    public void cleanup ();

    /** Returns the name of the view. */
    public String getName ();

    /** Returns the displayer in which this view is displayed. */
    public View2DDisplayer getDisplayer ();

    /** Returns the window which the view displays. */
    public Window2D getWindow ();

    /** Specify the type of the view. Update afterward. */
    public void setType (Type type);

    /** Specify the type of the view. Update if specified. */
    public void setType (Type type, boolean update);

    /** Returns the type of the view. */
    public Type getType ();

    /** Set the parent view of this view. Update afterward. */
    public void setParent (View2D parent);

    /** Set the parent view of this view. Update if specified. */
    public void setParent (View2D parent, boolean update);

    /** Returns the parent of this view. */
    public View2D getParent ();

    /** Set whether the app wants the view to be visible. Update afterward. */
    public void setVisibleApp (boolean visible);

    /** Set whether the app wants the view to be visible. Update if specified. */
    public void setVisibleApp (boolean visible, boolean update);

    /** Return whether the app wants the to be visible. */
    public boolean isVisibleApp ();

    /** Set whether the user wants the view to be visible. Update afterward. */
    public void setVisibleUser (boolean visible);

    /** Set whether the user wants the view to be visible. Update if specified. */
    public void setVisibleUser (boolean visible, boolean update);

    /** Return whether the user wants the to be visible. */
    public boolean isVisibleUser ();

    /** 
     * Returns whether the view is actually visible. To be actually visible, a view needs to 
     * have both visibleApp and visibleUser set to true, and all ancestor views need to be 
     * actually visible.
     */
    public boolean isActuallyVisible ();

    /** Specify whether the view should be decorated by a frame. Update afterward. */
    public void setDecorated (boolean decorated);

    /** Specify whether the view should be decorated by a frame. Update if specified. */
    public void setDecorated (boolean decorated, boolean update);

    /** Return whether the view should be decorated by a frame. */
    public boolean isDecorated ();

    /** Specify the frame title (used only when the view is decorated). Update afterward. */
    public void setTitle (String title);

    /** Specify the frame title (used only when the view is decorated). Update if specified. */
    public void setTitle (String title, boolean update);

    /** Returns the frame title. */
    public String getTitle ();

    /** 
     * Specify the portion of the window which is displayed by this view. 
     * Update afterward.
       TODO: notyet: public void setWindowAperture (Rectangle aperture);
     */

    /** 
     * Specify the portion of the window which is displayed by this view.
     * Update if specified.
       TODO: notyet: public void setWindowAperture (Rectangle aperture, boolean update);
     */

    /** 
     * Return the portion of the window which is displayed by this view.
       TODO: notyet: public Rectangle getWindowAperture ();
     */

    /** 
     * Specify a geometry node. If <code>geometryNode</code> is null the default geometry node is used.
     * Update afterward.
     */
    public void setGeometryNode (GeometryNode geometryNode);

    /** 
     * Specify a geometry node. If <code>geometryNode</code> is null the default geometry node is used.
     * Update if specified.
     */
    public void setGeometryNode (GeometryNode geometryNode, boolean update);

    /** Returns the geometry node used by this view. */
    public GeometryNode getGeometryNode ();

    /** A size change which comes from the app. Update afterward. */
    public void setSizeApp (Dimension size);

    /** A size change which comes from the app. Update if specified. */
    public void setSizeApp (Dimension size, boolean update);

    /** Returns the app-specified size (in pixels). */
    public Dimension getSizeApp ();

    /** A size change which comes from the user. */
    // TODO: notyet: public void setSizeUser(int width, int height);
    /* TODO: user size getters */

    /** Returns the current width of the view in the local coordinate system of the displayer. */
    public float getDisplayerLocalWidth ();

    /** Returns the current height of the view in the local coordinate system of the displayer. */
    public float getDisplayerLocalHeight ();

    /** 
     * A window close which comes from the user. Close the window of this view.
     */
    public void windowCloseUser ();

    /**  
     * Tells the view that the window's stack position may have changed.
     */
    public void stackChanged (boolean update);

    /** Moves the window of this view to the top of its app's window stack. */
    public void windowRestackToTop ();

    /** Moves the window of this view to the bottom of its app's window stack. */
    public void windowRestackToBottom ();

    /**  
     * Moves this window so that it is above the window of the given sibling view in the app's window stack.
     * If sibling is null, the window is moved to the top of the stack.
     */
    public void windowRestackAbove (View2D sibling);

    /**  
     * Moves this window so that it is below the window of the given sibling view in the app's window stack.
     * If sibling is null, the window is moved to the bottom of the stack.
     */
    public void windowRestackBelow (View2D sibling);

    /** 
     * Specify the size of the displayed pixels of this view.
     * Update afterward. This defaults to the initial pixel scale of the window.
     */
    public void setPixelScale (Vector2f pixelScale);

    /** 
     * Specify the size of the displayed pixels of this view.
     * Update if specified. Defaults to the initial pixel scale of the window.
     */
    public void setPixelScale (Vector2f pixelScale, boolean update);

    /** Return the pixel scale of this view. */
    public Vector2f getPixelScale ();

    /** 
     * Specify the pixel offset translation from the top left corner of the parent (comes from the app). 
     * Update afterward.
     */
    public void setOffset(Point offset);

    /** 
     * Specify the pixel offset translation from the top left corner of the parent (comes from the app).
     * Update if specified.
     */
    public void setOffset(Point offset, boolean update);

    /** Returns the offset. */
    public Point getOffset ();

    /** Specify the user-specified translation of this view. Update afterward. */
    public void setTranslationUser (Vector3f translation);

    /** Specify the user-specified translation of this view. Update if specified. */
    public void setTranslationUser (Vector3f translation, boolean update);

    /** Returns the user translation of this view. */
    public Vector3f getTranslationUser ();

    /** Apply all pending updates. */
    public void update ();

    /** 
     * Converts the given 3D mouse event into a 2D event and forwards it along to the view's controlArb.
     *
     * @param window The window this view displays.
     * @param me3d The 3D mouse event to deliver.
     */
    public void deliverEvent(Window2D window, MouseEvent3D me3d);

    /**
     * Transform the given 3D point in local coordinates into the corresponding point
     * in the pixel space of the view's image. The given point must be in the plane of the view.
     * @param point The point to transform.
     * @param clamp If true return the last position if the argument point is null or the resulting
     * position is outside of the geometry's rectangle. Otherwise, return null if these conditions hold.
     * @return the 2D position of the pixel space the view's image.
     */
    public Point calcPositionInPixelCoordinates(Vector3f point, boolean clamp);

    /**
     * Given a point in the pixel space of the Wonderland canvas calculates 
     * the texel coordinates of the point on the geometry where a
     * ray starting from the current eye position intersects the geometry.
     */
    public Point calcIntersectionPixelOfEyeRay(int x, int y);

    /**
     * Add an event listener to this view.
     * @param listener The listener to add.
     */
    public void addEventListener(EventListener listener);

    /**
     * Remove an event listener from this view.
     * @param listener The listener to remove.
     */
    public void removeEventListener(EventListener listener);

    /**
     * Add an entity component to this view.
     */
    public void addEntityComponent(Class clazz, EntityComponent comp);

    /**
     * Remove an entity component from this view.
     */
    public void removeEntityComponent(Class clazz);
}