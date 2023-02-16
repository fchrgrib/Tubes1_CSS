package Services;

import Enums.*;
import Models.*;

import java.util.*;
import java.util.stream.*;

public class BotService {
    private GameObject bot;
    private PlayerAction playerAction;
    private GameState gameState;

    public boolean afterburner = false;
    public int shieldUse = 0;

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

    // Prosedur untuk menentukan aksi bot
    public void computeNextPlayerAction(PlayerAction playerAction) {
        playerAction.action = PlayerActions.FORWARD; // Aksi maju DEFAULT
        playerAction.heading = new Random().nextInt(360);

        if (!gameState.getGameObjects().isEmpty()) {
            var foodList = gameState.getGameObjects()
                    .stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD)
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            var enemy = gameState.getPlayerGameObjects()
                    .stream().filter(item -> (item.getGameObjectType() == ObjectTypes.PLAYER) && (item != bot))
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            var wormHole = gameState.getGameObjects()
                    .stream().filter(item -> (item.getGameObjectType() == ObjectTypes.WORMHOLE))
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            var gasCloud = gameState.getGameObjects()
                    .stream().filter(item -> (item.getGameObjectType() == ObjectTypes.GAS_CLOUD))
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            var asteroid = gameState.getGameObjects()
                    .stream().filter(item -> (item.getGameObjectType() == ObjectTypes.ASTEROID_FIELD))
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            var torpedoSalvo = gameState.getGameObjects()
                    .stream().filter(item -> (item.getGameObjectType() == ObjectTypes.TORPEDO_SALVO))
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            var superFood = gameState.getGameObjects()
                    .stream().filter(item -> (item.getGameObjectType() == ObjectTypes.SUPERFOOD))
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            var supernovaPickUp = gameState.getGameObjects()
                    .stream().filter(item -> (item.getGameObjectType() == ObjectTypes.SUPERNOVA_PICKUP))
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            var supernovaBomb = gameState.getGameObjects()
                    .stream().filter(item -> (item.getGameObjectType() == ObjectTypes.SUPERNOVA_BOMB))
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            var teleporter = gameState.getGameObjects()
                    .stream().filter(item -> (item.getGameObjectType() == ObjectTypes.TELEPORTER))
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            var shield = gameState.getGameObjects()
                    .stream().filter(item -> (item.getGameObjectType() == ObjectTypes.SHIELD))
                    .sorted(Comparator
                            .comparing(item -> getDistanceBetween(bot, item)))
                    .collect(Collectors.toList());

            List<List<GameObject>> listDangerousObject = new ArrayList<>(); //

            if (!wormHole.isEmpty()) {
                listDangerousObject.add(wormHole);
            }
            if (!gasCloud.isEmpty()) {
                listDangerousObject.add(gasCloud);
            }
            if (!asteroid.isEmpty()) {
                listDangerousObject.add(asteroid);
            }
            if (!torpedoSalvo.isEmpty()) {
                listDangerousObject.add(torpedoSalvo);
            }
            if (!supernovaBomb.isEmpty()) {
                listDangerousObject.add(supernovaBomb);
            }

            var nearDangerousObject = getNearDangerousObject(listDangerousObject);
            // Jika objek berbahaya yang paling dekat berjarak kurang dari 27
            if (getDistanceBetween(bot, nearDangerousObject) < 27) {
                // Cari objek berbahaya kedua yang paling dekat
                GameObject n2 = getNearSecondDangerousObject(listDangerousObject, nearDangerousObject);

                // Jika jarak antara objek berbahaya pertama dan kedua kurang dari 10
                if ((getDistanceBetween(bot, n2) - getDistanceBetween(bot, nearDangerousObject)) < 10) {
                    playerAction.action = PlayerActions.FORWARD;
                    playerAction.heading = goToFood(foodList, superFood, bot);
                } else {
                    // Jika object berbahaya tersebut adalah torpedo salvo maka aktifkan pelindung
                    if (isDangerousTorpedoSalvo(bot, nearDangerousObject) && (shieldUse == 0)) {
                        playerAction.action = PlayerActions.ACTIVATESHIELD;
                        shieldUse = 1;
                    } else {
                        playerAction.action = PlayerActions.FORWARD; // Gerak maju
                    }

                    // Jika sudut objek berbahaya pertama yang paling dekat kurang dari 90 derajat
                    if ((getHeadingBetween(nearDangerousObject) <= 90)) {
                        playerAction.heading = (getHeadingBetween(nearDangerousObject) + 60) % 360; // Mengatur // sudut
                        System.out.println("thos");
                    }

                    // Jika sudut objek berbahaya pertama yang paling dekat lebih dari 90 derajat
                    if ((getHeadingBetween(nearDangerousObject) > 90)
                            && (getHeadingBetween(nearDangerousObject) < 180)) {
                        playerAction.heading = ((getHeadingBetween(nearDangerousObject) - 60) + 360) % 360;
                        System.out.println("this");
                    }
                }

            } else {
                if (enemy.get(0).size > bot.size) { // Jika musuh lebih besar dari bot
                    if (enemy.size() > 1
                            && ((getDistanceBetween(bot, enemy.get(0)) - getDistanceBetween(bot, enemy.get(1))) < 20)
                            && (enemy.get(1).size > bot.size)) {
                        playerAction.action = PlayerActions.FORWARD;
                        playerAction.heading = goToFood(foodList, superFood, bot);

                    } else {
                        if (enemy.get(0).size > 1.5 * bot.size) { // Jika musuh lebih besar dari 1.5 kali bot
                            if (distanceOfBigBigerShoot(getDistanceBetween(bot, enemy.get(0)))) {
                                playerAction.action = PlayerActions.FIRETORPEDOES; // Menembakkan torpedo
                                playerAction.heading = getHeadingBetween(enemy.get(0));
                            } else {

                                playerAction.action = PlayerActions.FORWARD;
                                playerAction.heading = goToFood(foodList, superFood, bot);
                            }
                        } else {
                            if (getDistanceBetween(bot, enemy.get(0)) < 71) { // Jika jarak musuh kurang dari 71
                                if ((getHeadingBetween(enemy.get(0)) <= 90)) {
                                    playerAction.action = PlayerActions.FORWARD;
                                    playerAction.heading = (getHeadingBetween(enemy.get(0)) + 90) % 360;
                                    System.out.println("thus");
                                }
                                if ((getHeadingBetween(enemy.get(0)) > 90)
                                        && (getHeadingBetween(enemy.get(0)) < 180)) {
                                    playerAction.action = PlayerActions.FORWARD;
                                    playerAction.heading = getHeadingBetween(enemy.get(0)) - 90;
                                    System.out.println("thas");
                                }
                            } else { // Jika jarak musuh lebih dari 71
                                if (distanceOfBigShoot(getDistanceBetween(bot, enemy.get(0)))) { // 100
                                    playerAction.action = PlayerActions.FIRETORPEDOES;
                                    playerAction.heading = getHeadingBetween(enemy.get(0));
                                } else {
                                    playerAction.action = PlayerActions.FORWARD;
                                    playerAction.heading = goToFood(foodList, superFood, bot);
                                }
                            }
                        }
                    }

                } else { // Jika musuh lebih kecil dari bot
                    if (distancceOfEat(getDistanceBetween(bot, enemy.get(0)))) { // Jika jarak musuh kurang dari 50
                        playerAction.action = PlayerActions.FORWARD;
                        playerAction.heading = getHeadingBetween(enemy.get(0));
                    } else if (distanceOfShoot(getDistanceBetween(bot, enemy.get(0)))) { //
                        if (getHeadingBetween(enemy.get(0)) == getHeadingBetween(asteroid.get(0))) {
                            playerAction.action = PlayerActions.FORWARD;

                            if (getDistanceBetween(bot, foodList.get(0)) > getDistanceBetween(bot,
                                    superFood.get(0))) {
                                playerAction.heading = getHeadingBetween(superFood.get(0));
                            } else {
                                playerAction.heading = getHeadingBetween(foodList.get(0));
                            }
                        } else {
                            if ((enemy.get(0).size * 1.2 < bot.size) && (enemy.size() == 1)) {
                                playerAction.action = PlayerActions.FORWARD;
                                playerAction.heading = getHeadingBetween(enemy.get(0));
                            } else {
                                if (!distanceOfBigShoot(getDistanceBetween(bot, enemy.get(0)))) {
                                    playerAction.action = PlayerActions.FORWARD;

                                    if (getDistanceBetween(bot, foodList.get(0)) > getDistanceBetween(bot,
                                            superFood.get(0))) {
                                        playerAction.heading = getHeadingBetween(superFood.get(0));
                                    } else {
                                        playerAction.heading = getHeadingBetween(foodList.get(0));
                                    }
                                } else {
                                    playerAction.action = PlayerActions.FIRETORPEDOES;
                                    playerAction.heading = getHeadingBetween(enemy.get(0));
                                    System.out.println("nembak aman");
                                }
                            }
                        }
                    } else if (getDistanceBetween(bot, enemy.get(0)) >= 600) {
                        playerAction.action = PlayerActions.FORWARD;

                        if (getDistanceBetween(bot, foodList.get(0)) > getDistanceBetween(bot, superFood.get(0))) {
                            playerAction.heading = getHeadingBetween(superFood.get(0));
                        } else {
                            playerAction.heading = getHeadingBetween(foodList.get(0));
                        }
                    }

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
        Optional<GameObject> optionalBot = gameState.getPlayerGameObjects().stream()
                .filter(gameObject -> gameObject.id.equals(bot.id)).findAny();
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

    // Prosedur untuk menuju food atau superfood
    private int goToFood(List<GameObject> foodList, List<GameObject> superFood, GameObject bot) {
        int heading = 0;
        if (getDistanceBetween(bot, foodList.get(0)) > getDistanceBetween(bot, superFood.get(0))) {
            heading = getHeadingBetween(superFood.get(0));
        } else {
            heading = getHeadingBetween(foodList.get(0));
        }
        return heading;
    }

    // Fungsi untuk mendapatkan objek yang berbahaya terdekat
    private GameObject getNearDangerousObject(List<List<GameObject>> listDangerousObject) {
        double distance = getDistanceBetween(bot, listDangerousObject.get(0).get(0));
        GameObject ret = listDangerousObject.get(0).get(0);

        for (int i = 0; i < listDangerousObject.size(); i++) {
            if (distance > getDistanceBetween(bot, listDangerousObject.get(i).get(0))) {
                ret = listDangerousObject.get(i).get(0);
            }
        }
        return ret;
    }

    private GameObject getNearSecondDangerousObject(List<List<GameObject>> listDangerousObject,
            GameObject firstDangerousObject) {
        double distance = getDistanceBetween(bot, listDangerousObject.get(0).get(0));
        GameObject ret = listDangerousObject.get(0).get(0);

        for (int i = 0; i < listDangerousObject.size(); i++) {
            if (distance > getDistanceBetween(bot, listDangerousObject.get(i).get(0))
                    && listDangerousObject.get(i).get(0) != firstDangerousObject) {
                ret = listDangerousObject.get(i).get(0);
            }
        }
        return ret;
    }

    private boolean isGoToSupernovaPickup(GameObject bot, List<GameObject> supernovaPickup, List<GameObject> enemy) {
        double distance = getDistanceBetween(bot, supernovaPickup.get(0));
        double distanceEnemy = getDistanceBetween(supernovaPickup.get(0), enemy.get(0));
        if ((distance < distanceEnemy) && (distance < 200)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isDangerousTorpedoSalvo(GameObject bot, GameObject dangerousObject) {
        // Cek apakah object game tersebut adalah torpedo salvo
        if (dangerousObject.gameObjectType == ObjectTypes.TORPEDO_SALVO) {
            // Cek apakah torpedo salvo tersebut berada di dekat bot
            if (getDistanceBetween(bot, dangerousObject) < 5) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private boolean distanceOfShoot(double distance) {
        if (distance < 600 && distance > 100) {
            return true;
        } else {
            return false;
        }
    }

    private boolean distanceOfBigShoot(double distance) {
        if (distance < 400) {
            return true;
        } else {
            return false;
        }
    }

    private boolean distancceOfEat(double distance) {
        if (distance <= 100) {
            return true;
        } else {
            return false;
        }
    }

    private boolean distanceOfBigBigerShoot(double distance) {
        if (distance < 800) {
            return true;
        } else {
            return false;
        }
    }
}
