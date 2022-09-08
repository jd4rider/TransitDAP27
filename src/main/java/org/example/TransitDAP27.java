package org.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class TransitDAP27 {
    //Input/Output Variables with Getters and Setters
    private String token;
    private String output = "";
    public String getOutPut() { return output; }
    public void setOutPut(String a) {  this.output = a;   }

    private String joboutput = "";
    public String getJobOutPut() {  return joboutput;   }
    public void setJobOutPut(String a) {   this.joboutput = a;   }

    //Local variables for running job that are set in the Constructor
    private String l_catalogYear;
    private String l_freezeType;
    private String l_reportType;

    private String l_transitUsername = System.getenv("username");
    private String l_transitPin = System.getenv("pin");



    //Constructor
//    public TransitDAP27 (String c_catalogYear, String c_freezeType, String c_reportType) {
//        this.l_catalogYear = c_catalogYear;
//        this.l_freezeType = c_freezeType;
//        this.l_reportType = c_reportType;
//    }

    public void test() {
        getTransitToken();

        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://dwtest.cnm.edu/TransitUI/api/transit/specifications/dap27"))
                .headers("Content-Type", "application/json")
                .headers("Authorization", token)
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept((res) -> {
                    setOutPut(res);
                })
                .join();
    }


    private static String buildRequest (List<String> model, String catalogYear, String freezeType, String reportType) {
        long start = System.currentTimeMillis();
        String majorCode;
        String programCode;
        String degreeCode;
        String pdfInfo;
        int numberOfCopies = 1;

        if (freezeType.equals("AASGEN")) {
            majorCode = "GEN1";
            programCode = "AAS";
            degreeCode = "AAS_GE";

        } else {
            majorCode = "GEN2";
            programCode = "AS";
            degreeCode = "AA_AS_GE";
        }

        String LEVEL_CODE = "00";

        StringBuilder idListBuilder = new StringBuilder();
        StringBuilder levelCodeBuilder = new StringBuilder();
        StringBuilder catalogYearBuilder = new StringBuilder();
        StringBuilder majorCodeBuilder = new StringBuilder();
        StringBuilder programCodeBuilder = new StringBuilder();
        StringBuilder degreeCodeBuilder = new StringBuilder();
        StringBuilder freezeTypeBuilder = new StringBuilder();
        StringBuilder reportTypeBuilder = new StringBuilder();


        levelCodeBuilder.append("\"").append(LEVEL_CODE).append("\"");
        catalogYearBuilder.append("\"").append(catalogYear).append("\"");
        majorCodeBuilder.append("\"").append(majorCode).append("\"");
        programCodeBuilder.append("\"").append(programCode).append("\"");
        degreeCodeBuilder.append("\"").append(degreeCode).append("\"");
        freezeTypeBuilder.append("\"").append(freezeType).append("\"");
        reportTypeBuilder.append("\"").append(reportType).append("\"");

        if (reportType.equals("PDF")) {
            pdfInfo = "\n                ,{" +
                    "\n                    \"id\": \"dap27.pageDimensions\"," +
                    "\n                    \"value\": \"LETTER_PORTRAIT\"" +
                    "\n                 }," +
                    "\n                {" +
                    "\n                    \"id\": \"dap27.createOneFilePerStudent\"," +
                    "\n                    \"value\": \"false\"" +
                    "\n                 }," +
                    "\n                {" +
                    "\n                    \"id\": \"dap27.auditReport\"," +
                    "\n                   \"value\": \"RPT30\"" +
                    "\n                }" ;
        } else {
            pdfInfo = "";
        }



        for (String id : model) {
            idListBuilder.append("\"").append(id).append("\",");
        }
        idListBuilder.setLength(idListBuilder.length() - 1);

        String requestBody = "{\n    " +
                "\"name\": \"DAP27\"," +
                "\n    \"parameters\": [" +
                "\n        {" +
                "\n            \"type\": \"SELECTIDLIST\"," +
                "\n            \"ids\": ["+
                idListBuilder +
                "\n                     ]" +
                "\n        }," +
                "\n        {" +
                "\n           \"type\": \"QUESTIONS\"," +
                "\n           \"answers\": [" +
                "\n                {" +
                "\n                    \"id\": \"dap27.catalogYear\"," +
                "\n                    \"value\": " + catalogYearBuilder +
                "\n                }," +
                "\n                {" +
                "\n                    \"id\": \"dap27.school\"," +
                "\n                    \"value\": " + levelCodeBuilder +
                "\n                }," +
                "\n                {" +
                "\n                    \"id\": \"dap27.degree\"," +
                "\n                    \"value\": " + degreeCodeBuilder +
                "\n                 }," +
                "\n                {" +
                "\n                    \"id\": \"dap27.program\"," +
                "\n                    \"value\": " + programCodeBuilder +
                "\n                 }," +
                "\n                {" +
                "\n                    \"id\": \"dap27.major\"," +
                "\n                    \"value\": " + majorCodeBuilder +
                "\n                 }," +
                "\n                {" +
                "\n                    \"id\": \"dap27.freezeType\"," +
                "\n                    \"value\": " + freezeTypeBuilder +
                "\n                 }," +
                "\n                {" +
                "\n                    \"id\": \"dap27.auditDescription\"," +
                "\n                    \"value\": \"GenEd Audit\"" +
                "\n                 }," +
                "\n                {" +
                "\n                    \"id\": \"dap27.outputType\"," +
                "\n                   \"value\": " + reportTypeBuilder +
                "\n                }" +
                pdfInfo +
                "\n            ]" +
                "\n        } " +
                "\n    ]" +
                "\n}";

        return requestBody;

    };

    private void getTransitToken () {
        String requestBody = "{\"username\" : \""+l_transitUsername+"\", \"password\" : \""+l_transitPin+"\"}";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://dwtest.cnm.edu/TransitUI/api/stateless-token"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .headers("Content-Type", "application/json")
                .headers("Referer", "https://dwtest.cnm.edu/TransitUI")
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept((res) -> {
                    if(res == null || res.isEmpty() || res == " " || res == "") System.out.println("Username or Password is incorrect");
                    else this.token = res.split(":")[1].replaceAll("^\"|\"$", "").replaceAll("}$", "");
                })
                .join();
    }


    public void testPostDap27() {

        List<String> model = new ArrayList<String>();

        model.add("010654717");
        model.add("010011309");

        String requestBody = buildRequest(model, "2020", "AASGEN", "XML");

        System.out.println(requestBody);

        getTransitToken();

        System.out.println(token);


        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://dwtest.cnm.edu/TransitUI/api/transit/instances"))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .headers("Content-Type", "application/json")
                .headers("Authorization", token)
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept((res) -> {
                    setJobOutPut(res);
                })
                .join();

    }


}
