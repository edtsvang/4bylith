package bylith_project.client;

import org.springframework.stereotype.Component;

@Component
public class ProtocolClientFactory {
	private final HttpClientImpl httpClient;
	private final FtpClientImpl ftpClient;
	private final SshClientImpl sshClient;

	public ProtocolClientFactory(HttpClientImpl httpClient, FtpClientImpl ftpClient, SshClientImpl sshClient) {
		this.httpClient = httpClient;
		this.ftpClient = ftpClient;
		this.sshClient = sshClient;
	}

	public ProtocolClient getClient(String protocol) {
		return switch (protocol.toUpperCase()) {
		case "HTTP", "HTTPS" -> httpClient;
		case "FTP" -> ftpClient;
		case "SSH" -> sshClient;
		default -> throw new IllegalArgumentException("Unsupported protocol: " + protocol);
		};
	}
}