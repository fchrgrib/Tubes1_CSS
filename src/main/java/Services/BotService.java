package Services;

import Enums.*;
import Models.*;

import java.util.*;
import java.util.stream.*;

import com.fasterxml.jackson.databind.node.BooleanNode;

public class BotService {
    private GameObject bot;
    private PlayerAction playerAction;
    private GameState gameState;

    public boolean afterburner = false;
    public int shieldUse = 0;
    public int torpedoItem = 0;
    public int teleporterItem = 100;
    public boolean supernovaItem = false;

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

            // Memasukkkan benda-benda berbahaya ke dalam list
            List<GameObject> listDangerousObject = new ArrayList<>();
            listDangerousObject.addAll(wormHole);
            listDangerousObject.addAll(gasCloud);
            listDangerousObject.addAll(asteroid);
            listDangerousObject.addAll(torpedoSalvo);
            listDangerousObject.addAll(supernovaBomb);

            // Defaultnya adalah bot akan bergerak maju ke arah food atau superfood terdekat
            if (foodList.size() + superFood.size() + supernovaPickUp.size() > 0) {
                playerAction.heading = goToFood(foodList, superFood, bot, supernovaPickUp, listDangerousObject);
            }

            // // Cek apakah kita mempunyai teleporter atau tidak
            // if (bot.teleporterCount > 0) {
            // System.out.println("Teleporter Detected, sizeBot: " + bot.getSize());
            // for (int i = 0; i < teleporter.size(); i++) {
            // // Bayangkan jika kita berada di teleporter
            // GameObject tempBot = bot;
            // // Ubah koordinatnya sama seperti teleporter
            // tempBot.setPosition(teleporter.get(i).getPosition());

            // // Bandingkan apakah ketika teleport lebih sedikit benda berbahaya
            // double distanceTempBotDangerous = getDistanceBetween(
            // getNearDangerousObject(listDangerousObject, tempBot), tempBot);
            // double distanceBotDangerous =
            // getDistanceBetween(getNearDangerousObject(listDangerousObject, bot),
            // bot);

            // if (distanceTempBotDangerous < distanceBotDangerous) {
            // System.out.println("Teleporting");
            // playerAction.heading = goToFood(foodList, superFood, bot, supernovaPickUp,
            // listDangerousObject);
            // playerAction.action = PlayerActions.TELEPORT;
            // updateItemBot();
            // this.playerAction = playerAction;
            // return;
            // }
            // }
            // } else {
            // if ((teleporterItem >= 100) && (bot.getSize() >= 30)) {
            // System.out.println("Teleporter Item Detected");
            // playerAction.action = PlayerActions.FIRETELEPORT;
            // teleporterItem -= 100;
            // updateItemBot();
            // this.playerAction = playerAction;
            // return;

            // }
            // }

            // Jika terdapat torpedo salvonya yang mengincar bot, maka bot akan menghindar
            // atau memasang shield
            if (torpedoSalvo.size() > 0) {
                for (int i = 0; i < torpedoSalvo.size(); i++) {
                    // Apabila jarak torpedo salvo dengan bot kurang dari 20, maka bot akan
                    // menghindar
                    if (getDistanceBetween(bot, torpedoSalvo.get(i)) <= 60) {
                        System.out.println(
                                "Torpedo Salvo Detected, SizeBot: " + bot.getSize() + ", ShieldUse: " + shieldUse);
                        if ((shieldUse == 0) && (bot.getSize() > 40)) {
                            System.out.println("Shield Activated");
                            playerAction.action = PlayerActions.ACTIVATESHIELD;
                            shieldUse++;
                            updateItemBot();
                            this.playerAction = playerAction;
                            return;
                        } else if ((shieldUse > 20) && (bot.getSize() >= 10) && (torpedoItem >= 10)) {
                            // Jika tidak ada shield, maka bot akan menghindar dan menembak torpedo
                            // salvo tersebut
                            playerAction.action = PlayerActions.FIRETORPEDOES;
                            playerAction.heading = getHeadingBetween(torpedoSalvo.get(i));
                            torpedoItem -= 10;
                            updateItemBot();
                            this.playerAction = playerAction;
                            return;
                        }

                    }
                }
            }

            // Jika ada musuh yang berada pada radius tembak, maka bot akan menembak musuh
            // tersebut
            if (enemy.size() > 0) {
                // Cari semua kemungkinan musuh
                for (int i = 0; i < enemy.size(); i++) {
                    // Jika musuh berada dalam radius tembak, maka bot akan menembak musuh tersebut
                    if (isEnemyInRadius(enemy.get(i), bot) && (bot.getSize() >= 10) && (torpedoItem >= 10)) {
                        playerAction.action = PlayerActions.FIRETORPEDOES;
                        playerAction.heading = getHeadingBetween(enemy.get(i));
                        torpedoItem -= 10;
                        break;
                    }
                    // Jika ukuran bot lebih besar dari musuh, maka bot akan mengambil musuh
                    // tersebut
                    if (((bot.getSize() - 20) > (enemy.get(i).getSize()) * 2)) {
                        playerAction.heading = getHeadingBetween(enemy.get(i));
                        if (distancceOfEat(getDistanceBetween(bot, enemy.get(i)))) {
                            playerAction.action = PlayerActions.STARTAFTERBURNER;
                            afterburner = true;
                        } else {
                            if (afterburner) {
                                playerAction.action = PlayerActions.STOPAFTERBURNER;
                                afterburner = false;
                                break;
                            }
                        }

                    }
                }
            }

        }
        updateItemBot();
        this.playerAction = playerAction;
    }

    public GameState getGameState() {
        return this.gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        updateSelfState();
    }

    private void updateItemBot() {
        // Update Shield
        if (shieldUse > 40) {
            shieldUse = 0;
        } else if (shieldUse > 0) {
            shieldUse++;
        }

        // Update Torpedo Salvo
        if (torpedoItem < 50) {
            torpedoItem++;
        }

        if (teleporterItem < 1000) {
            teleporterItem++;
        }

    }

    private void updateSelfState() {
        Optional<GameObject> optionalBot = gameState.getPlayerGameObjects().stream()
                .filter(gameObject -> gameObject.id.equals(bot.id)).findAny();
        optionalBot.ifPresent(bot -> this.bot = bot);
    }

    // Fungsi untuk menentukan berapa sisa dari ukuran torpedo yang tersisa
    // setelah menabrak benda-benda objek sebelum musuh
    // Dengan menggunakan pendekatan persamaan linear dan persamaan lingkaran
    private int isOtherObjectNearTorpedo(GameObject bot, GameObject enemy) {
        // Buat sebuah list yang isinya adalah objek-objek yang berbahaya
        // Objek yang berbahaya untuk torpedo adalah gascloud, asteroid, food, dan
        // superfood
        int torpedoSize = 10;

        List<GameObject> listDangerousObject = new ArrayList<>();
        listDangerousObject.addAll(gameState.getGameObjects().stream()
                .filter(item -> (item.getGameObjectType() == ObjectTypes.GAS_CLOUD)).collect(Collectors.toList()));
        listDangerousObject.addAll(gameState.getGameObjects().stream()
                .filter(item -> (item.getGameObjectType() == ObjectTypes.ASTEROID_FIELD)).collect(Collectors.toList()));
        listDangerousObject.addAll(gameState.getGameObjects().stream()
                .filter(item -> (item.getGameObjectType() == ObjectTypes.FOOD)).collect(Collectors.toList()));
        listDangerousObject.addAll(gameState.getGameObjects().stream()
                .filter(item -> (item.getGameObjectType() == ObjectTypes.SUPERFOOD)).collect(Collectors.toList()));

        // Coba hitung persamaan garis lurus yang dibentuk dari bot ke musuh
        // y = mx + c
        double m = ((double) enemy.getPosition().y - (double) bot.getPosition().y)
                / ((double) enemy.getPosition().x - (double) bot.getPosition().x);
        double c = (double) bot.getPosition().y - (m * (double) bot.getPosition().x);

        // Cari seluruh kemungkinan object yang berada di garis lurus tersebut
        for (int i = 0; i < listDangerousObject.size(); i++) {
            // Tentukan pusat lingkaran pada objek yang berbahaya
            double a = (double) listDangerousObject.get(i).getPosition().x;
            double b = (double) listDangerousObject.get(i).getPosition().y;

            // Tentukan jari-jari lingkaran pada objek yang berbahaya
            double r = (double) listDangerousObject.get(i).getSize();

            // Tentukan titik potong antara garis lurus dan lingkaran
            // Rumusnya adalah x = (-m*c + b +/- sqrt((m^2 + 1) * (c^2 - r^2 + b^2)) / (m^2
            // + 1)
            double jarak = Math.abs((m * a - b + c) / Math.sqrt(m * m + 1));

            // tentukan apakah objek berbahaya berada di garis lurus
            if (jarak <= r) {
                torpedoSize = torpedoSize - listDangerousObject.get(i).getSize();
            }

            if (torpedoSize <= 0) {
                return 0;
            }
        }
        return torpedoSize;

    }

    // Membuat fungsi untuk menghitung apakah terdapat musuh dalam radius tembak
    private boolean isEnemyInRadius(GameObject enemy, GameObject bot) {
        int torpedoSize = isOtherObjectNearTorpedo(bot, enemy);

        double distance = getDistanceBetween(enemy, bot);
        if (distance > 800) {
            return false;
        }
        // Hitung berapa tick yang diperlukan agar torpedo menghantam musuh
        // Kecepatan torpedo adalah 60 pixel per tick
        double timeToEnemy = (distance + ((double) enemy.getSize()) + ((double) bot.getSize())) / 60.0;

        // Hitung posisi musuh setelah beberapa tick
        double enemyX = (((double) enemy.getSpeed()) * timeToEnemy);

        if ((enemyX) < (((double) enemy.getSize()) + torpedoSize)) {
            return true;
        }

        return false;

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
    private int goToFood(List<GameObject> foodList, List<GameObject> superFood, GameObject bot,
            List<GameObject> supernovaPickup, List<GameObject> listDangerousObject) {
        // Awalnya masukkan semua isi foodList, superFood, dan supernovaPickup ke dalam
        // listFood
        List<GameObject> listFood = new ArrayList<GameObject>();
        listFood.addAll(foodList);
        listFood.addAll(superFood);
        listFood.addAll(supernovaPickup);

        // Urutkan listFood berdasarkan jarak terdekat
        listFood = listFood.stream().sorted(Comparator.comparing(item -> getDistanceBetween(bot, item)))
                .collect(Collectors.toList());

        // Jika listFood tidak kosong, maka bot akan menuju food atau superfood terdekat
        if (listFood.size() > 0) {
            // Looping semua kemungkinan dalam listFood
            for (int i = 0; i < listFood.size(); i++) {

                // Jika isi dari ListFood dekat dengan benda berbahaya, maka cari food
                // selanjutnya

                if (getDistanceBetween(listFood.get(i),
                        getNearDangerousObject(listDangerousObject, listFood.get(i))) < ((double) bot
                                .getSize() - 3) * 2) {
                    continue;
                }

                // Jika food berada dekat pada radius batas map, maka cari food selanjutnya

                // Jika isi dari ListFood tidak dekat dengan benda berbahaya, maka
                // Cek apakah food tersebut adalah supernovaPickup dan jarak antara bot dan food
                // tersebut dekat
                if (listFood.get(i).getGameObjectType() == ObjectTypes.SUPERNOVA_PICKUP
                        && getDistanceBetween(bot, listFood.get(i)) < (double) bot.getSpeed()) {
                    // Jika dekat, maka bot akan menuju food tersebut
                    System.out.println("SUPERNOVA PICKUP");
                    supernovaItem = true;
                }

                // Kembalikan heading menuju food tersebut jika tidak ada benda berbahaya
                return getHeadingBetween(listFood.get(i));
            }
        }

        return 0;
    }

    // Fungsi untuk mendapatkan objek yang berbahaya terdekat
    private GameObject getNearDangerousObject(List<GameObject> listDangerousObject, GameObject obj) {
        double distance = getDistanceBetween(obj, listDangerousObject.get(0));
        GameObject ret = listDangerousObject.get(0);

        for (int i = 0; i < listDangerousObject.size(); i++) {
            if (distance > getDistanceBetween(bot, listDangerousObject.get(i))) {
                ret = listDangerousObject.get(i);
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

    private boolean distanceOfShoot(double distance) {
        if (distance < 600 && distance > 100) {
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
        if (distance < 1000) {
            return true;
        } else {
            return false;
        }
    }

}
