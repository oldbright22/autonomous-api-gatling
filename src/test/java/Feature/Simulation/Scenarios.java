package Feature.Simulation;

import Feature.Utility.ConfigLoader;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;

import scala.collection.JavaConverters;
import scala.collection.immutable.ArraySeq;
import scala.collection.Map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static io.gatling.javaapi.core.CoreDsl.jsonFile;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;


public class Scenarios {

    private static List<java.util.Map<String, Object>> scenariosList = jsonFile("scenarios/scenariosFile.json").readRecords();
    private static int scenariosCount = jsonFile("scenarios/scenariosFile.json").recordsCount();


    public void Scenarios() {
        //Confirm there is in fact a scenario file present in designated path
    }


    public ScenarioBuilder createDynamicScenario(String scenarioName) {
        List<ChainBuilder> dynamicChains = new ArrayList<>();

        // Convert Java List to Scala List of Maps
        List<scala.collection.Map<String, Object>> scalaList = new ArrayList<>();

        for (java.util.Map<String, Object> javaMapEntry : scenariosList) {
            // Convert each Java Map to a Scala Map
            Map<String, Object> scalaMap = JavaConverters.mapAsScalaMap(javaMapEntry);

            // Add the Scala Map to the Scala List
            scalaList.add(scalaMap);
        }

        String baseURL = ConfigLoader.getInstance().getBaseurl().toString();
        String acceptHeader = ConfigLoader.getInstance().getAcceptheader().toString();
        String contentHeader = ConfigLoader.getInstance().getContenttypeheader().toString();
        String token = "";
        Iterator<Map<String, Object>> iterator = scalaList.iterator();

        while (iterator.hasNext()) {
            Map<String, Object> map = iterator.next();

            int id = Integer.parseInt((map.get("id")).get().toString());
            String chainName = map.get("chainName").get().toString();
            String request = map.get("request").get().toString();
            String endpoint = map.get("requestEndpoint").get().toString();

            Object requestBody = map.get("requestBody").get();
            ArraySeq<scala.collection.Map<String, String>> bodyData = (ArraySeq<scala.collection.Map<String, String>>) requestBody;
            //List<HashMap<String,String>> bodyParams = new ArrayList<>();
            String bodyParams = "{\n";
            if (bodyData.size() > 1) {
                for (int i = 0; i < bodyData.size(); i++) {
                    bodyParams = bodyParams + "  \"" + bodyData.apply(i).get("key").get() + "\": \"" + bodyData.apply(i).get("value").get() + "\"";
                    if (i < bodyData.size() - 1) {
                        bodyParams = bodyParams + ",\n";
                    } else {
                        bodyParams = bodyParams + "\n";
                    }

                    //HashMap<String,String> params = new HashMap<>();
                    //params.put("key",bodyData.apply(i).get("key").get());
                    //params.put("value",bodyData.apply(i).get("value").get());
                    //bodyParams.add(params);
                }
                bodyParams = bodyParams + "}";
            }

            String templateFile = map.get("templateFile").get().toString();
            String feederJsonFile = map.get("feederJsonFile").get().toString();
            int status = Integer.parseInt(map.get("status").get().toString());

            //Verify each field listed is present in ResponseBody
            Object responseFields = map.get("responseFields").get();
            ArraySeq<String> respData = (ArraySeq<String>) responseFields;
            //for (int i = 0; i < respData.size(); i++) {
            //    String value = respData.apply(i).toString();
            //}


            switch(request.toLowerCase()) {
                case "authenticate":
                    token = respData.apply(0).toString();
                    ChainBuilder chainAuthenticate =
                                     exec(http(chainName)
                                     .post(baseURL+endpoint)
                                     .header("Accept", acceptHeader)
                                     .header("Content-Type", contentHeader)
                                     .body(StringBody(bodyParams))
                                            .check(status().is(status))
                                            .check(jmesPath(token).saveAs("jwt"+token)))
                                            .pause(2);
                    dynamicChains.add(chainAuthenticate);
                    break;

                case "getall":
                    ChainBuilder chainGetAll =
                                    exec(http(chainName)
                                    .get(baseURL+endpoint)
                                    .header("Accept", acceptHeader)
                                    .header("Content-Type", contentHeader)
                                             .check(status().is(status)))
                                             .pause(2);

                    dynamicChains.add(chainGetAll);
                    break;

                case "getbyid":
                    ChainBuilder chainGetById =
                            exec(http(chainName)
                                    .get(baseURL+endpoint + "")
                                    .header("Accept", acceptHeader)
                                    .header("Content-Type", contentHeader)
                                            .check(status().is(status)))
                                            .pause(2);

                    dynamicChains.add(chainGetById);
                    break;

                case "post":
                    String uid = respData.apply(0).toString();
                    FeederBuilder.FileBased<Object> feederNew = jsonFile(feederJsonFile).random();
                    ChainBuilder chainPost =
                        feed(feederNew)
                            .exec(http(chainName)
                                    .post(baseURL+endpoint)
                                    .header("Accept", acceptHeader)
                                    .header("Content-Type", contentHeader)
                                    .header("Authorization", "Bearer #{jwt"+token+"}")
                                    .body(ElFileBody(templateFile)).asJson()
                                            .check(jmesPath(uid).saveAs("jwt"+uid))
                                            .check(status().is(status))
                                    //.check(status().is(session ->
                                    //        200 + java.util.concurrent.ThreadLocalRandom.current().nextInt(2) // 2
                                    //))
                            ).pause(2);

                    dynamicChains.add(chainPost);
                    break;

                case "put":
                    String uuid = respData.apply(0).toString();
                    FeederBuilder.FileBased<Object> feederUpdate = jsonFile(feederJsonFile).random();
                    ChainBuilder chainPut =
                        feed(feederUpdate)
                            .exec(http(chainName)
                                    .put(baseURL+endpoint)
                                    .header("Accept", acceptHeader)
                                    .header("Content-Type", contentHeader)
                                    .header("Authorization", "Bearer #{jwt"+token+"}")
                                    .body(ElFileBody(templateFile)).asJson()
                                                .check(jmesPath(uuid).saveAs("jwt"+uuid))
                                                .check(status().is(status))
                                        //.check(status().is(session ->
                                        //        200 + java.util.concurrent.ThreadLocalRandom.current().nextInt(2) // 2
                                        //))
                                ).pause(2);

                    dynamicChains.add(chainPut);
                    break;

                case "delete":
                    FeederBuilder.FileBased<Object> feederDelete = jsonFile(feederJsonFile).random();
                    ChainBuilder chainDelete =
                        feed(feederDelete)
                            .exec(http(chainName)
                                    .delete(baseURL+endpoint)
                                    .header("Authorization", "Bearer #{jwt"+token+"}")
                                    .header("Accept", acceptHeader)
                                    .header("Content-Type", contentHeader)
                                            .check(bodyString().is("Video game deleted"))
                    ).pause(2);

                    dynamicChains.add(chainDelete);
                    break;

                default:
                    // code block
            }//end-switch

        }//while


        return scenario(scenarioName)
                .exec(dynamicChains.toArray(new ChainBuilder[0]));
    }

}

//private static FeederBuilder.FileBased<Object> feederNew = jsonFile("feeders/" + ConfigLoader.getInstance().getNewfile()).random();
//private static FeederBuilder.FileBased<Object> feederUpdate = jsonFile("feeders/" + ConfigLoader.getInstance().getUpdatefile()).random();
