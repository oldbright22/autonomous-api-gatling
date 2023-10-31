package Feature.Simulation;

import Feature.Utility.ConfigLoader;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.util.ArrayList;
import java.util.List;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class Simulation extends io.gatling.javaapi.core.Simulation {

    private static final int USER_COUNT = Integer.parseInt(ConfigLoader.getInstance().getUsers());
    private static final int RAMP_DURATION = Integer.parseInt(ConfigLoader.getInstance().getRampDuration());



    //Http configuration
    private HttpProtocolBuilder protocol = http
            .baseUrl(ConfigLoader.getInstance().getBaseurl().toString())
            .acceptHeader(ConfigLoader.getInstance().getAcceptheader().toString())
            .contentTypeHeader(ConfigLoader.getInstance().getContenttypeheader().toString());


    private List<ChainBuilder> dynamicChainBuilder = new ArrayList<>();

    private Scenarios scenarios = new Scenarios();
    private List<PopulationBuilder> dynamicScenarios = new ArrayList<>();

    //BEFORE BLOCK
    @Override
    public void before() {
        System.out.printf("Virtual Users %d users %n", USER_COUNT);
        System.out.printf("Ramp up users over %d seconds %n", RAMP_DURATION);
    }

    private ScenarioBuilder scn = scenarios.createDynamicScenario("Scenario 1");

    //Load Simulation - with dynamic parameters at run time
    {
        setUp(
                scn.injectOpen(
                        nothingFor(5),
                        rampUsers(USER_COUNT).during(RAMP_DURATION)
                ).protocols(http)
        );

    }

/*
injectOpen,  vs  injectClose,  vs inject
// Define the list of scenarios
  val scenarios = List(
    scenario1.inject(rampUsers(10) during (10 seconds)),
    scenario2.inject(rampUsers(5) during (5 seconds))
  )

  // Set up the load test with dynamic scenarios
  setUp(scenarios)
    .protocols(http)
}
*/



 /*
    {
    setUp(
            scn.injectClosed(injectionProfile)
                // child1 and child2 will start at the same time when last parent user will terminate
                .andThen(child1.injectClosed(injectionProfile)
                     // grandChild will start when last child1 user will terminate
                    .andThen(grandChild.injectClosed(injectionProfile)),
                    child2.injectClosed(injectionProfile)
                ).andThen(
                // child3 will start when last grandChild and child2 users will terminate
                child3.injectClosed(injectionProfile)
                )
         );
    }
    */



    //private ScenarioBuilder scn = new io.gatling.core.structure.ScenarioBuilder()

    // Define the simulation setUp
    // setUp(dynamicScenarios.toArray(new ChainBuilder[0]))
    //        .protocols(httpProtocol);



    //mvn gatling:test -DUSERS=10 -DRAMP_DURATION=20
}



//private static FeederBuilder.Batchable<String> feeder = csv("feeders/file1.csv").random();
//private static FeederBuilder.FileBased<Object> feederNew = jsonFile("feeders/" + ConfigLoader.getInstance().getNewfile()).random();
//private static FeederBuilder.FileBased<Object> feederUpdate = jsonFile("feeders/" + ConfigLoader.getInstance().getUpdatefile()).random();


//private static ChainBuilder executeGetAll =
//        exec(http("Get All ")
//                .get(ConfigLoader.getInstance().getEndpointGetall().toString()));

    /*
    private static ChainBuilder executeAuthenticate =
            exec(http("Authenticate")
                    .post("/authenticate")
                    .body(StringBody("{\n" +
                            "  \"password\": \"" + ConfigLoader.getInstance().getAdminuser().toString() + "\",\n" +
                            "  \"username\": \"" + ConfigLoader.getInstance().getAdminpassword().toString() + "\"\n" +
                            "}"))
                    .check(jmesPath("token").saveAs("jwtToken"))
            );

    private static ChainBuilder executePost =
            feed(feederNew)
                    .exec(http("API Post - #{name}")
                            .post(ConfigLoader.getInstance().getEndpointPost().toString())
                            .header("Authorization", "Bearer #{jwtToken}")
                            .body(ElFileBody("bodies/" + ConfigLoader.getInstance().getTempfile())).asJson()
                            .check(jmesPath("id").saveAs("jwtId"))
                            .check(status().is(201))
                            //.check(status().is(session ->
                            //        200 + java.util.concurrent.ThreadLocalRandom.current().nextInt(2) // 2
                            //))

                    );

    private static ChainBuilder executePut =
            feed(feederUpdate)
                    .exec(http("API Put - #{name} - #{id}")
                            .put(ConfigLoader.getInstance().getEndpointPut().toString() + "/#{id}")
                            .header("Authorization", "Bearer #{jwtToken}")
                            .body(ElFileBody("bodies/" + ConfigLoader.getInstance().getTempfile())).asJson()
                            .check(jmesPath("id").saveAs("jwtId"))
                    );

    private static ChainBuilder executeGetSingle =
            exec(http("API Get Single - #{name} - #{id} - #{jwtId}")
                    .get(ConfigLoader.getInstance().getEndpointGetsingle().toString() + "/#{id}")
                    .check(jmesPath("name").saveAs("jwtName"))
                    .check(jmesPath("name").isEL("#{jwtName}"))
                    .check(status().is(200))
            );

    private static ChainBuilder executeDelete =
            exec(http("API Delete - #{name} - #{id} ")
                    .delete(ConfigLoader.getInstance().getEndpointDelete().toString() + "/#{id}")
                    .header("Authorization", "Bearer #{jwtToken}")
                    .check(bodyString().is("API deleted completed"))
            );
    */


    /*
    // Define a loop to run each scenario
    List<ChainBuilder> dynamicScenarios = new ArrayList<>();
    for (String scenarioName : scenarioNames) {
        ChainBuilder scenarioChain = scenario(scenarioName)
                .feed(scenarioFeeder)
                .exec(http("Request in " + scenarioName)
                        .get("/page/${scenarioName}"))
                .pause(3);
        dynamicScenarios.add(scenarioChain);
    }*/


    /*
    //Scenario Definition
    private ScenarioBuilder scn =
            scenario("Video Game Stress Test")
                    .exec(executeGetAll)
                    .pause(2)
                    .exec(executeAuthenticate)
                    .pause(2)
                    .exec(executePost)
                    .pause(2)
                    .exec(executeGetSingle)
                    .pause(2)
                    .exec(executeDelete);
    */
