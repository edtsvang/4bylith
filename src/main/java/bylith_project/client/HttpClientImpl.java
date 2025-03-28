package bylith_project.client;

import java.io.IOException;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HttpClientImpl implements ProtocolClient {
	private long responseTime;

	@Value("${server.monitor.timeout}")
	private long timeoutMs;

	@SuppressWarnings("deprecation")
	@Override
	public boolean isHealthy(String url) {
		try (CloseableHttpClient client = HttpClients.createDefault()) {
			HttpGet request = new HttpGet(url);
			request.setConfig(RequestConfig.custom().setConnectionRequestTimeout(Timeout.ofMilliseconds(timeoutMs))
					.setResponseTimeout(Timeout.ofMilliseconds(timeoutMs)).build());

			long start = System.currentTimeMillis();
			try (CloseableHttpResponse response = client.execute(request)) {
				responseTime = System.currentTimeMillis() - start;
				int statusCode = response.getCode();
				EntityUtils.consume(response.getEntity());
				return statusCode >= 200 && statusCode < 300 && responseTime < timeoutMs;
			}
		} catch (IOException e) {
			responseTime = timeoutMs + 1;
			return false;
		}
	}

	@Override
	public long getResponseTime() {
		return responseTime;
	}

}
