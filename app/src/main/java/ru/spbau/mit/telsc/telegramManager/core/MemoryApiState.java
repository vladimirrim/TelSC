package ru.spbau.mit.telsc.telegramManager.core;


import android.util.SparseArray;
import android.util.SparseBooleanArray;

import org.telegram.api.TLConfig;
import org.telegram.api.TLDcOption;
import org.telegram.api.auth.TLAuthorization;
import org.telegram.api.engine.storage.AbsApiState;
import org.telegram.mtproto.state.AbsMTProtoState;
import org.telegram.mtproto.state.ConnectionInfo;
import org.telegram.mtproto.state.KnownSalt;

import java.util.ArrayList;

public class MemoryApiState implements AbsApiState {

    private SparseArray<ConnectionInfo[]> connections = new SparseArray<>();
    private SparseArray< byte[]> keys = new SparseArray<>();
    private SparseBooleanArray isAuth = new SparseBooleanArray();

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
        return isAuth.get(dcId) && isAuth.get(dcId);
    }

    @Override
    public synchronized void setAuthenticated(int dcId, boolean auth) {
        isAuth.put(dcId, auth);
    }

    @Override
    public synchronized void updateSettings(TLConfig config) {
        connections.clear();
        SparseArray<ArrayList<ConnectionInfo>> tConnections = new SparseArray<>();
        int id = 0;
        for (TLDcOption option : config.getDcOptions()) {
            if (tConnections.get(option.getId()) == null) {
                tConnections.put(option.getId(), new ArrayList<>());
            }
            tConnections.get(option.getId()).add(new ConnectionInfo(id++, 0, option.getIpAddress(), option.getPort()));
        }

        for (int dc =0;dc < tConnections.size();dc++) {
            connections.put(dc, tConnections.valueAt(dc).toArray(new ConnectionInfo[0]));
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
        if (connections.get(dcId) == null) {
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