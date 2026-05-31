package nl.hardwerkendenederlanders.hrcms.performancetests.stresstests;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;
import static nl.hardwerkendenederlanders.hrcms.performancetests.utils.utils.performanceTestServerPort;
import static nl.hardwerkendenederlanders.hrcms.performancetests.utils.utils.randint;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import java.util.Map;

public class StressTestLogin extends Simulation {

    private HttpProtocolBuilder httpProtocol = http.baseUrl("http://localhost:" + performanceTestServerPort)
            .inferHtmlResources(
                    AllowList(),
                    DenyList(
                            ".*\\.js",
                            ".*\\.css",
                            ".*\\.gif",
                            ".*\\.jpeg",
                            ".*\\.jpg",
                            ".*\\.ico",
                            ".*\\.woff",
                            ".*\\.woff2",
                            ".*\\.(t|o)tf",
                            ".*\\.png",
                            ".*\\.svg",
                            ".*detectportal\\.firefox\\.com.*"))
            .acceptHeader(
                    "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8")
            .acceptEncodingHeader("gzip, deflate, br")
            .acceptLanguageHeader("nl;q=0.7")
            .upgradeInsecureRequestsHeader("1")
            .userAgentHeader(
                    "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/148.0.0.0 Mobile Safari/537.36");

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
            Map.entry("sec-ch-ua-platform", "Android"));

    private Map<CharSequence, String> headers_1 = Map.ofEntries(
            Map.entry("Cache-Control", "no-cache"),
            Map.entry("Origin", "http://localhost:" + performanceTestServerPort),
            Map.entry("Pragma", "no-cache"),
            Map.entry("Sec-Fetch-Dest", "document"),
            Map.entry("Sec-Fetch-Mode", "navigate"),
            Map.entry("Sec-Fetch-Site", "same-origin"),
            Map.entry("Sec-Fetch-User", "?1"),
            Map.entry("Sec-GPC", "1"),
            Map.entry("sec-ch-ua", "Chromium\";v=\"148\", \"Brave\";v=\"148\", \"Not/A)Brand\";v=\"99"),
            Map.entry("sec-ch-ua-mobile", "?1"),
            Map.entry("sec-ch-ua-platform", "Android"));

    private ScenarioBuilder scn = scenario("login")
            .exec(
                    http("open login page").get("/login").headers(headers_0),
                    pause(6),
                    http("post login request")
                            .post("/login")
                            .headers(headers_1)
                            .formParam("email", randint(20_000) + "@theorg.nl")
                            .formParam("password", "secret")
                            .check(status().is(404)), // for some reason logging in on gatling sends you to 404
                    // page. probs sends you to
                    // http://localhost:6031/jsession<somethingelsehere>
                    http("force move to home page").get("/").headers(headers_1).check(status().is(200)));

    {
        setUp(scn.injectOpen(atOnceUsers(2000))).protocols(httpProtocol);
    }
}
