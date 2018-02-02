package ru.spbau.mit.telsc.telegramManager.core;


import org.telegram.api.TLConfig;
import org.telegram.api.TLDcOption;
import org.telegram.api.auth.TLAuthorization;
import org.telegram.api.engine.storage.AbsApiState;
import org.telegram.mtproto.state.AbsMTProtoState;
import org.telegram.mtproto.state.ConnectionInfo;
import org.telegram.mtproto.state.KnownSalt;

import java.util.ArrayList;
import java.util.HashMap;

public class MemoryApiState implements AbsApiState {

    private HashMap<Integer, ConnectionInfo[]> connections = new HashMap<>();
    private HashMap<Integer, byte[]> keys = new HashMap<>();
    private HashMap<Integer, Boolean> isAuth = new HashMap<>();

    private int primaryDc = 1;

    public MemoryApiState(String hostAddr) {
        String addrComponents[] = hostAddr.split(":");
        String ipAddr = addrComponents[0];
        int port = Integer.parseInt(addrComponents[1]);
        connections.put(1, new ConnectionInfo[] {
                new ConnectionInfo(1, 0, ipAddr, port)
        });
    }

    @Override
    public synchronized int getPrimaryDc() {
        return primaryDc;
    }

    @Override
    public synchronized void setPrimaryDc(int dc) {
        primaryDc = dc;
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }

    @Override
    public synchronized boolean isAuthenticated(int dcId) {
        if (isAuth.containsKey(dcId)) {
            return isAuth.get(dcId);
        }
        return false;
    }

    @Override
    public synchronized void setAuthenticated(int dcId, boolean auth) {
        isAuth.put(dcId, auth);
    }

    @Override
    public synchronized void updateSettings(TLConfig config) {
        connections.clear();
        HashMap<Integer, ArrayList<ConnectionInfo>> tConnections = new HashMap<>();
        int id = 0;
        for (TLDcOption option : config.getDcOptions()) {
            if (!tConnections.containsKey(option.getId())) {
                tConnections.put(option.getId(), new ArrayList<>());
            }
            tConnections.get(option.getId()).add(new ConnectionInfo(id++, 0, option.getIpAddress(), option.getPort()));
        }

        for (Integer dc : tConnections.keySet()) {
            connections.put(dc, tConnections.get(dc).toArray(new ConnectionInfo[0]));
        }
    }

    @Override
    public synchronized byte[] getAuthKey(int dcId) {
        return keys.get(dcId);
    }

    @Override
    public synchronized void putAuthKey(int dcId, byte[] key) {
        keys.put(dcId, key);
    }

    @Override
    public synchronized ConnectionInfo[] getAvailableConnections(int dcId) {
        if (!connections.containsKey(dcId)) {
            return new ConnectionInfo[0];
        }

        return connections.get(dcId);
    }

    @Override
    public synchronized AbsMTProtoState getMtProtoState(final int dcId) {
        return new AbsMTProtoState() {
            private KnownSalt[] knownSalts = new KnownSalt[0];

            @Override
            public byte[] getAuthKey() {
                return MemoryApiState.this.getAuthKey(dcId);
            }

            @Override
            public ConnectionInfo[] getAvailableConnections() {
                return MemoryApiState.this.getAvailableConnections(dcId);
            }

            @Override
            public KnownSalt[] readKnownSalts() {
                return knownSalts;
            }

            @Override
            protected void writeKnownSalts(KnownSalt[] salts) {
                knownSalts = salts;
            }
        };
    }

    @Override
    public void doAuth(TLAuthorization tlAuthorization) {

    }

    @Override
    public synchronized void resetAuth() {
        isAuth.clear();
    }

    @Override
    public synchronized void reset() {
        isAuth.clear();
        keys.clear();
    }

    @Override
    public int getUserId() {
        return 0;
    }
}