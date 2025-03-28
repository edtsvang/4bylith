package bylith_project.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bylith_project.entity.RequestHistory;
import bylith_project.entity.Server;
import bylith_project.exceptions.GlobalExceptionHandler.ResourceNotFoundException;
import bylith_project.repository.RequestHistoryRepository;
import bylith_project.repository.ServerRepository;

@RestController
@RequestMapping("/api/servers")
public class ServerController {
    private final ServerRepository serverRepository;
    private final RequestHistoryRepository historyRepository;

    public ServerController(ServerRepository serverRepository, RequestHistoryRepository historyRepository) {
        this.serverRepository = serverRepository;
        this.historyRepository = historyRepository;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Server> createServer(@RequestBody Server server) {
        server.setHealthStatus(Server.HealthStatus.UNKNOWN);
        Server savedServer = serverRepository.save(server);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedServer);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Server> getServer(@PathVariable Long id) {
        Server server = serverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Server  with id " + id + " not found"));
        List<RequestHistory> lastRequests = historyRepository.findTop10ByServerOrderByTimestampDesc(server);
        server.setRequestHistory(lastRequests);
        return ResponseEntity.ok(server);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Server> updateServer(@PathVariable Long id, @RequestBody Server updatedServer) {
        Server server = serverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Server  with id " + id + " not found"));
        server.setName(updatedServer.getName());
        server.setUrl(updatedServer.getUrl());
        server.setProtocol(updatedServer.getProtocol());
        Server savedServer = serverRepository.save(server);
        return ResponseEntity.ok(savedServer);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteServer(@PathVariable Long id) {
        Server server = serverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Server  with id " + id + " not found"));
        serverRepository.delete(server);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Server>> getAllServers() {
        List<Server> servers = serverRepository.findAll();
        return ResponseEntity.ok(servers);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<RequestHistory>> getServerHistory(@PathVariable Long id) {
        Server server = serverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Server  with id " + id + " not found"));
        List<RequestHistory> history = historyRepository.findByServerOrderByTimestampDesc(server);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/{id}/health-at")
    public ResponseEntity<String> getHealthAtTimestamp(@PathVariable Long id, @RequestParam String timestamp) {
        Server server = serverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Server  with id " + id + " not found"));
        LocalDateTime time;
        try {
            time = LocalDateTime.parse(timestamp);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid timestamp format: " + timestamp);
        }

        List<RequestHistory> history = historyRepository.findByServerAndTimestampBeforeOrderByTimestampDesc(server, time);
        if (history.size() < 5) {
            return ResponseEntity.ok(Server.HealthStatus.UNKNOWN.name());
        }

        boolean isHealthy = history.stream().limit(5).allMatch(RequestHistory::isSuccess);

        boolean isUnhealthy = false;
        List<RequestHistory> lastFive = history.stream().limit(5).toList();
        for (int i = 0; i <= lastFive.size() - 3; i++) {
            if (!lastFive.get(i).isSuccess() && !lastFive.get(i + 1).isSuccess() && !lastFive.get(i + 2).isSuccess()) {
                isUnhealthy = true;
                break;
            }
        }

        String healthStatus = isHealthy ? Server.HealthStatus.HEALTHY.name()
                : isUnhealthy ? Server.HealthStatus.UNHEALTHY.name() : Server.HealthStatus.UNKNOWN.name();
        return ResponseEntity.ok(healthStatus);
    }
}