package searchengine.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "connect")
@Getter
@Setter
public class ConnectConfig {

    private String userAgent;
    private String referrer;
    private Cookie cookie;
    private int timeout;
    private boolean ignoreHttpErrors;
    private boolean ignoreContentType;
    private int maxBodySize;
}
