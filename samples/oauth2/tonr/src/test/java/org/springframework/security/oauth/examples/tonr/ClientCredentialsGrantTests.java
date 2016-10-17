package org.springframework.security.oauth.examples.tonr;


import org.junit.*;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.token.*;
import org.springframework.security.oauth2.client.token.grant.client.*;
import org.springframework.security.oauth2.common.*;
import org.springframework.web.client.*;

import java.util.*;

import static org.junit.Assert.*;

/**
 * @author Ryan Heaton
 * @author Dave Syer
 */
public class ClientCredentialsGrantTests {

	@Rule
	public ServerRunning serverRunning = ServerRunning.isRunning();

	@Test
	public void testConnectDirectlyToResourceServer() throws Exception {

		ClientCredentialsResourceDetails resource = new ClientCredentialsResourceDetails();

		resource.setAccessTokenUri(serverRunning.getUrl("/sparklr2/oauth/token"));
		resource.setClientId("my-client-with-registered-redirect");
		resource.setId("sparklr");
		resource.setScope(Arrays.asList("trust"));

		ClientCredentialsAccessTokenProvider provider = new ClientCredentialsAccessTokenProvider();
		OAuth2AccessToken accessToken = provider.obtainAccessToken(resource, new DefaultAccessTokenRequest());

		OAuth2RestTemplate template = new OAuth2RestTemplate(resource, new DefaultOAuth2ClientContext(resource.getId(), accessToken));
		String result = template.getForObject(serverRunning.getUrl("/sparklr2/photos/trusted/message"), String.class);
		assertEquals("Hello, Trusted Client", result);

	}

	@Test
	public void testConnectThroughClientApp() throws Exception {

		// tonr is a trusted client of sparklr for this resource
		RestTemplate template = new RestTemplate();
		String result = template.getForObject(serverRunning.getUrl("/tonr2/trusted/message"), String.class);
		// System.err.println(result);
		assertEquals("{\"message\":\"Hello, Trusted Client\"}", result);

	}

}
