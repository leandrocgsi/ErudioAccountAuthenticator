package br.com.erudio.accounts_example;

import static br.com.erudio.authenticator.authentication.AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import br.com.erudio.authenticator.authentication.AccountGeneral;

public class Main extends Activity {
	
	private static final String STATE_DIALOG = "state_dialog";
	private static final String STATE_INVALIDATE = "state_invalidate";

    private String TAG = this.getClass().getSimpleName();
    private AccountManager mAccountManager;
    private AlertDialog mAlertDialog;
    private boolean mInvalidate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        mAccountManager = AccountManager.get(this);

        findViewById(R.id.btnAddAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            addNewAccount(AccountGeneral.ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
            }
        });

        findViewById(R.id.btnGetAuthToken).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            showAccountPicker(AUTHTOKEN_TYPE_FULL_ACCESS, false);
            }
        });

        findViewById(R.id.btnGetAuthTokenConvenient).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            getTokenForAccountCreateIfNeeded(AccountGeneral.ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
            }
        });
        findViewById(R.id.btnInvalidateAuthToken).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            showAccountPicker(AUTHTOKEN_TYPE_FULL_ACCESS, true);
            }
        });
        
        if (savedInstanceState != null) {
        	boolean showDialog = savedInstanceState.getBoolean(STATE_DIALOG);
        	boolean invalidate = savedInstanceState.getBoolean(STATE_INVALIDATE);
        	if (showDialog) {
        		showAccountPicker(AUTHTOKEN_TYPE_FULL_ACCESS, invalidate);
        	}
        }

    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	if (mAlertDialog != null && mAlertDialog.isShowing()) {
    		outState.putBoolean(STATE_DIALOG, true);
    		outState.putBoolean(STATE_INVALIDATE, mInvalidate);
    	}
    }

    private void addNewAccount(String accountType, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.addAccount(accountType, authTokenType, null, null, this, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
            try {
                Bundle bnd = future.getResult();
                showMessage("Account was created");
                Log.d("Erudio", "AddNewAccount Bundle is " + bnd);

            } catch (Exception e) {
                e.printStackTrace();
                showMessage(e.getMessage());
            }
            }
        }, null);
    }

    private void showAccountPicker(final String authTokenType, final boolean invalidate) {
    	mInvalidate = invalidate;
        final Account availableAccounts[] = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

        if (availableAccounts.length == 0) {
            Toast.makeText(this, "No accounts", Toast.LENGTH_SHORT).show();
        } else {
            String name[] = new String[availableAccounts.length];
            for (int i = 0; i < availableAccounts.length; i++) {
                name[i] = availableAccounts[i].name;
            }

            // Account picker
            mAlertDialog = new AlertDialog.Builder(this).setTitle("Pick Account").setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, name), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                if(invalidate)
                    invalidateAuthToken(availableAccounts[which], authTokenType);
                else
                    getExistingAccountAuthToken(availableAccounts[which], authTokenType);
                }
            }).create();
            mAlertDialog.show();
        }
    }

    private void getExistingAccountAuthToken(Account account, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(account, authTokenType, null, this, null, null);

        new Thread(new Runnable() {
            @Override
            public void run() {
            try {
                Bundle bnd = future.getResult();

                final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                showMessage((authtoken != null) ? "SUCCESS!\ntoken: " + authtoken : "FAIL");
                Log.d("Erudio", "GetToken Bundle is " + bnd);
            } catch (Exception e) {
                e.printStackTrace();
                showMessage(e.getMessage());
            }
            }
        }).start();
    }

    private void invalidateAuthToken(final Account account, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(account, authTokenType, null, this, null,null);

        new Thread(new Runnable() {
            @Override
            public void run() {
            try {
                Bundle bnd = future.getResult();

                final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                mAccountManager.invalidateAuthToken(account.type, authtoken);
                showMessage(account.name + " invalidated");
            } catch (Exception e) {
                e.printStackTrace();
                showMessage(e.getMessage());
            }
            }
        }).start();
    }

    private void getTokenForAccountCreateIfNeeded(String accountType, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthTokenByFeatures(accountType, authTokenType, null, this, null, null,
            new AccountManagerCallback<Bundle>() {
                @Override
                public void run(AccountManagerFuture<Bundle> future) {
                Bundle bnd = null;
                try {
                    bnd = future.getResult();
                    final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                    showMessage(((authtoken != null) ? "SUCCESS!\ntoken: " + authtoken : "FAIL"));
                    Log.d("Erudio", "GetTokenForAccount Bundle is " + bnd);

                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(e.getMessage());
                }
                }
            }
            , null);
    }

    private void showMessage(final String msg) {
    	if (TextUtils.isEmpty(msg)) return;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}