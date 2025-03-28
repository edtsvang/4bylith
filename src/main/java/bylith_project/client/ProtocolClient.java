package bylith_project.client;

public interface ProtocolClient {

	boolean isHealthy(String url);

	long getResponseTime();

}
