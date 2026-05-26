package nl.hardwerkendenederlanders.hrcms.preformancetests.loadtests;

import java.time.Duration;
import java.util.*;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;


public class Login extends Simulation {

  private String serverPort = "8080";

  private HttpProtocolBuilder httpProtocol = http
    .baseUrl("http://localhost:" + serverPort)
    .disableFollowRedirect()
    .inferHtmlResources(AllowList(), DenyList(".*\\.gif", ".*\\.jpeg", ".*\\.jpg", ".*\\.ico", ".*\\.woff", ".*\\.woff2", ".*\\.(t|o)tf", ".*\\.png", ".*\\.svg", ".*detectportal\\.firefox\\.com.*"))
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate, br")
    .acceptLanguageHeader("nl;q=0.7")
    .upgradeInsecureRequestsHeader("1")
    .userAgentHeader("Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36");
  
  private Map<CharSequence, String> headers_0 = Map.ofEntries(
    Map.entry("Cache-Control", "no-cache"),
    Map.entry("Pragma", "no-cache"),
    Map.entry("Sec-Fetch-Dest", "document"),
    Map.entry("Sec-Fetch-Mode", "navigate"),
    Map.entry("Sec-Fetch-Site", "none"),
    Map.entry("Sec-Fetch-User", "?1"),
    Map.entry("Sec-GPC", "1"),
    Map.entry("sec-ch-ua", "Chromium\";v=\"148\", \"Brave\";v=\"148\", \"Not/A)Brand\";v=\"99"),
    Map.entry("sec-ch-ua-mobile", "?1"),
    Map.entry("sec-ch-ua-platform", "Android")
  );
  
  private Map<CharSequence, String> headers_1 = Map.ofEntries(
    Map.entry("Cache-Control", "no-cache"),
    Map.entry("Origin", "http://localhost:" + serverPort),
    Map.entry("Pragma", "no-cache"),
    Map.entry("Sec-Fetch-Dest", "document"),
    Map.entry("Sec-Fetch-Mode", "navigate"),
    Map.entry("Sec-Fetch-Site", "same-origin"),
    Map.entry("Sec-Fetch-User", "?1"),
    Map.entry("Sec-GPC", "1"),
    Map.entry("sec-ch-ua", "Chromium\";v=\"148\", \"Brave\";v=\"148\", \"Not/A)Brand\";v=\"99"),
    Map.entry("sec-ch-ua-mobile", "?1"),
    Map.entry("sec-ch-ua-platform", "Android")
  );
  
  private Map<CharSequence, String> headers_2 = Map.ofEntries(
    Map.entry("Cache-Control", "no-cache"),
    Map.entry("Pragma", "no-cache"),
    Map.entry("Sec-Fetch-Dest", "document"),
    Map.entry("Sec-Fetch-Mode", "navigate"),
    Map.entry("Sec-Fetch-Site", "same-origin"),
    Map.entry("Sec-Fetch-User", "?1"),
    Map.entry("Sec-GPC", "1"),
    Map.entry("sec-ch-ua", "Chromium\";v=\"148\", \"Brave\";v=\"148\", \"Not/A)Brand\";v=\"99"),
    Map.entry("sec-ch-ua-mobile", "?1"),
    Map.entry("sec-ch-ua-platform", "Android")
  );


  private ScenarioBuilder scn = scenario("Login")
    .exec(
      http("request_0")
        .get("/login")
        .headers(headers_0),
      pause(2),
      http("request_1")
        .post("/login")
        .headers(headers_1)
        .formParam("email", "int@int.int")
        .formParam("password", "int")
        .check(status().is(302)),
      pause(1),
      http("request_3")
        .get("/")
        .headers(headers_2)
        .check(status().is(200))
    );

  {
	  setUp(scn.injectOpen(atOnceUsers(1))).protocols(httpProtocol);
  }
}
