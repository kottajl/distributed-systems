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

public class Fridge extends com.zeroc.Ice.Value
{
    public Fridge()
    {
    }

    public Fridge(java.util.Map<java.lang.String, java.lang.Integer> content, int temperature)
    {
        this.content = content;
        this.temperature = temperature;
    }

    public java.util.Map<java.lang.String, java.lang.Integer> content;

    public int temperature;

    public Fridge clone()
    {
        return (Fridge)super.clone();
    }

    public static String ice_staticId()
    {
        return "::Devices::Fridge";
    }

    @Override
    public String ice_id()
    {
        return ice_staticId();
    }

    /** @hidden */
    public static final long serialVersionUID = 387032471307533764L;

    /** @hidden */
    @Override
    protected void _iceWriteImpl(com.zeroc.Ice.OutputStream ostr_)
    {
        ostr_.startSlice(ice_staticId(), -1, true);
        ItemCounterHelper.write(ostr_, content);
        ostr_.writeInt(temperature);
        ostr_.endSlice();
    }

    /** @hidden */
    @Override
    protected void _iceReadImpl(com.zeroc.Ice.InputStream istr_)
    {
        istr_.startSlice();
        content = ItemCounterHelper.read(istr_);
        temperature = istr_.readInt();
        istr_.endSlice();
    }
}
