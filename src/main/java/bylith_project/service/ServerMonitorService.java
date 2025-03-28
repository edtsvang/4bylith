package bylith_project.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import bylith_project.client.ProtocolClient;
import bylith_project.client.ProtocolClientFactory;
import bylith_project.entity.RequestHistory;
import bylith_project.entity.Server;
import bylith_project.repository.RequestHistoryRepository;
import bylith_project.repository.ServerRepository;

@Service
public class ServerMonitorService {
	private final ServerRepository serverRepository;
	private final RequestHistoryRepository historyRepository;
	private final EmailService emailService;
	private final ProtocolClientFactory protocolClientFactory;

	public ServerMonitorService(ServerRepository serverRepository, RequestHistoryRepository historyRepository,
			EmailService emailService, ProtocolClientFactory protocolClientFactory) {
		this.serverRepository = serverRepository;
		this.historyRepository = historyRepository;
		this.emailService = emailService;
		this.protocolClientFactory = protocolClientFactory;
	}

	@Scheduled(fixedRate = 60000)
	public void monitorServers() {
		List<Server> servers = serverRepository.findAll();
		for (Server server : servers) {
			ProtocolClient client = protocolClientFactory.getClient(server.getProtocol());
			boolean isHealthy = client.isHealthy(server.getUrl());
			long responseTime = client.getResponseTime();

			RequestHistory history = new RequestHistory();
			history.setServer(server);
			history.setTimestamp(LocalDateTime.now());
			history.setSuccess(isHealthy);
			history.setResponseTime(responseTime);
			historyRepository.save(history);

			updateServerStatus(server);
		}
	}

	private void updateServerStatus(Server server) {
		List<RequestHistory> recentRequests = historyRepository.findTop5ByServerOrderByTimestampDesc(server);
		boolean isHealthy = recentRequests.size() >= 5 && recentRequests.stream().allMatch(RequestHistory::isSuccess);
		boolean isUnhealthy = false;
		if (recentRequests.size() >= 3) {
			for (int i = 0; i <= recentRequests.size() - 3; i++) {
				if (!recentRequests.get(i).isSuccess() && !recentRequests.get(i + 1).isSuccess()
						&& !recentRequests.get(i + 2).isSuccess()) {
					isUnhealthy = true;
					break;
				}
			}
		}

		Server.HealthStatus previousStatus = server.getHealthStatus();
		if (isHealthy) {
			server.setHealthStatus(Server.HealthStatus.HEALTHY);
		} else if (isUnhealthy) {
			server.setHealthStatus(Server.HealthStatus.UNHEALTHY);
		} else {
			server.setHealthStatus(Server.HealthStatus.UNKNOWN);
		}

		if (server.getHealthStatus() == Server.HealthStatus.UNHEALTHY
				&& previousStatus != Server.HealthStatus.UNHEALTHY) {
			emailService.sendAlertEmail(server.getName(), server.getUrl());
		}

		serverRepository.save(server);
	}
}