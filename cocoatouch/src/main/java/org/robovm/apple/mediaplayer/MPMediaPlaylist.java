/*
 * Copyright (C) 2014 Trillian Mobile AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.robovm.apple.mediaplayer;

/*<imports>*/
import java.io.*;
import java.nio.*;
import java.util.*;
import org.robovm.objc.*;
import org.robovm.objc.annotation.*;
import org.robovm.objc.block.*;
import org.robovm.rt.*;
import org.robovm.rt.bro.*;
import org.robovm.rt.bro.annotation.*;
import org.robovm.rt.bro.ptr.*;
import org.robovm.apple.foundation.*;
import org.robovm.apple.uikit.*;
import org.robovm.apple.coregraphics.*;
/*</imports>*/

/*<javadoc>*/
/**
 * @since Available in iOS 3.0 and later.
 */
/*</javadoc>*/
/*<annotations>*/@Library("MediaPlayer") @NativeClass/*</annotations>*/
/*<visibility>*/public/*</visibility>*/ class /*<name>*/MPMediaPlaylist/*</name>*/ 
    extends /*<extends>*/MPMediaItemCollection/*</extends>*/ 
    /*<implements>*//*</implements>*/ {

    /*<ptr>*/public static class MPMediaPlaylistPtr extends Ptr<MPMediaPlaylist, MPMediaPlaylistPtr> {}/*</ptr>*/
    /*<bind>*/static { ObjCRuntime.bind(MPMediaPlaylist.class); }/*</bind>*/
    /*<constants>*//*</constants>*/
    /*<constructors>*/
    public MPMediaPlaylist() {}
    protected MPMediaPlaylist(SkipInit skipInit) { super(skipInit); }
    /*</constructors>*/
    /*<properties>*/
    
    /*</properties>*/
    /*<members>*//*</members>*/
    /*<methods>*/
    @GlobalValue(symbol="MPMediaPlaylistPropertyPersistentID", optional=true)
    public static native NSString PropertyPersistentID();
    @GlobalValue(symbol="MPMediaPlaylistPropertyName", optional=true)
    public static native NSString PropertyName();
    @GlobalValue(symbol="MPMediaPlaylistPropertyPlaylistAttributes", optional=true)
    public static native NSString PropertyPlaylistAttributes();
    @GlobalValue(symbol="MPMediaPlaylistPropertySeedItems", optional=true)
    public static native NSString PropertySeedItems();
    
    
    /*</methods>*/
}