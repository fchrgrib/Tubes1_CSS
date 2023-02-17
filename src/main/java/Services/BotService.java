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

    public List<GameObject> listTeleporter = new ArrayList<>();

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
        // Buat heading default berdasarkan titik tengah
        playerAction.heading = getHeadingToCenterPoint();

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

            var supernovaBomb = gameState.getGameObjects()
                    .stream().filter(item -> (item.getGameObjectType() == ObjectTypes.SUPERNOVA_BOMB))
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

            // Hapus semua elemen list teleporter yang berada pada luar radius world
            // Menggunakan while
            int idx = 0;
            while (idx < listTeleporter.size()) {
                GameObject teleporter = listTeleporter.get(idx);
                if (getDistanceFromCenter(teleporter) > gameState.world.radius) {
                    listTeleporter.remove(idx);
                } else {
                    idx++;
                }
            }

            // Defaultnya adalah bot akan bergerak maju ke arah food atau superfood terdekat
            int heading = 0;
            if (foodList.size() + superFood.size() > 0) {
                heading = goToFood(foodList, superFood, bot, listDangerousObject);
            }

            if (heading > -1) {
                playerAction.heading = heading;
            }

            // Cek apakah terdapat teleporter
            if (listTeleporter.size() > 0) {
                // Ambil teleporter pertama yang diluncurkan
                GameObject teleporter = listTeleporter.get(0);
                // Jika terdapat teleporter yang jika diambil, maka bot akan memakan enemy yang
                // berada pada sekitarnya
                GameObject tempBot = bot;
                tempBot.setPosition(teleporter.getPosition());

                // Cek apakah terdapat enemy yang lebih kecil dari tempBot dan bot teresebut
                // menyentuh atau berada pada radius bot
                for (int i = 0; i < enemy.size(); i++) {
                    if ((enemy.get(i).getSize() < tempBot.getSize())
                            && (getDistanceBetween(tempBot, enemy.get(i)) <= 0)) {
                        // Ambil teleporter
                        playerAction.action = PlayerActions.TELEPORT;
                        playerAction.heading = getHeadingBetween(teleporter);
                        updateItemBot();
                        // Hapus elemen teleporter ini dari list
                        listTeleporter.remove(0);
                        this.playerAction = playerAction;
                        return;
                    }
                }

            }

            // Jika terdapat torpedo salvonya yang mengincar bot, maka bot akan menghindar
            // atau memasang shield
            if (torpedoSalvo.size() > 0) {
                for (int i = 0; i < torpedoSalvo.size(); i++) {
                    // Apabila jarak torpedo salvo dengan bot kurang dari 200 dan torpedo salvo akan
                    // mengenai, maka bot akan
                    // menghindar (cara mengecek apakah torpedo salvo akan mengenai adalah dengan )
                    if ((getDistanceBetween(bot, torpedoSalvo.get(i)) <= 200)
                            && (isTorpedoHeadingDangerous(bot, torpedoSalvo.get(i)))) {
                        System.out.println(
                                "Torpedo Salvo Detected, SizeBot: " + bot.getSize() + ", ShieldUse: " + shieldUse);
                        if ((shieldUse == 0) && (bot.getSize() > 30)) {
                            System.out.println("Shield Activated");
                            playerAction.action = PlayerActions.ACTIVATESHIELD;
                            shieldUse++;
                            updateItemBot();
                            this.playerAction = playerAction;
                            return;
                        } else if ((shieldUse > 20) && (bot.getSize() >= 20) && (torpedoItem >= 10)) {
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
            if (enemy.size() > 1) {
                int idxMin = 0;

                // Cari semua kemungkinan musuh
                for (int i = 0; i < enemy.size(); i++) {
                    // Jika musuh berada dalam radius tembak, maka bot akan menembak musuh tersebut
                    if (isEnemyInRadius(enemy.get(i), bot) && (bot.getSize() >= 20) && (torpedoItem >= 10)) {
                        // Deteksi apakah musuh tersebut memakai shield atau tidak

                        // Cari di dalam list shield, apakah posisinya sama dengan musuh yang kita
                        // targetkan
                        boolean isShield = false;
                        for (int j = 0; j < shield.size(); j++) {
                            if (shield.get(j).getPosition().equals(enemy.get(i).getPosition())) {
                                // Jika sama maka langsung batal tembak
                                isShield = true;
                            }
                        }

                        // Jika tidak ada shield, maka bot akan menembak musuh tersebut
                        if (!isShield) {
                            System.out.println("Enemy Detected, SizeBot: " + bot.getSize() + ", Torpedo Item: "
                                    + torpedoItem + ", Enemy Size: " + enemy.get(i).getSize());
                            playerAction.action = PlayerActions.FIRETORPEDOES;
                            playerAction.heading = getHeadingBetween(enemy.get(i));
                            torpedoItem -= 10;
                            updateItemBot();
                            this.playerAction = playerAction;
                            return;
                        }
                    }

                    if (enemy.get(i).getSize() < enemy.get(idxMin).getSize()) {
                        idxMin = i;
                    }
                }

                // Jika terdapat musuh yang lebih kecil dari bot, maka bot menembakkan
                // teleporter
                if ((bot.getSize() > enemy.get(idxMin).getSize()) && (bot.getSize() >= 20) && (teleporterItem >= 100)) {
                    playerAction.action = PlayerActions.FIRETELEPORT;
                    playerAction.heading = getHeadingBetween(enemy.get(idxMin));
                    teleporterItem -= 100;
                    updateItemBot();
                    this.playerAction = playerAction;
                    return;
                }

            } else if (enemy.size() == 1) {
                int i = 0;
                if ((((bot.getSize() - 20) > (enemy.get(i).getSize()) * 2
                        + (int) getDistanceBetween(bot, enemy.get(i)))) && (bot.getSize() > 50)) {
                    playerAction.heading = getHeadingBetween(enemy.get(i));
                    if (distancceOfEat(getDistanceBetween(bot, enemy.get(i)))) {
                        playerAction.action = PlayerActions.STARTAFTERBURNER;
                        afterburner = true;
                    } else {
                        if (afterburner) {
                            playerAction.action = PlayerActions.STOPAFTERBURNER;
                            afterburner = false;
                            updateItemBot();
                            this.playerAction = playerAction;
                            return;
                        }
                    }

                } else {
                    if (afterburner) {
                        playerAction.action = PlayerActions.STOPAFTERBURNER;
                        afterburner = false;
                        updateItemBot();
                        this.playerAction = playerAction;
                        return;
                    }

                    // Jika kita dapat menembak musuh
                    if (isEnemyInRadius(enemy.get(i), bot) && (bot.getSize() >= 20) && (torpedoItem >= 10)) {
                        // Deteksi apakah musuh tersebut memakai shield atau tidak

                        // Cari di dalam list shield, apakah posisinya sama dengan musuh yang kita
                        // targetkan
                        boolean isShield = false;
                        for (int j = 0; j < shield.size(); j++) {
                            if (shield.get(j).getPosition().equals(enemy.get(i).getPosition())) {
                                // Jika sama maka langsung batal tembak
                                isShield = true;
                            }
                        }

                        // Jika tidak ada shield, maka bot akan menembak musuh tersebut
                        if (!isShield) {
                            playerAction.action = PlayerActions.FIRETORPEDOES;
                            playerAction.heading = getHeadingBetween(enemy.get(i));
                            torpedoItem -= 10;
                            updateItemBot();
                            this.playerAction = playerAction;
                            return;
                        }
                    }
                }
            } else {
                System.out.println("Yeah Menang");
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

    private boolean isTorpedoHeadingDangerous(GameObject bot, GameObject torpedoSalvo) {
        int headingBotToTorpedo = getHeadingBetween(torpedoSalvo);
        int headingTorpedoToBot = torpedoSalvo.currentHeading;
        int headingPlus180 = (headingBotToTorpedo + 180) % 360;

        int range1 = (headingPlus180 - 90) % 360;
        int range2 = (headingPlus180 + 90) % 360;

        if (headingPlus180 < 90) {
            return (headingTorpedoToBot >= range1 && headingTorpedoToBot <= 360)
                    || (headingTorpedoToBot >= 0 && headingTorpedoToBot <= range2);
        } else if (headingPlus180 < 180) {
            return (headingTorpedoToBot >= range1 && headingTorpedoToBot <= range2);
        } else if (headingPlus180 < 270) {
            return (headingTorpedoToBot >= range1 && headingTorpedoToBot <= range2);
        } else {
            return (headingTorpedoToBot >= range1 && headingTorpedoToBot <= 360)
                    || (headingTorpedoToBot >= 0 && headingTorpedoToBot <= range2);
        }

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

    private int getHeadingToCenterPoint() {
        var direction = toDegrees(Math.atan2(0 - bot.getPosition().y, 0 - bot.getPosition().x));
        return (direction + 360) % 360;
    }

    private int toDegrees(double v) {
        return (int) (v * (180 / Math.PI));
    }

    // Fungsi berapa jarak antara titik pusat map ((0,0)) dengan bot
    private double getDistanceFromCenter(GameObject bot) {
        return Math.sqrt(Math.pow(bot.getPosition().x, 2) + Math.pow(bot.getPosition().y, 2));
    }

    // Prosedur untuk menuju food atau superfood
    private int goToFood(List<GameObject> foodList, List<GameObject> superFood, GameObject bot,
            List<GameObject> listDangerousObject) {
        // Awalnya masukkan semua isi foodList, superFood, dan supernovaPickup ke dalam
        // listFood
        List<GameObject> listFood = new ArrayList<GameObject>();
        listFood.addAll(foodList);
        listFood.addAll(superFood);

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
                                .getSize() - 3) * 3) {
                    continue;
                }

                // Jika food berada dekat pada radius batas map, maka cari food selanjutnya
                if ((int) getDistanceBetween(listFood.get(i), bot) + bot.getSize()
                        + (int) getDistanceFromCenter(bot) > gameState.world.radius) {
                    continue;
                }

                // Kembalikan heading menuju food tersebut jika tidak ada benda berbahaya
                return getHeadingBetween(listFood.get(i));
            }
        }

        return -1;
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

    private boolean distancceOfEat(double distance) {
        if (distance <= 100) {
            return true;
        } else {
            return false;
        }
    }

}
