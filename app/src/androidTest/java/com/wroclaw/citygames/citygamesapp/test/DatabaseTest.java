package com.wroclaw.citygames.citygamesapp.test;

import android.database.Cursor;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.util.Log;

import com.wroclaw.citygames.citygamesapp.dao.GameDao;
import com.wroclaw.citygames.citygamesapp.dao.ScenarioDao;
import com.wroclaw.citygames.citygamesapp.dao.TeamDao;
import com.wroclaw.citygames.citygamesapp.database.DatabaseHelper;
import com.wroclaw.citygames.citygamesapp.database.GameTable;
import com.wroclaw.citygames.citygamesapp.database.ScenarioTable;
import com.wroclaw.citygames.citygamesapp.database.TeamTable;
import com.wroclaw.citygames.citygamesapp.model.Game;
import com.wroclaw.citygames.citygamesapp.model.Scenario;
import com.wroclaw.citygames.citygamesapp.model.Team;

public class DatabaseTest extends AndroidTestCase {
    private DatabaseHelper db;
    private TeamDao teamDao;
    private ScenarioDao scenarioDao;
    private GameDao gameDao;
    @Override
    public void setUp() throws Exception {
        super.setUp();
        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), "test_");
        db = new DatabaseHelper(context);
        teamDao = TeamDao.getInstance(db);
        scenarioDao = ScenarioDao.getInstance(db);
        gameDao = GameDao.getInstance(db);
    }

    @Override
    public void tearDown() throws Exception {
        db.close();
        super.tearDown();
    }

    public void testDBCreated(){
        assertTrue(db.getReadableDatabase().isDatabaseIntegrityOk());

        Cursor c = db.getReadableDatabase().query(TeamTable.TABLE_NAME,TeamTable.ALL_COLUMNS,null,null,null,null,null);
        assertNotNull(c);
        assertFalse(c.moveToNext());

        c = db.getReadableDatabase().query(ScenarioTable.TABLE_NAME,ScenarioTable.ALL_COLUMNS,null,null,null,null,null);
        assertNotNull(c);
        assertFalse(c.moveToNext());

        c = db.getReadableDatabase().query(GameTable.TABLE_NAME,GameTable.ALL_COLUMNS,null,null,null,null,null);
        assertNotNull(c);
        assertFalse(c.moveToNext());

        assertNotNull(teamDao);
        assertNotNull(scenarioDao);
        assertNotNull(gameDao);
    }

    public void testInsertTeam(){
        Team team = new Team();
        Long id = teamDao.save(team);
        assertNotNull(id);
        assertTrue(id != -1);
    }

    public void testGetTeam(){
        Team team = new Team();
        team.setPassword("pass");
        team.setName("name");
        Long id = teamDao.save(team);
        assertNotNull(id);
        assertTrue(id != -1);

        team = teamDao.get(id);
        assertNotNull(team);
        assertTrue(team.getName().equals("name"));
        assertTrue(team.getPassword().equals("pass"));
        assertTrue(team.getTeamId() == id);
    }

    public void testGetTeamNull(){
        Team team = teamDao.get(100);
        assertNull(team);
    }

    public void testGetTeamDuplicateId(){
        Team team = new Team();
        team.setTeamId((long) 10);
        Long id = teamDao.save(team);
        assertTrue(id==10);

        id = teamDao.save(team);
        assertTrue(id == -1);
    }

    public void testUpdateTeam(){
        Team team = new Team();
        team.setName("name");
        Long id = teamDao.save(team);
        assertNotNull(id);
        assertTrue(id != -1);
        team.setTeamId(id);
        team.setName("name2");
        teamDao.update(team);

        team = teamDao.get(id);
        assertNotNull(team);
        assertTrue(team.getName().equals("name2"));
    }

    public void testDeleteTeam(){
        Team team = new Team();
        Long id = teamDao.save(team);
        assertNotNull(id);
        assertTrue(id != -1);
        team.setTeamId(id);
        teamDao.delete(team);
        team = teamDao.get(id);
        assertNull(team);
    }

    public void testDeleteAllTeam(){
        Team team = new Team();
        teamDao.save(team);
        team = new Team();
        teamDao.save(team);
        team = new Team();
        teamDao.save(team);

        assertTrue(teamDao.getAll().size() == 3);
        teamDao.deleteAll();
        assertTrue(teamDao.getAll().size() == 0);
    }

    public void testInsertScenario(){
        Scenario scenario = new Scenario();
        scenario.setScenarioId((long) 1);
        Long id = scenarioDao.save(scenario);
        assertNotNull(id);
        Log.d("testy", String.valueOf(id));
        assertTrue(id != -1);
    }

    public void testGetScenarioNull(){
        Scenario scenario = scenarioDao.get(100);
        assertNull(scenario);
    }

   public void testGetScenarioDuplicateId(){
        Scenario scenario = new Scenario();
        scenario.setScenarioId((long) 10);
        Long id = scenarioDao.save(scenario);
        assertTrue(id == 10);

        id = scenarioDao.save(scenario);
        assertTrue(id == -1);
    }

    public void testGetScenario(){
        Scenario scenario = new Scenario();
        scenario.setScenarioId((long) 2);
        scenario.setDistanceKm((float) 1.03);
        scenario.setName("name");
        Long id = scenarioDao.save(scenario);
        assertNotNull(id);
        assertTrue(id != -1);

        scenario = scenarioDao.get(id);
        assertNotNull(scenario);
        assertTrue(scenario.getName().equals("name"));
        assertTrue(scenario.getDistanceKm()==(float) 1.03);
        assertTrue(scenario.getScenarioId()==id);
    }

    public void testUpdateScenario(){
        Scenario scenario = new Scenario();
        scenario.setName("name");
        Long id = scenarioDao.save(scenario);
        assertNotNull(id);
        assertTrue(id != -1);
        scenario.setScenarioId(id);
        scenario.setName("name2");
        scenarioDao.update(scenario);

        scenario = scenarioDao.get(id);
        assertNotNull(scenario);
        assertTrue(scenario.getName().equals("name2"));
    }

    public void testDeleteScenario(){
        Scenario scenario = new Scenario();
        Long id = scenarioDao.save(scenario);
        assertNotNull(id);
        assertTrue(id != -1);
        scenario.setScenarioId(id);
        scenarioDao.delete(scenario);
        scenario = scenarioDao.get(id);
        assertNull(scenario);
    }

    public void testDeleteAllScenario(){
        scenarioDao.deleteAll();
        assertTrue(scenarioDao.getAll().size()==0);
        Scenario scenario = new Scenario();
        scenarioDao.save(scenario);
        scenario = new Scenario();
        scenarioDao.save(scenario);
        scenario = new Scenario();
        scenarioDao.save(scenario);

        assertTrue(scenarioDao.getAll().size() == 3);
        scenarioDao.deleteAll();
        assertTrue(scenarioDao.getAll().size() == 0);
    }

    public void testInsertGame(){
        Team team = new Team();
        Long teamId = teamDao.save(team);
        assertNotNull(teamId);
        assertTrue(teamId>0);
        Scenario scenario = new Scenario();

        Long scenarioId = scenarioDao.save(scenario);
        scenario.setScenarioId(scenarioId);
        assertNotNull(scenarioId);
        assertTrue(scenarioId > 0);

        Game game = new Game();
        game.setTeamId(teamId);
        game.setScenario(scenario);
        Long gameId = gameDao.save(game);
        assertNotNull(gameId);
        assertTrue(gameId > 0);

        Game game1  = new Game();
        Long id = gameDao.save(game1);
        assertNotNull(id);
        assertTrue(id == -1);

        game.setGameId(gameId);
        gameId = gameDao.save(game);
        assertNotNull(gameId);
        assertTrue(gameId==-1);
    }

    public void testReadGame(){
        int count = gameDao.getAll().size();
        Team team = new Team();
        Long teamId = teamDao.save(team);
        Scenario scenario = new Scenario();
        Long scenarioId = scenarioDao.save(scenario);
        scenario.setScenarioId(scenarioId);

        Game game = new Game();
        game.setTeamId(teamId);
        game.setScenario(scenario);
        Long gameId = gameDao.save(game);
        assertNotNull(gameId);
        assertTrue(gameId > 0);
        assertTrue(gameDao.getAll().size() == count + 1);

        Game test = gameDao.get(0);
        assertNull(test);

        test = gameDao.get(gameId);
        assertNotNull(test);
        assertTrue(test.getGameId() == gameId);
    }

    public void testUpdateGame(){
        int count = gameDao.getAll().size();
        Team team = new Team();
        Long teamId = teamDao.save(team);
        Scenario scenario = new Scenario();
        Long scenarioId = scenarioDao.save(scenario);
        scenario.setScenarioId(scenarioId);

        Game game = new Game();
        game.setTeamId(teamId);
        game.setScenario(scenario);
        Long gameId = gameDao.save(game);
        assertNotNull(gameId);
        assertTrue(gameId > 0);
        assertTrue(gameDao.getAll().size() == count + 1);
        game.setGameId(gameId);

        game.setPoints(100);
        gameDao.update(game);

        Game test = gameDao.get(gameId);
        assertNotNull(test);
        assertTrue(test.getGameId() == gameId);
        assertTrue(test.getPoints()==game.getPoints());


    }
    public void testRemoveGame(){
        int count = gameDao.getAll().size();
        Team team = new Team();
        Long teamId = teamDao.save(team);
        Scenario scenario = new Scenario();
        Long scenarioId = scenarioDao.save(scenario);
        scenario.setScenarioId(scenarioId);

        Game game = new Game();
        game.setTeamId(teamId);
        game.setScenario(scenario);
        Long gameId = gameDao.save(game);
        assertNotNull(gameId);
        assertTrue(gameId > 0);
        assertTrue(gameDao.getAll().size() == count + 1);
        game.setGameId(gameId);

        gameDao.delete(null);
        assertTrue(gameDao.getAll().size() == count + 1);

        gameDao.delete(new Game());
        assertTrue(gameDao.getAll().size() == count + 1);

        gameDao.delete(game);
        assertTrue(gameDao.getAll().size() == count);

        gameDao.deleteAll();
        assertTrue(gameDao.getAll().size() == 0);
    }

}