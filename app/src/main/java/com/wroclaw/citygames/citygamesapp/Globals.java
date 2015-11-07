package com.wroclaw.citygames.citygamesapp;


public interface Globals {
    String MAIN_URL="192.168.0.48:8080/CityGamesServer/citygames";
    //String MAIN_URL = "192.168.1.122:8080/CityGamesServer/citygames";
    String LOGIN_URI = "login";
    String REGISTER_URI = "register";
    String SCENARIOS_URI = "scenarios";
    String COLLECT_DATA_URI = "collect_data";
    String SIGNIN_URI = "signin";
    String SIGNOUT_URI = "signout";
    String CREATE_TEAM_URI = "create_team";
    String GAMEPLAY_URI = "gameplay";
    String REGISTER_GAME_URI = "new_game";
    String GET_FINISHED_GAME_URI = "get_game";
    String IMAGE_DIRECTORY_PATH ="imageDir";
    String RANKING_BEST20_URI = "ranking/best20";
    String RANKING_GET_NEXT_10 = "ranking/get_next";
    String PROJECT_NUMBER = "58592866754";
    String CREDENTIALS = "CREDENTIALS";

    int FINISH_TASK = -300;
    int CHOICE_TASK = -100;
    int SYNC_TASK = -200;
    int UPDATE_TASK = -400;
}
