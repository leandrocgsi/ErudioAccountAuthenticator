package br.com.erudio.authenticator.authentication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ErudioAuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {

        ErudioAuthenticator authenticator = new ErudioAuthenticator(this);
        return authenticator.getIBinder();
    }
}
