package net.kdt.pojavlaunch.premium.account;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class AccountManager {
    private Context context;
    private SharedPreferences prefs;
    private Gson gson;
    private static final String PREFS_NAME = "bangla_accounts";
    private static final String ACCOUNTS_DIR = "accounts";

    public AccountManager(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
        createAccountsDirectory();
    }

    private void createAccountsDirectory() {
        File dir = new File(context.getFilesDir(), ACCOUNTS_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public void addAccount(GameAccount account) {
        List<GameAccount> accounts = getAllAccounts();
        accounts.add(account);
        saveAccounts(accounts);
    }

    public void updateAccount(GameAccount account) {
        List<GameAccount> accounts = getAllAccounts();
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getId().equals(account.getId())) {
                accounts.set(i, account);
                break;
            }
        }
        saveAccounts(accounts);
    }

    public void deleteAccount(String accountId) {
        List<GameAccount> accounts = getAllAccounts();
        accounts.removeIf(a -> a.getId().equals(accountId));
        saveAccounts(accounts);
        deleteAccountFiles(accountId);
    }

    public List<GameAccount> getAllAccounts() {
        String json = prefs.getString("accounts", "[]");
        Type type = new TypeToken<List<GameAccount>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public GameAccount getAccount(String accountId) {
        List<GameAccount> accounts = getAllAccounts();
        for (GameAccount account : accounts) {
            if (account.getId().equals(accountId)) {
                return account;
            }
        }
        return null;
    }

    public void setCurrentAccount(String accountId) {
        prefs.edit().putString("current_account", accountId).apply();
    }

    public GameAccount getCurrentAccount() {
        String accountId = prefs.getString("current_account", null);
        if (accountId != null) {
            return getAccount(accountId);
        }
        List<GameAccount> accounts = getAllAccounts();
        return accounts.isEmpty() ? null : accounts.get(0);
    }

    public boolean saveSkinToAccount(String accountId, byte[] skinData) {
        try {
            File skinFile = new File(context.getFilesDir(), ACCOUNTS_DIR + "/" + accountId + "/skin.png");
            skinFile.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(skinFile);
            fos.write(skinData);
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveCapToAccount(String accountId, byte[] capeData) {
        try {
            File capeFile = new File(context.getFilesDir(), ACCOUNTS_DIR + "/" + accountId + "/cape.png");
            capeFile.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(capeFile);
            fos.write(capeData);
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public File getSkinFile(String accountId) {
        File skinFile = new File(context.getFilesDir(), ACCOUNTS_DIR + "/" + accountId + "/skin.png");
        return skinFile.exists() ? skinFile : null;
    }

    public File getCapeFile(String accountId) {
        File capeFile = new File(context.getFilesDir(), ACCOUNTS_DIR + "/" + accountId + "/cape.png");
        return capeFile.exists() ? capeFile : null;
    }

    public boolean exportAccount(String accountId, File exportFile) {
        try {
            GameAccount account = getAccount(accountId);
            if (account == null) return false;

            String json = gson.toJson(account);
            FileOutputStream fos = new FileOutputStream(exportFile);
            fos.write(json.getBytes());
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean importAccount(File importFile) {
        try {
            byte[] data = Files.readAllBytes(importFile.toPath());
            String json = new String(data);
            GameAccount account = gson.fromJson(json, GameAccount.class);
            addAccount(account);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean backupAllAccounts(File backupFile) {
        try {
            List<GameAccount> accounts = getAllAccounts();
            String json = gson.toJson(accounts);
            FileOutputStream fos = new FileOutputStream(backupFile);
            fos.write(json.getBytes());
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean restoreAllAccounts(File backupFile) {
        try {
            byte[] data = Files.readAllBytes(backupFile.toPath());
            String json = new String(data);
            Type type = new TypeToken<List<GameAccount>>() {}.getType();
            List<GameAccount> accounts = gson.fromJson(json, type);
            saveAccounts(accounts);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void saveAccounts(List<GameAccount> accounts) {
        String json = gson.toJson(accounts);
        prefs.edit().putString("accounts", json).apply();
    }

    private void deleteAccountFiles(String accountId) {
        File accountDir = new File(context.getFilesDir(), ACCOUNTS_DIR + "/" + accountId);
        if (accountDir.exists()) {
            deleteDirectory(accountDir);
        }
    }

    private void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteDirectory(file);
                }
            }
        }
        dir.delete();
    }
}
