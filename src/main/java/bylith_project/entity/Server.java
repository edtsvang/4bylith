package bylith_project.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "servers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Server {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotBlank
	@NotNull
	private String name;
	@NotNull
	@NotBlank
	private String url;
	private String protocol;
	private HealthStatus healthStatus;

	@OneToMany(mappedBy = "server", cascade = CascadeType.ALL)
	private List<RequestHistory> requestHistory;

	public enum HealthStatus {
		HEALTHY, UNHEALTHY, UNKNOWN
	}

}
