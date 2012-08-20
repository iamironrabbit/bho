/**
 * 
 */
package org.ironrabbit.tbchat;

import org.ironrabbit.tbchat.app.im.engine.ChatSession;

import net.java.otr4j.session.SessionID;
import android.os.RemoteException;
import org.ironrabbit.tbchat.IOtrKeyManager;
import org.ironrabbit.tbchat.IOtrKeyManager.Stub;

/** @author n8fr8 */
public class OtrKeyManagerAdapter extends IOtrKeyManager.Stub {

    private OtrAndroidKeyManagerImpl _keyManager;

    private SessionID _sessionId;

    private String _accountId;

    public OtrKeyManagerAdapter(OtrAndroidKeyManagerImpl keyManager, SessionID sessionId,
            String accountId) {

        _keyManager = keyManager;

        _sessionId = sessionId;

        _accountId = accountId;
    }

    public OtrKeyManagerAdapter(OtrAndroidKeyManagerImpl keyManager) {

        _keyManager = keyManager;

    }

    public void setSessionId(SessionID _sessionId) {
        this._sessionId = _sessionId;
    }

    public void setAccountId(String _accountId) {
        this._accountId = _accountId;
    }

    /* (non-Javadoc)
     * @see org.ironrabbit.tbchat.IOtrKeyManager#verifyKey(java.lang.String)
     */
    @Override
    public void verifyKey(String address) throws RemoteException {

        _keyManager.verifyUser(address);

    }

    /* (non-Javadoc)
     * @see org.ironrabbit.tbchat.IOtrKeyManager#unverifyKey(java.lang.String)
     */
    @Override
    public void unverifyKey(String address) throws RemoteException {

        _keyManager.unverifyUser(address);

    }

    /* (non-Javadoc)
     * @see org.ironrabbit.tbchat.IOtrKeyManager#isKeyVerified(java.lang.String)
     */
    @Override
    public boolean isKeyVerified(String address) throws RemoteException {
        return _keyManager.isVerifiedUser(address);
    }

    /* (non-Javadoc)
     * @see org.ironrabbit.tbchat.IOtrKeyManager#getLocalFingerprint(java.lang.String)
     */
    @Override
    public String getLocalFingerprint() throws RemoteException {

        if (_sessionId != null)
            return _keyManager.getLocalFingerprint(_sessionId);
        else if (_accountId != null)
            return _keyManager.getLocalFingerprint(_accountId);
        else
            return null;
    }

    /* (non-Javadoc)
     * @see org.ironrabbit.tbchat.IOtrKeyManager#getRemoteFingerprint(java.lang.String)
     */
    @Override
    public String getRemoteFingerprint() throws RemoteException {

        return _keyManager.getRemoteFingerprint(_sessionId);
    }

    /* (non-Javadoc)
     * @see org.ironrabbit.tbchat.IOtrKeyManager#generateLocalKeyPair(java.lang.String)
     */
    @Override
    public void generateLocalKeyPair() throws RemoteException {

        if (_sessionId != null)
            _keyManager.generateLocalKeyPair(_sessionId);
        else if (_accountId != null)
            _keyManager.generateLocalKeyPair(_accountId);
    }

}
