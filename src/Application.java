import com.javarush.engine.cell.Color;
import com.javarush.engine.cell.Game;

import java.util.ArrayList;
import java.util.List;

public class Application extends Game {
    private static final int SIDE = 9;
    private GameObject[][] gameField = new GameObject[SIDE][SIDE];
    private int countMinesOnField;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";
    private int countFlags;
    private boolean isGameStopped = false;
    private int countClosedTiles = SIDE * SIDE;
    private int score;

    @Override
    public void initialize() {
        setScreenSize(SIDE, SIDE);

        createGame();
    }

    private void createGame() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                boolean isMine = getRandomNumber(10) < 1;
                if (isMine) {
                    countMinesOnField++;
                }
                gameField[y][x] = new GameObject(x, y, isMine);
                setCellColor(x, y, Color.AQUA);
            }
        }
        countFlags = countMinesOnField;
        countMineNeighbors();
    }

    private List<GameObject> getNeighbors(GameObject gameObject) {
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue;
                }
                if (x < 0 || x >= SIDE) {
                    continue;
                }
                if (gameField[y][x] == gameObject) {
                    continue;
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }

    private void countMineNeighbors() {
        for (int y = 0; y < SIDE; y++) {
            for (int x = 0; x < SIDE; x++) {
                GameObject gb = gameField[x][y];
                if (!gb.isMine) {
                    for (GameObject gameObject : getNeighbors(gb)) {
                        if (gameObject.isMine) {
                            gb.countMineNeighbors++;
                        }
                    }
                }
                setCellValue(x, y, "");
            }
        }
    }

    private void openTile(int x, int y) {
        GameObject gb = gameField[y][x];
        System.out.println("is open = " + gb.isOpen);
        System.out.println("is flag = " + gb.isFlag);
        if (gb.isOpen) {
            return;
        } else if (gb.isFlag) {
            return;
        } else if (isGameStopped) {
            return;
        }
        gb.isOpen = true;
        countClosedTiles--;
        setCellColor(x, y, Color.ORANGE);
        if (gb.isMine) {
            setCellValueEx(x, y, Color.RED, MINE);
            gameOver();
            return;
        }

        if (gb.countMineNeighbors == 0) {
            setCellValue(x, y, "");
            for (GameObject gameObject : getNeighbors(gb)) {
                if (gameObject.isOpen || gameObject.isMine) {
                    continue;
                }
                openTile(gameObject.x, gameObject.y);
            }
        } else {
            setCellNumber(x, y, gb.countMineNeighbors);
        }
        score += 5;
        setScore(score);
        if (countClosedTiles == countMinesOnField && !gb.isMine) {
            win();
            return;
        }

    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        super.onMouseLeftClick(x, y);
        if (isGameStopped) {
            restart();
            return;
        }
        System.out.println("Left mouse click x = " + x + ", y = " + y);
        openTile(x, y);
    }

    private void markTile(int x, int y) {
        GameObject gb = gameField[y][x];
        System.out.println("is open = " + gb.isOpen);
        System.out.println("is flag = " + gb.isFlag);
        System.out.println("count flag = " + countFlags);
        if (gb.isOpen) {
            return;
        } else if (countFlags == 0 && !gb.isFlag) {
            return;
        } else if (isGameStopped) {
            return;
        }
        if (!gb.isFlag) {
            gb.isFlag = true;
            countFlags--;
            setCellValue(x, y, FLAG);
            setCellColor(x, y, Color.YELLOW);
            return;
        }
        gb.isFlag = false;
        countFlags++;
        setCellValue(x, y, "");
        setCellColor(x, y, Color.AQUA);


    }

    @Override
    public void onMouseRightClick(int x, int y) {
        super.onMouseRightClick(x, y);
        System.out.println("Right mouse click x = " + x + ", y = " + y);
        markTile(x, y);
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.GREEN, "Вы програли", Color.BLACK, 22);
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.GREEN, "Вы победили", Color.BLACK, 22);
    }

    private void restart() {
        isGameStopped = false;
        countClosedTiles = SIDE * SIDE;
        countMinesOnField = 0;
        score = 0;
        createGame();
        setScore(score);

    }
}
