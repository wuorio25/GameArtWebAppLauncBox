package com.example.servingwebcontent;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import com.example.servingwebcontent.Game;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.servingwebcontent.*;

@Controller
public class XMLParser {

    @GetMapping("/gameart")
    public String main(@RequestParam(name="gameName", required=false, defaultValue="World") String gameName, Model model) {
        Game theLatestGame = new Game();
        //Get a list of the xml in the directory
        String[] filesToParse = filesToParse();
        //Parse through all XML
        List<Game> latestGamesPerConsole = getLatestGamePerConsole(filesToParse);
        //Then out of all of those systems, find the latest game that was played:
        theLatestGame = getLatestGameOverall(latestGamesPerConsole);

        //values from Game latestGame object:
        String gameTitle = theLatestGame.getGameTitle();
        String gamePlatform = theLatestGame.getGamePlatform();

        //Add the game atributes to Spring for return:
        model.addAttribute("gameName", gameTitle);
        model.addAttribute("gamePlatform", gamePlatform);
        String imageSRC = "H:\\Arcade\\LaunchBox\\Images\\" + gamePlatform + "\\Box - 3D\\" + gameTitle + "-01.png";
        model.addAttribute("imageSRC", imageSRC);

        return "gameart";

    }


    public static List<Game> getLatestGamePerConsole(String[] filesToParse) {
        Map<Date, String> unsortedLastPlayedToGamePerPlatformMap = new HashMap<>();
        List<Date> timeList = new ArrayList<>();
        List<Game> latestPlayedGameObjectList = new ArrayList<>();
        String path = "H:\\Arcade\\LaunchBox\\Data\\Platforms\\";
        if(filesToParse != null){
            for (int h = 0; h < filesToParse.length; h++) {
                if(filesToParse[h].contains("MAME")) {
                    try {

                        File inputFile = new File(path + filesToParse[h]);
                        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                        Document doc = dBuilder.parse(inputFile);
                        doc.getDocumentElement().normalize();
                        NodeList gameList = doc.getElementsByTagName("Game");

                        for (int i = 0; i < gameList.getLength(); i++) {

                            Node currentNode = gameList.item(i);

                            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {

                                Element eElement = (Element) currentNode;
                                String applicationPath = eElement.getElementsByTagName("ApplicationPath").item(0).getTextContent();
                                String lastPlayed = "";
                                if (eElement.getElementsByTagName("LastPlayedDate").item(0) != null) {
                                    lastPlayed = eElement.getElementsByTagName("LastPlayedDate").item(0).getTextContent();

                                }
                                if (!lastPlayed.isEmpty()) {
                                    String dateTime = lastPlayed.split("\\.")[0];
                                    String pattern = "yyyy-MM-dd'T'HH:mm:ss";
                                    DateFormat df = new SimpleDateFormat(pattern);
                                    Date lastPlayedDateTime = df.parse(dateTime);
                                    if (lastPlayedDateTime != null) {
                                        timeList.add(lastPlayedDateTime);
                                        unsortedLastPlayedToGamePerPlatformMap.put(lastPlayedDateTime, applicationPath);
                                    }
                                }
                            }
                        }
                        String latestGame = "";
                        if (!timeList.isEmpty()) {
                            Collections.sort(timeList, Collections.reverseOrder());
                            Date latestTime = timeList.get(0);
                            latestGame = unsortedLastPlayedToGamePerPlatformMap.get(latestTime);
                            System.out.println("latestGame " + latestGame);

                        }
                        for (int j = 0; j < gameList.getLength(); j++) {
                            Node currentNode = gameList.item(j);
                            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element eElement = (Element) currentNode;
                                String applicationPath = eElement.getElementsByTagName("ApplicationPath").item(0).getTextContent();
                                if (applicationPath == latestGame) {
                                    Game gameObject = new Game();
                                    Date lastPlayedDateTime = null;
                                    String lastPlayed = eElement.getElementsByTagName("LastPlayedDate").item(0).getTextContent();
                                    if (!lastPlayed.isEmpty()) {
                                        String dateTime = lastPlayed.split("\\.")[0];
                                        String pattern = "yyyy-MM-dd'T'HH:mm:ss";
                                        DateFormat df = new SimpleDateFormat(pattern);
                                        lastPlayedDateTime = df.parse(dateTime);
                                    }
                                    String gameTitle = eElement.getElementsByTagName("Title").item(0).getTextContent();
                                    String gamePlatform = eElement.getElementsByTagName("Platform").item(0).getTextContent();
                                    gameObject.setLatestTime(lastPlayedDateTime);
                                    gameObject.setGameTitle(gameTitle);
                                    gameObject.setGamePlatform(gamePlatform);
                                    latestPlayedGameObjectList.add(gameObject);
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return latestPlayedGameObjectList;
    }

    public static Game getLatestGameOverall(List<Game> latestGamesPerConsole) {
        Date latestTimeForLatestGame = null;
        List<Date> timeList = new ArrayList<>();
        for (Game game : latestGamesPerConsole) {
            timeList.add(game.getLatestTime());
        }
        if (!timeList.isEmpty()) {
            Collections.sort(timeList, Collections.reverseOrder());
            latestTimeForLatestGame = timeList.get(0);
        }
        for (Game game : latestGamesPerConsole) {
            if (game.getLatestTime() == latestTimeForLatestGame) {
                return game;
            }
        }
        return null;
    }

    public static String[] filesToParse() {
        String[] pathnames;

        // Creates a new File instance by converting the given pathname string into an abstract pathname
        File f = new File("H:\\Arcade\\LaunchBox\\Data\\Platforms");

        // Populates the array with names of files and directories
        pathnames = f.list();

        return pathnames;
    }
}
