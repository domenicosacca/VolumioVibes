package com.thebeginner.volumiovibes.helper;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Build;
import android.util.Log;

public class MDNSScanner {

    private final String TAG = "MDNSScanner";

    private NsdManager.DiscoveryListener discoveryListener;
    private NsdManager.ResolveListener resolveListener;

    private DeviceHandler deviceHandler;

    private NsdManager nsdManager;

    private Vendor vendor;

    private boolean started = false;
    public static final String SERVICE_NAME = "_Volumio._tcp.";

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public MDNSScanner(Context context){
        nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        discoveryListener = getNewDiscoveryListener(SERVICE_NAME);
        resolveListener = getNewResolveListener();
    }

    public void setDeviceHandler(DeviceHandler handler){
        deviceHandler = handler;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void start() {
        if (!started) {
            started = true;
            nsdManager.discoverServices(SERVICE_NAME,
                    NsdManager.PROTOCOL_DNS_SD, discoveryListener);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void stop(){
        if(started) {
            started = false;
            nsdManager.stopServiceDiscovery(discoveryListener);
        }
    }


    private NsdManager.DiscoveryListener getNewDiscoveryListener(final String service_name) {
        return new NsdManager.DiscoveryListener() {

            @Override
            public void onDiscoveryStarted(String regType) {

            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onServiceFound(NsdServiceInfo service) {
                if (service.getServiceType().equals(service_name)) {
                    nsdManager.resolveService(service, resolveListener);
                }else{
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {

            }

            @Override
            public void onDiscoveryStopped(String serviceType) {

            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onStartDiscoveryFailed(String serviceType, int
                    errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                if(started) {
                    started = false;
                    nsdManager.stopServiceDiscovery(this);
                }
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onStopDiscoveryFailed(String serviceType, int
                    errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                if(started) {
                    started = false;
                    nsdManager.stopServiceDiscovery(this);
                }
            }
        };
    }

    private NsdManager.ResolveListener getNewResolveListener() {
        return new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo,
                                        int errorCode) {
                // Called when the resolve fails. Use the error code to debug.
                Log.e(TAG, "Resolve failed " + errorCode);
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Device device = new Device();
                device.host = serviceInfo.getHost().getHostAddress();
                device.name = serviceInfo.getServiceName();
                deviceHandler.handle(device);
            }
        };
    }

    public class Device{
        public String host;
        public String name;
    }

    public interface DeviceHandler{
        void handle(Device device);
    }
}

