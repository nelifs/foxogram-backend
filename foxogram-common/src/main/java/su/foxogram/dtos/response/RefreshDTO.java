package su.foxogram.dtos.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshDTO {
    private String accessToken;
    private String refreshToken;
    private long expiresAt;

    public RefreshDTO(String accessToken, String refreshToken, long expiresAt) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
    }
}
