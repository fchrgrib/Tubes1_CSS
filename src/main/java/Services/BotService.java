package Services;

import Enums.*;
import Models.*;

import java.util.*;
import java.util.stream.*;


public class BotService {
    private GameObject bot;
    private PlayerAction playerAction;
    private GameState gameState;

    public BotService() {
        this.playerAction = new PlayerAction();
        this.gameState = new GameState();
    }


    public GameObject getBot() {
        return this.bot;
    }

    public void setBot(GameObject bot) {
        this.bot = bot;
    }

    public PlayerAction getPlayerAction() {
        return this.playerAction;
    }

    public void setPlayerAction(PlayerAction playerAction) {
        this.playerAction = playerAction;
    }

    public void computeNextPlayerAction(PlayerAction playerAction) {
        playerAction.action = PlayerActions.FORWARD;


        if (!gameState.getGameObjects().isEmpty()) {
            var foodList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            var enemy = gameState.getPlayerGameObjects()
                    .stream().filter(item -> (item.getGameObjectType()== ObjectTypes.PLAYER)&&(item!=bot) )
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            var wormHole = gameState.getGameObjects()
                    .stream().filter(item -> (item.getGameObjectType()== ObjectTypes.WORMHOLE) )
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            var gasCloud = gameState.getGameObjects()
                    .stream().filter(item -> (item.getGameObjectType()== ObjectTypes.GAS_CLOUD) )
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            var asteroid = gameState.getGameObjects()
                    .stream().filter(item -> (item.getGameObjectType()== ObjectTypes.ASTEROID_FIELD) )
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            var torpedoSalvo = gameState.getGameObjects()
                    .stream().filter(item -> (item.getGameObjectType()== ObjectTypes.TORPEDO_SALVO) )
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            var superFood = gameState.getGameObjects()
                    .stream().filter(item -> (item.getGameObjectType()== ObjectTypes.SUPERFOOD) )
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            var supernovaPickUp = gameState.getGameObjects()
                    .stream().filter(item -> (item.getGameObjectType()== ObjectTypes.SUPERNOVA_PICKUP) )
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            var supernovaBomb = gameState.getGameObjects()
                    .stream().filter(item -> (item.getGameObjectType()== ObjectTypes.SUPERNOVA_BOMB) )
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            var teleporter = gameState.getGameObjects()
                    .stream().filter(item -> (item.getGameObjectType()== ObjectTypes.TELEPORTER) )
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            var shield = gameState.getGameObjects()
                    .stream().filter(item -> (item.getGameObjectType()== ObjectTypes.SHIELD) )
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());


            List<List<GameObject>> listDangerousObject  = new ArrayList<>();


            if(!wormHole.isEmpty()){
                listDangerousObject.add(wormHole);
            }
            if(!gasCloud.isEmpty()){
                listDangerousObject.add(gasCloud);
            }
            if(!asteroid.isEmpty()){
                listDangerousObject.add(asteroid);
            }
            if(!torpedoSalvo.isEmpty()){
                listDangerousObject.add(torpedoSalvo);
            }
            if(!supernovaBomb.isEmpty()){
                listDangerousObject.add(supernovaBomb);
            }
            if(!supernovaPickUp.isEmpty()){
                listDangerousObject.add(supernovaPickUp);
            }
            var nearDangerousObject = getNearDangerousObject(listDangerousObject);
            if(getDistanceBetween(bot,foodList.get(0))>getDistanceBetween(nearDangerousObject,bot)){
                playerAction.action = PlayerActions.FORWARD;
                playerAction.heading = (getHeadingBetween(nearDangerousObject)+90)%360;

            }else {
                playerAction.action = PlayerActions.FORWARD;
                if (getDistanceBetween(bot, foodList.get(0)) > getDistanceBetween(bot, superFood.get(0))) {
                    playerAction.heading = getHeadingBetween(superFood.get(0));
                } else {
                    playerAction.heading = getHeadingBetween(foodList.get(0));
                }
            }




        }

        this.playerAction = playerAction;
    }


    public GameState getGameState() {
        return this.gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        updateSelfState();
    }

    private void updateSelfState() {
        Optional<GameObject> optionalBot = gameState.getPlayerGameObjects().stream().filter(gameObject -> gameObject.id.equals(bot.id)).findAny();
        optionalBot.ifPresent(bot -> this.bot = bot);
    }

    private double getDistanceBetween(GameObject object1, GameObject object2) {
        var triangleX = Math.abs(object1.getPosition().x - object2.getPosition().x);
        var triangleY = Math.abs(object1.getPosition().y - object2.getPosition().y);
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY);
    }

    private int getHeadingBetween(GameObject otherObject) {
        var direction = toDegrees(Math.atan2(otherObject.getPosition().y - bot.getPosition().y,
                otherObject.getPosition().x - bot.getPosition().x));
        return (direction + 360) % 360;
    }

    private int toDegrees(double v) {
        return (int) (v * (180 / Math.PI));
    }

    private GameObject getNearDangerousObject(List<List<GameObject>> listDangerousObject){
        double distance = getDistanceBetween(bot,listDangerousObject.get(0).get(0));
        GameObject ret = listDangerousObject.get(0).get(0);

        for(int i=0;i<listDangerousObject.size();i++){
            if(distance>getDistanceBetween(bot,listDangerousObject.get(i).get(0))){
                ret = listDangerousObject.get(i).get(0);
            }
        }
        return ret;
    }

}