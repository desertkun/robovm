/*
 * Copyright (C) 2013-2015 RoboVM AB
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
package org.robovm.apple.passkit;

/*<imports>*/
import java.io.*;
import java.nio.*;
import java.util.*;
import org.robovm.objc.*;
import org.robovm.objc.annotation.*;
import org.robovm.objc.block.*;
import org.robovm.rt.*;
import org.robovm.rt.annotation.*;
import org.robovm.rt.bro.*;
import org.robovm.rt.bro.annotation.*;
import org.robovm.rt.bro.ptr.*;
import org.robovm.apple.foundation.*;
import org.robovm.apple.uikit.*;
import org.robovm.apple.addressbook.*;
import org.robovm.apple.contacts.*;
/*</imports>*/

/*<javadoc>*/
/**
 * @since Available in iOS 9.0 and later.
 */
/*</javadoc>*/
/*<annotations>*/@Marshaler(Bits.AsMachineSizedIntMarshaler.class)/*</annotations>*/
public final class /*<name>*/PKPaymentMethodType/*</name>*/ extends Bits</*<name>*/PKPaymentMethodType/*</name>*/> {
    /*<values>*/
    public static final PKPaymentMethodType Unknown = new PKPaymentMethodType(0L);
    public static final PKPaymentMethodType Debit = new PKPaymentMethodType(1L);
    public static final PKPaymentMethodType Credit = new PKPaymentMethodType(2L);
    public static final PKPaymentMethodType Prepaid = new PKPaymentMethodType(3L);
    public static final PKPaymentMethodType Store = new PKPaymentMethodType(4L);
    /*</values>*/

    /*<bind>*/
    /*</bind>*/
    /*<constants>*//*</constants>*/
    /*<methods>*//*</methods>*/

    private static final /*<name>*/PKPaymentMethodType/*</name>*/[] values = _values(/*<name>*/PKPaymentMethodType/*</name>*/.class);

    public /*<name>*/PKPaymentMethodType/*</name>*/(long value) { super(value); }
    private /*<name>*/PKPaymentMethodType/*</name>*/(long value, long mask) { super(value, mask); }
    protected /*<name>*/PKPaymentMethodType/*</name>*/ wrap(long value, long mask) {
        return new /*<name>*/PKPaymentMethodType/*</name>*/(value, mask);
    }
    protected /*<name>*/PKPaymentMethodType/*</name>*/[] _values() {
        return values;
    }
    public static /*<name>*/PKPaymentMethodType/*</name>*/[] values() {
        return values.clone();
    }
}
