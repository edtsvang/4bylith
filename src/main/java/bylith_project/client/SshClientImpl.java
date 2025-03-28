package bylith_project.client;

import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

@Component
public class SshClientImpl implements ProtocolClient {
	private long responseTime;

	@Value("${ssh.username}")
	private String sshUsername;

	@Value("${ssh.password}")
	private String sshPassword;

	@Value("${server.monitor.timeout}")
	private long timeoutMs;

	@Override
	public boolean isHealthy(String url) {
		JSch jsch = new JSch();
		Session session = null;
		try {
			URL sshUrl = new URL(url);
			String host = sshUrl.getHost();
			int port = sshUrl.getPort() != -1 ? sshUrl.getPort() : 22;
			session = jsch.getSession(sshUsername, host, port);
			session.setPassword(sshPassword);

			long start = System.currentTimeMillis();
			session.connect((int) timeoutMs);
			boolean isConnected = session.isConnected();
			responseTime = System.currentTimeMillis() - start;

			return isConnected && responseTime < timeoutMs;
		} catch (Exception e) {
			responseTime = timeoutMs + 1;
			return false;
		} finally {
			if (session != null && session.isConnected()) {
				session.disconnect();
			}
		}
	}

	@Override
	public long getResponseTime() {
		return responseTime;
	}

}
