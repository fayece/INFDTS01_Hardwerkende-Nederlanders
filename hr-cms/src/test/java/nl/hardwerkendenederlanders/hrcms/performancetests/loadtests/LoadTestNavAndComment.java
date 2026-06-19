package nl.hardwerkendenederlanders.hrcms.performancetests.loadtests;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import static nl.hardwerkendenederlanders.hrcms.performancetests.performancetestConfig.performanceTestServerPort;
import static nl.hardwerkendenederlanders.hrcms.performancetests.utils.utils.randint;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import java.util.*;

public class LoadTestNavAndComment extends Simulation {

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
            Map.entry("Upgrade-Insecure-Requests", "1"),
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
            Map.entry("Upgrade-Insecure-Requests", "1"),
            Map.entry("sec-ch-ua", "Chromium\";v=\"148\", \"Brave\";v=\"148\", \"Not/A)Brand\";v=\"99"),
            Map.entry("sec-ch-ua-mobile", "?1"),
            Map.entry("sec-ch-ua-platform", "Android"));

    private Map<CharSequence, String> headers_2 = Map.ofEntries(
            Map.entry("Cache-Control", "no-cache"),
            Map.entry("Pragma", "no-cache"),
            Map.entry("Sec-Fetch-Dest", "document"),
            Map.entry("Sec-Fetch-Mode", "navigate"),
            Map.entry("Sec-Fetch-Site", "same-origin"),
            Map.entry("Sec-Fetch-User", "?1"),
            Map.entry("Sec-GPC", "1"),
            Map.entry("Upgrade-Insecure-Requests", "1"),
            Map.entry("sec-ch-ua", "Chromium\";v=\"148\", \"Brave\";v=\"148\", \"Not/A)Brand\";v=\"99"),
            Map.entry("sec-ch-ua-mobile", "?1"),
            Map.entry("sec-ch-ua-platform", "Android"));

    private Map<CharSequence, String> headers_5 = Map.ofEntries(
            Map.entry("Accept", "*/*"),
            Map.entry("Cache-Control", "no-cache"),
            Map.entry("Origin", "http://localhost:" + performanceTestServerPort),
            Map.entry("Pragma", "no-cache"),
            Map.entry("Sec-Fetch-Dest", "empty"),
            Map.entry("Sec-Fetch-Mode", "cors"),
            Map.entry("Sec-Fetch-Site", "same-origin"),
            Map.entry("Sec-GPC", "1"),
            Map.entry("sec-ch-ua", "Chromium\";v=\"148\", \"Brave\";v=\"148\", \"Not/A)Brand\";v=\"99"),
            Map.entry("sec-ch-ua-mobile", "?1"),
            Map.entry("sec-ch-ua-platform", "Android"));

    private ScenarioBuilder scn = scenario("login")
            .exec(
                    http("get login page").get("/login").headers(headers_0),
                    pause(4),
                    http("post login information")
                            .post("/login")
                            .headers(headers_1)
                            .formParam("username", "user" + randint(10_000))
                            .formParam("password", "secret")
                            .check(status().is(404)), // for some reason logging in on gatling sends you to 404
                    // page. probs sends you to
                    // http://localhost:6031/jsession<somethingelsehere>
                    pause(3),
                    http("get random page of articles").get("/?page=2").headers(headers_2),
                    pause(3),
                    http("get another random page of articles")
                            .get("/?page=" + randint(200))
                            .headers(headers_2),
                    pause(2),
                    http("get specific article")
                            .get("/article/00000000-0000-0000-0000-001900190018")
                            .headers(headers_2),
                    pause(14),
                    http("post comment at article")
                            .post("/comment/article/00000000-0000-0000-0000-001900190018/new")
                            .headers(headers_5)
                            .formParam("commentBody", "amaij das tijd voor nenen reactie he mannekes?"),
                    pause(5),
                    http("go to home page").get("/").headers(headers_2));

    {
        setUp(scn.injectOpen(constantUsersPerSec(4).during(60))).protocols(httpProtocol);
    }
}
