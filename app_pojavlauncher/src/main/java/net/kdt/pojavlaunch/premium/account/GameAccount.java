package net.kdt.pojavlaunch.premium.account;

import com.google.gson.annotations.SerializedName;
import java.util.UUID;

public class GameAccount {
    @SerializedName("id")
    private String id;
    
    @SerializedName("username")
    private String username;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("type")
    private AccountType type;
    
    @SerializedName("accessToken")
    private String accessToken;
    
    @SerializedName("refreshToken")
    private String refreshToken;
    
    @SerializedName("uuid")
    private String uuid;
    
    @SerializedName("createdAt")
    private long createdAt;
    
    @SerializedName("lastUsed")
    private long lastUsed;
    
    @SerializedName("hasSkin")
    private boolean hasSkin;
    
    @SerializedName("hasCape")
    private boolean hasCape;

    public enum AccountType {
        MICROSOFT,
        OFFLINE,
        CRACKED
    }

    public GameAccount(String username, AccountType type) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.type = type;
        this.createdAt = System.currentTimeMillis();
        this.lastUsed = System.currentTimeMillis();
        this.hasSkin = false;
        this.hasCape = false;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public AccountType getType() { return type; }
    public void setType(AccountType type) { this.type = type; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public long getCreatedAt() { return createdAt; }
    public long getLastUsed() { return lastUsed; }
    public void setLastUsed(long lastUsed) { this.lastUsed = lastUsed; }

    public boolean hasSkin() { return hasSkin; }
    public void setHasSkin(boolean hasSkin) { this.hasSkin = hasSkin; }

    public boolean hasCape() { return hasCape; }
    public void setHasCape(boolean hasCape) { this.hasCape = hasCape; }
}
