package org.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.example.TransitDAP27;


public class Main {

    public static void main(String[] args) {
        //Main main = new Main();
        TransitDAP27 transitDap27 = new TransitDAP27();
        transitDap27.test();
        transitDap27.testPostDap27();
        System.out.println(transitDap27.getOutPut());
        System.out.println(transitDap27.getJobOutPut());

    }


}