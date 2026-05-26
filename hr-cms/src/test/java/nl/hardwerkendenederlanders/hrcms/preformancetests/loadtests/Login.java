package nl.hardwerkendenederlanders.hrcms.preformancetests.loadtests;

import java.time.Duration;
import java.util.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import io.gatling.javaapi.jdbc.*;
import nl.hardwerkendenederlanders.hrcms.HrCmsApplication;
import nl.hardwerkendenederlanders.hrcms.models.Role;
import nl.hardwerkendenederlanders.hrcms.services.UserServiceImpl;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.RoleService;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import static io.gatling.javaapi.jdbc.JdbcDsl.*;

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


  @Override
  public void before() {
    var app = SpringApplication.run(
            HrCmsApplication.class,
            new  String[] {"--server.port=" + serverPort});
    var roleService = app.getBean(RoleService.class);
    List<Role> userId = roleService.findAll();
    System.out.println("Id of user Role is:");
    System.out.println(userId.get(2).getId());
    System.out.println(userId.get(2).getRoleName());
    var userService = app.getBean(UserService.class);
    userService.insertUser("int", "", "int", "int@int.int", "int", userId.get(2).getId());
    var users = userService.getUsers(0, "", false);
    var encoder = new BCryptPasswordEncoder();

    System.out.println(users);
    var user1 = users.get(0);
    System.out.println(user1.getEmail());
    System.out.println(user1.getPasswordHash());
    System.out.println(encoder.matches("int", user1.getPasswordHash()));
  }

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
      pause(1000),
      http("request_3")
        .get("/")
        .headers(headers_2)
        .check(status().is(200))
    );

  {
	  setUp(scn.injectOpen(atOnceUsers(1))).protocols(httpProtocol);
  }
}
