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

public interface ICameraPTZPrx extends ICameraPrx
{
    default void changeZoom(Zoom zoom)
    {
        changeZoom(zoom, com.zeroc.Ice.ObjectPrx.noExplicitContext);
    }

    default void changeZoom(Zoom zoom, java.util.Map<String, String> context)
    {
        _iceI_changeZoomAsync(zoom, context, true).waitForResponse();
    }

    default java.util.concurrent.CompletableFuture<Void> changeZoomAsync(Zoom zoom)
    {
        return _iceI_changeZoomAsync(zoom, com.zeroc.Ice.ObjectPrx.noExplicitContext, false);
    }

    default java.util.concurrent.CompletableFuture<Void> changeZoomAsync(Zoom zoom, java.util.Map<String, String> context)
    {
        return _iceI_changeZoomAsync(zoom, context, false);
    }

    /**
     * @hidden
     * @param iceP_zoom -
     * @param context -
     * @param sync -
     * @return -
     **/
    default com.zeroc.IceInternal.OutgoingAsync<Void> _iceI_changeZoomAsync(Zoom iceP_zoom, java.util.Map<String, String> context, boolean sync)
    {
        com.zeroc.IceInternal.OutgoingAsync<Void> f = new com.zeroc.IceInternal.OutgoingAsync<>(this, "changeZoom", com.zeroc.Ice.OperationMode.Idempotent, sync, null);
        f.invoke(false, context, null, ostr -> {
                     Zoom.ice_write(ostr, iceP_zoom);
                 }, null);
        return f;
    }

    default void moveLens(int transX, int transY)
        throws InvalidArgumentError
    {
        moveLens(transX, transY, com.zeroc.Ice.ObjectPrx.noExplicitContext);
    }

    default void moveLens(int transX, int transY, java.util.Map<String, String> context)
        throws InvalidArgumentError
    {
        try
        {
            _iceI_moveLensAsync(transX, transY, context, true).waitForResponseOrUserEx();
        }
        catch(InvalidArgumentError ex)
        {
            throw ex;
        }
        catch(com.zeroc.Ice.UserException ex)
        {
            throw new com.zeroc.Ice.UnknownUserException(ex.ice_id(), ex);
        }
    }

    default java.util.concurrent.CompletableFuture<Void> moveLensAsync(int transX, int transY)
    {
        return _iceI_moveLensAsync(transX, transY, com.zeroc.Ice.ObjectPrx.noExplicitContext, false);
    }

    default java.util.concurrent.CompletableFuture<Void> moveLensAsync(int transX, int transY, java.util.Map<String, String> context)
    {
        return _iceI_moveLensAsync(transX, transY, context, false);
    }

    /**
     * @hidden
     * @param iceP_transX -
     * @param iceP_transY -
     * @param context -
     * @param sync -
     * @return -
     **/
    default com.zeroc.IceInternal.OutgoingAsync<Void> _iceI_moveLensAsync(int iceP_transX, int iceP_transY, java.util.Map<String, String> context, boolean sync)
    {
        com.zeroc.IceInternal.OutgoingAsync<Void> f = new com.zeroc.IceInternal.OutgoingAsync<>(this, "moveLens", com.zeroc.Ice.OperationMode.Idempotent, sync, _iceE_moveLens);
        f.invoke(true, context, null, ostr -> {
                     ostr.writeInt(iceP_transX);
                     ostr.writeInt(iceP_transY);
                 }, null);
        return f;
    }

    /** @hidden */
    static final Class<?>[] _iceE_moveLens =
    {
        InvalidArgumentError.class
    };

    /**
     * Contacts the remote server to verify that the object implements this type.
     * Raises a local exception if a communication error occurs.
     * @param obj The untyped proxy.
     * @return A proxy for this type, or null if the object does not support this type.
     **/
    static ICameraPTZPrx checkedCast(com.zeroc.Ice.ObjectPrx obj)
    {
        return com.zeroc.Ice.ObjectPrx._checkedCast(obj, ice_staticId(), ICameraPTZPrx.class, _ICameraPTZPrxI.class);
    }

    /**
     * Contacts the remote server to verify that the object implements this type.
     * Raises a local exception if a communication error occurs.
     * @param obj The untyped proxy.
     * @param context The Context map to send with the invocation.
     * @return A proxy for this type, or null if the object does not support this type.
     **/
    static ICameraPTZPrx checkedCast(com.zeroc.Ice.ObjectPrx obj, java.util.Map<String, String> context)
    {
        return com.zeroc.Ice.ObjectPrx._checkedCast(obj, context, ice_staticId(), ICameraPTZPrx.class, _ICameraPTZPrxI.class);
    }

    /**
     * Contacts the remote server to verify that a facet of the object implements this type.
     * Raises a local exception if a communication error occurs.
     * @param obj The untyped proxy.
     * @param facet The name of the desired facet.
     * @return A proxy for this type, or null if the object does not support this type.
     **/
    static ICameraPTZPrx checkedCast(com.zeroc.Ice.ObjectPrx obj, String facet)
    {
        return com.zeroc.Ice.ObjectPrx._checkedCast(obj, facet, ice_staticId(), ICameraPTZPrx.class, _ICameraPTZPrxI.class);
    }

    /**
     * Contacts the remote server to verify that a facet of the object implements this type.
     * Raises a local exception if a communication error occurs.
     * @param obj The untyped proxy.
     * @param facet The name of the desired facet.
     * @param context The Context map to send with the invocation.
     * @return A proxy for this type, or null if the object does not support this type.
     **/
    static ICameraPTZPrx checkedCast(com.zeroc.Ice.ObjectPrx obj, String facet, java.util.Map<String, String> context)
    {
        return com.zeroc.Ice.ObjectPrx._checkedCast(obj, facet, context, ice_staticId(), ICameraPTZPrx.class, _ICameraPTZPrxI.class);
    }

    /**
     * Downcasts the given proxy to this type without contacting the remote server.
     * @param obj The untyped proxy.
     * @return A proxy for this type.
     **/
    static ICameraPTZPrx uncheckedCast(com.zeroc.Ice.ObjectPrx obj)
    {
        return com.zeroc.Ice.ObjectPrx._uncheckedCast(obj, ICameraPTZPrx.class, _ICameraPTZPrxI.class);
    }

    /**
     * Downcasts the given proxy to this type without contacting the remote server.
     * @param obj The untyped proxy.
     * @param facet The name of the desired facet.
     * @return A proxy for this type.
     **/
    static ICameraPTZPrx uncheckedCast(com.zeroc.Ice.ObjectPrx obj, String facet)
    {
        return com.zeroc.Ice.ObjectPrx._uncheckedCast(obj, facet, ICameraPTZPrx.class, _ICameraPTZPrxI.class);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for the per-proxy context.
     * @param newContext The context for the new proxy.
     * @return A proxy with the specified per-proxy context.
     **/
    @Override
    default ICameraPTZPrx ice_context(java.util.Map<String, String> newContext)
    {
        return (ICameraPTZPrx)_ice_context(newContext);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for the adapter ID.
     * @param newAdapterId The adapter ID for the new proxy.
     * @return A proxy with the specified adapter ID.
     **/
    @Override
    default ICameraPTZPrx ice_adapterId(String newAdapterId)
    {
        return (ICameraPTZPrx)_ice_adapterId(newAdapterId);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for the endpoints.
     * @param newEndpoints The endpoints for the new proxy.
     * @return A proxy with the specified endpoints.
     **/
    @Override
    default ICameraPTZPrx ice_endpoints(com.zeroc.Ice.Endpoint[] newEndpoints)
    {
        return (ICameraPTZPrx)_ice_endpoints(newEndpoints);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for the locator cache timeout.
     * @param newTimeout The new locator cache timeout (in seconds).
     * @return A proxy with the specified locator cache timeout.
     **/
    @Override
    default ICameraPTZPrx ice_locatorCacheTimeout(int newTimeout)
    {
        return (ICameraPTZPrx)_ice_locatorCacheTimeout(newTimeout);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for the invocation timeout.
     * @param newTimeout The new invocation timeout (in seconds).
     * @return A proxy with the specified invocation timeout.
     **/
    @Override
    default ICameraPTZPrx ice_invocationTimeout(int newTimeout)
    {
        return (ICameraPTZPrx)_ice_invocationTimeout(newTimeout);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for connection caching.
     * @param newCache <code>true</code> if the new proxy should cache connections; <code>false</code> otherwise.
     * @return A proxy with the specified caching policy.
     **/
    @Override
    default ICameraPTZPrx ice_connectionCached(boolean newCache)
    {
        return (ICameraPTZPrx)_ice_connectionCached(newCache);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for the endpoint selection policy.
     * @param newType The new endpoint selection policy.
     * @return A proxy with the specified endpoint selection policy.
     **/
    @Override
    default ICameraPTZPrx ice_endpointSelection(com.zeroc.Ice.EndpointSelectionType newType)
    {
        return (ICameraPTZPrx)_ice_endpointSelection(newType);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for how it selects endpoints.
     * @param b If <code>b</code> is <code>true</code>, only endpoints that use a secure transport are
     * used by the new proxy. If <code>b</code> is false, the returned proxy uses both secure and
     * insecure endpoints.
     * @return A proxy with the specified selection policy.
     **/
    @Override
    default ICameraPTZPrx ice_secure(boolean b)
    {
        return (ICameraPTZPrx)_ice_secure(b);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for the encoding used to marshal parameters.
     * @param e The encoding version to use to marshal request parameters.
     * @return A proxy with the specified encoding version.
     **/
    @Override
    default ICameraPTZPrx ice_encodingVersion(com.zeroc.Ice.EncodingVersion e)
    {
        return (ICameraPTZPrx)_ice_encodingVersion(e);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for its endpoint selection policy.
     * @param b If <code>b</code> is <code>true</code>, the new proxy will use secure endpoints for invocations
     * and only use insecure endpoints if an invocation cannot be made via secure endpoints. If <code>b</code> is
     * <code>false</code>, the proxy prefers insecure endpoints to secure ones.
     * @return A proxy with the specified selection policy.
     **/
    @Override
    default ICameraPTZPrx ice_preferSecure(boolean b)
    {
        return (ICameraPTZPrx)_ice_preferSecure(b);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for the router.
     * @param router The router for the new proxy.
     * @return A proxy with the specified router.
     **/
    @Override
    default ICameraPTZPrx ice_router(com.zeroc.Ice.RouterPrx router)
    {
        return (ICameraPTZPrx)_ice_router(router);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for the locator.
     * @param locator The locator for the new proxy.
     * @return A proxy with the specified locator.
     **/
    @Override
    default ICameraPTZPrx ice_locator(com.zeroc.Ice.LocatorPrx locator)
    {
        return (ICameraPTZPrx)_ice_locator(locator);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for collocation optimization.
     * @param b <code>true</code> if the new proxy enables collocation optimization; <code>false</code> otherwise.
     * @return A proxy with the specified collocation optimization.
     **/
    @Override
    default ICameraPTZPrx ice_collocationOptimized(boolean b)
    {
        return (ICameraPTZPrx)_ice_collocationOptimized(b);
    }

    /**
     * Returns a proxy that is identical to this proxy, but uses twoway invocations.
     * @return A proxy that uses twoway invocations.
     **/
    @Override
    default ICameraPTZPrx ice_twoway()
    {
        return (ICameraPTZPrx)_ice_twoway();
    }

    /**
     * Returns a proxy that is identical to this proxy, but uses oneway invocations.
     * @return A proxy that uses oneway invocations.
     **/
    @Override
    default ICameraPTZPrx ice_oneway()
    {
        return (ICameraPTZPrx)_ice_oneway();
    }

    /**
     * Returns a proxy that is identical to this proxy, but uses batch oneway invocations.
     * @return A proxy that uses batch oneway invocations.
     **/
    @Override
    default ICameraPTZPrx ice_batchOneway()
    {
        return (ICameraPTZPrx)_ice_batchOneway();
    }

    /**
     * Returns a proxy that is identical to this proxy, but uses datagram invocations.
     * @return A proxy that uses datagram invocations.
     **/
    @Override
    default ICameraPTZPrx ice_datagram()
    {
        return (ICameraPTZPrx)_ice_datagram();
    }

    /**
     * Returns a proxy that is identical to this proxy, but uses batch datagram invocations.
     * @return A proxy that uses batch datagram invocations.
     **/
    @Override
    default ICameraPTZPrx ice_batchDatagram()
    {
        return (ICameraPTZPrx)_ice_batchDatagram();
    }

    /**
     * Returns a proxy that is identical to this proxy, except for compression.
     * @param co <code>true</code> enables compression for the new proxy; <code>false</code> disables compression.
     * @return A proxy with the specified compression setting.
     **/
    @Override
    default ICameraPTZPrx ice_compress(boolean co)
    {
        return (ICameraPTZPrx)_ice_compress(co);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for its connection timeout setting.
     * @param t The connection timeout for the proxy in milliseconds.
     * @return A proxy with the specified timeout.
     **/
    @Override
    default ICameraPTZPrx ice_timeout(int t)
    {
        return (ICameraPTZPrx)_ice_timeout(t);
    }

    /**
     * Returns a proxy that is identical to this proxy, except for its connection ID.
     * @param connectionId The connection ID for the new proxy. An empty string removes the connection ID.
     * @return A proxy with the specified connection ID.
     **/
    @Override
    default ICameraPTZPrx ice_connectionId(String connectionId)
    {
        return (ICameraPTZPrx)_ice_connectionId(connectionId);
    }

    /**
     * Returns a proxy that is identical to this proxy, except it's a fixed proxy bound
     * the given connection.@param connection The fixed proxy connection.
     * @return A fixed proxy bound to the given connection.
     **/
    @Override
    default ICameraPTZPrx ice_fixed(com.zeroc.Ice.Connection connection)
    {
        return (ICameraPTZPrx)_ice_fixed(connection);
    }

    static String ice_staticId()
    {
        return "::Devices::ICameraPTZ";
    }
}