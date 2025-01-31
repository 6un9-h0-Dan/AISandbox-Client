package dev.aisandbox.client.agent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.xpath;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import dev.aisandbox.client.scenarios.TestRequest;
import dev.aisandbox.client.scenarios.TestResponse;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

public class AgentTest {

  @Test
  public void testPostJSON() throws AgentException {
    Agent a = new Agent();
    a.setTarget("http://localhost/postJSON");
    a.setEnableXML(false);
    a.setupAgent();
    // setup mock server
    MockRestServiceServer server = AgentMockTool.createMockServer(a);
    // setup expectations
    server
        .expect(requestTo("http://localhost/postJSON"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("name").value("Betty"))
        .andRespond(withSuccess("{\"number\":\"4\"}", MediaType.APPLICATION_JSON));
    // run request
    TestRequest req = new TestRequest();
    req.setName("Betty");
    TestResponse r = a.postRequest(req, TestResponse.class);
    server.verify();
    assertEquals("Answer=4", 4, r.getNumber());
  }

  @Test
  public void testPostXML() throws Exception {
    Agent a = new Agent();
    a.setTarget("http://localhost/postXML");
    a.setEnableXML(true);
    a.setupAgent();
    // setup mock server
    MockRestServiceServer server = AgentMockTool.createMockServer(a);
    // setup expectations
    server
        .expect(requestTo("http://localhost/postXML"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(content().contentType(MediaType.APPLICATION_XML))
        .andExpect(xpath("/testRequest/name").string("Fred"))
        .andRespond(
            withSuccess(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><testResponse><number>4</number></testResponse>",
                MediaType.APPLICATION_XML));
    // run request
    TestRequest req = new TestRequest();
    req.setName("Fred");
    TestResponse r = a.postRequest(req, TestResponse.class);
    server.verify();
    assertEquals("Answer=4", 4, r.getNumber());
  }

  @Test
  public void testPostBasicAuthXML() throws AgentException {
    Agent a = new Agent();
    a.setTarget("http://localhost/getXML");
    a.setEnableXML(true);
    a.setBasicAuth(true);
    a.setBasicAuthUsername("Aladdin");
    a.setBasicAuthPassword("OpenSesame");
    a.setupAgent();
    // setup mock server
    MockRestServiceServer server = AgentMockTool.createMockServer(a);
    // setup expectations
    server
        .expect(requestTo("http://localhost/getXML"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(header("Authorization", "Basic QWxhZGRpbjpPcGVuU2VzYW1l"))
        .andRespond(
            withSuccess(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><testResponse><number>5</number></testResponse>",
                MediaType.APPLICATION_XML));
    // run request
    TestRequest req = new TestRequest();
    req.setName("Fred");
    TestResponse r = a.postRequest(req, TestResponse.class);
    server.verify();
    assertEquals("Answer=4", 5, r.getNumber());
  }

  @Test
  public void testPostKeyAuthXML() throws AgentException {
    Agent a = new Agent();
    a.setTarget("http://localhost/getXML");
    a.setEnableXML(true);
    a.setApiKey(true);
    a.setApiKeyHeader("APIKey");
    a.setApiKeyValue("OpenSesame");
    a.setupAgent();
    // setup mock server
    MockRestServiceServer server = AgentMockTool.createMockServer(a);
    // setup expectations
    server
        .expect(requestTo("http://localhost/getXML"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(header("APIKey", "OpenSesame"))
        .andRespond(
            withSuccess(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><testResponse><number>5</number></testResponse>",
                MediaType.APPLICATION_XML));
    // run request
    TestRequest req = new TestRequest();
    req.setName("Fred");
    TestResponse r = a.postRequest(req, TestResponse.class);
    server.verify();
    assertEquals("Answer=4", 5, r.getNumber());
  }

  @Test
  public void testValidURL() {
    Agent a = new Agent();
    a.setTarget("http://localhost/api");
    assertTrue("Valid URL", a.getValidProperty().get());
  }

  @Test
  public void testInvalidURL() {
    Agent a = new Agent();
    a.setTarget("xxx://localhost/api");
    assertFalse("Inalid URL", a.getValidProperty().get());
  }

  @Test(expected = AgentConnectionException.class)
  public void testConnectionError() throws Exception {
    Agent a = new Agent();
    // use unreachable URL
    a.setTarget("http://localhost:9999/INVALID");
    a.setEnableXML(true);
    a.setApiKey(true);
    a.setApiKeyHeader("APIKey");
    a.setApiKeyValue("OpenSesame");
    a.setupAgent();
    // run request
    TestRequest req = new TestRequest();
    req.setName("Fred");
    TestResponse r = a.postRequest(req, TestResponse.class);
  }

  @Test(expected = AgentParserException.class)
  public void testPostBadXML() throws AgentException {
    Agent a = new Agent();
    a.setTarget("http://localhost/postXML");
    a.setEnableXML(true);
    a.setupAgent();
    // setup mock server
    MockRestServiceServer server = AgentMockTool.createMockServer(a);
    // setup expectations
    server
        .expect(requestTo("http://localhost/postXML"))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withSuccess("INVALID XML", MediaType.APPLICATION_XML));
    // run request
    TestRequest req = new TestRequest();
    req.setName("Fred");
    TestResponse r = a.postRequest(req, TestResponse.class);
    server.verify();
  }

  @Test(expected = AgentResetException.class)
  public void testResetPostRequest() throws Exception {
    Agent a = new Agent();
    a.setTarget("http://localhost/postXML");
    a.setEnableXML(true);
    a.setupAgent();
    // setup mock server
    MockRestServiceServer server = AgentMockTool.createMockServer(a);
    // setup expectations
    server
        .expect(requestTo("http://localhost/postXML"))
        .andExpect(method(HttpMethod.POST))
        .andExpect(content().contentType(MediaType.APPLICATION_XML))
        .andExpect(xpath("/testRequest/name").string("Fred"))
        .andRespond(withStatus(HttpStatus.RESET_CONTENT));
    // run request
    TestRequest req = new TestRequest();
    req.setName("Fred");
    TestResponse r = a.postRequest(req, TestResponse.class);
    server.verify();
  }
}
