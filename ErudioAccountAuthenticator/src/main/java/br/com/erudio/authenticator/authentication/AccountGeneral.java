package br.com.erudio.authenticator.authentication;

public class AccountGeneral {

    public static final String ACCOUNT_TYPE = "br.com.erudio.auth_example";

    public static final String ACCOUNT_NAME = "Erudio";

    public static final String AUTHTOKEN_TYPE_READ_ONLY = "Read only";
    public static final String AUTHTOKEN_TYPE_READ_ONLY_LABEL = "Read only access to an Erudio account";

    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full access";
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS_LABEL = "Full access to an Erudio account";

    public static final ServerAuthenticate sServerAuthenticate = new ParseComServerAuthenticate();
}
