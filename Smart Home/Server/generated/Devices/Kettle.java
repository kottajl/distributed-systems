//
// Copyright (c) ZeroC, Inc. All rights reserved.
//
//
// Ice version 3.7.10
//
// <auto-generated>
//
// Generated from file `devices.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package Devices;

public class Kettle extends com.zeroc.Ice.Value
{
    public Kettle()
    {
    }

    public Kettle(int capacity, int waterContent, int tempereture)
    {
        this.capacity = capacity;
        this.waterContent = waterContent;
        this.tempereture = tempereture;
    }

    public int capacity;

    public int waterContent;

    public int tempereture;

    public Kettle clone()
    {
        return (Kettle)super.clone();
    }

    public static String ice_staticId()
    {
        return "::Devices::Kettle";
    }

    @Override
    public String ice_id()
    {
        return ice_staticId();
    }

    /** @hidden */
    public static final long serialVersionUID = -1378905354999753254L;

    /** @hidden */
    @Override
    protected void _iceWriteImpl(com.zeroc.Ice.OutputStream ostr_)
    {
        ostr_.startSlice(ice_staticId(), -1, true);
        ostr_.writeInt(capacity);
        ostr_.writeInt(waterContent);
        ostr_.writeInt(tempereture);
        ostr_.endSlice();
    }

    /** @hidden */
    @Override
    protected void _iceReadImpl(com.zeroc.Ice.InputStream istr_)
    {
        istr_.startSlice();
        capacity = istr_.readInt();
        waterContent = istr_.readInt();
        tempereture = istr_.readInt();
        istr_.endSlice();
    }
}