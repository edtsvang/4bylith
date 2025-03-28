package bylith_project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "request_history")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RequestHistory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	@JoinColumn(name = "server_id")
	private Server server;
	@Column(nullable = false)
	private LocalDateTime timestamp;
	@Column(nullable = false)
	private boolean success;
	@Column(name = "response_time", nullable = false)
	private long responseTime;
}