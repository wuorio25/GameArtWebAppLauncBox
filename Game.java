package com.example.servingwebcontent;


import java.time.LocalDateTime;
import java.util.Date;

/**
 * Created by Wuorio on 3/20/2021.
 */
public class Game{
    //constructor
    private Date theLatestTime;
    private String theGameTitle;
    private String theGamePlatform;

    public Game() {
    }

//    public Game(LocalDateTime latestTime, String gameTitle, String gamePlatform) {
//    }

    public void setLatestTime( Date latestTime ) {
        theLatestTime = latestTime;
    }

    public Date getLatestTime( ) {
        return theLatestTime;
    }

    public void setGameTitle( String gameTitle ) {
        theGameTitle = gameTitle;
    }

    public String getGameTitle( ) {
        return theGameTitle;
    }

    public void setGamePlatform( String gamePlatform ) {
        theGamePlatform = gamePlatform;
    }

    public String getGamePlatform( ) {
        return theGamePlatform;
    }

}
