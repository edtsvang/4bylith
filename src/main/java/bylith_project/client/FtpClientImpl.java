package bylith_project.client;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FtpClientImpl implements ProtocolClient {
	private long responseTime;

	@Value("${ftp.username}")
	private String ftpUsername;

	@Value("${ftp.password}")
	private String ftpPassword;

	@Value("${server.monitor.timeout}")
	private long timeoutMs;

	@Override
	public boolean isHealthy(String url) {
		FTPClient ftpClient = new FTPClient();

		try {
			URL ftpUrl = new URL(url);
			String host = ftpUrl.getHost();
			long start = System.currentTimeMillis();
			ftpClient.connect(host);
			ftpClient.login(ftpUsername, ftpPassword);
			ftpClient.setSoTimeout((int) timeoutMs);
			boolean isConnected = ftpClient.isConnected();
			ftpClient.disconnect();
			responseTime = System.currentTimeMillis() - start;
			return isConnected && responseTime < timeoutMs;
		} catch (IOException e) {
			responseTime = timeoutMs + 1;
			return false;
		} finally {
			try {
				if (ftpClient.isConnected()) {
					ftpClient.disconnect();
				}
			} catch (IOException ignored) {
			}
		}
	}

	@Override
	public long getResponseTime() {
		return responseTime;
	}

}
