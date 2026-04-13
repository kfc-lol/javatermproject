package ca.bcit.comp2522.games.mygame;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Objects;

/**
 * Unified UI manager for the Maze Race game.
 * Handles the title scene (including rules) and the fully playable game scene.
 * All scene switching is performed internally via stage.setScene().
 * <p>
 * Styling is handled entirely through styles.css — no inline style strings
 * are applied to Button nodes, avoiding JavaFX Modena CSS conflicts.
 * <p>
 * GUI concurrency is achieved using JavaFX Task and Platform.runLater():
 * each player key press locks input, triggers a background Task that runs
 * one BFS step off the JavaFX Application Thread, then Platform.runLater()
 * updates the grid and unlocks input on the UI thread.
 * <p>
 * When one side finishes before the other, a Timeline drives the remaining
 * side until it also finishes, at which point the end overlay is shown.
 *
 * @author Kian Castro
 * @version 1.0
 */
public final class MazeUI
{
    public static final  int        FIRST_INDEX             = 0;
    public static final  int        CHECK_EVEN              = 0;
    private static final int        RULE_TITLE_COL          = 0;
    private static final int        RULE_DESC_COL           = 1;
    private static final int        EVEN_GRASS_TILE         = 2;
    // ---------------------------------------------------------------
    //  Scene dimensions
    // ---------------------------------------------------------------
    private static final int        SCENE_WIDTH_PX          = 660;
    private static final int        SCENE_HEIGHT_PX         = 720;
    // ---------------------------------------------------------------
    //  Font
    // ---------------------------------------------------------------
    private static final String     FONT_FAMILY             = "Georgia";
    private static final int        FONT_SIZE_TITLE_PT      = 48;
    private static final int        FONT_SIZE_SUBTITLE_PT   = 14;
    private static final int        FONT_SIZE_HEADING_PT    = 18;
    private static final int        FONT_SIZE_SUBHEADING_PT = 15;
    private static final int        FONT_SIZE_RULE_PT       = 13;
    private static final int        FONT_SIZE_BUTTON_PT     = 14;
    private static final int        FONT_SIZE_NOTIFIER_PT   = 12;
    private static final int        FONT_SIZE_PROGRESS_PT   = 13;
    private static final int        FONT_SIZE_TICK_PT       = 13;
    private static final int        FONT_SIZE_CELL_PT       = 16;
    private static final int        FONT_SIZE_RESULT_PT     = 24;
    // ---------------------------------------------------------------
    //  Colours (text only — backgrounds handled by CSS)
    // ---------------------------------------------------------------
    private static final String     COLOR_FOREST_DARK       = "#2d5a27";
    private static final String     COLOR_TEXT_PRIMARY      = "#1a1a1a";
    private static final String     COLOR_TEXT_MUTED        = "#555555";
    private static final String     COLOR_PLAYER            = "#1a6bb5";
    private static final String     COLOR_BFS               = "#c0392b";
    private static final String     COLOR_EXIT_TEXT         = "#f1c40f";
    // ---------------------------------------------------------------
    //  Layout
    // ---------------------------------------------------------------
    private static final int        SPACING_LARGE_PX        = 24;
    private static final int        SPACING_MEDIUM_PX       = 14;
    private static final int        SPACING_SMALL_PX        = 8;
    private static final int        PADDING_OUTER_PX        = 36;
    private static final int        PADDING_RULE_H_PX       = 12;
    private static final int        PADDING_RULE_V_PX       = 7;
    private static final int        PADDING_DIALOG_PX       = 36;
    private static final int        BUTTON_WIDTH_PX         = 180;
    private static final int        BUTTON_HEIGHT_PX        = 42;
    private static final int        SEED_FIELD_WIDTH_PX     = 260;
    private static final int        DIALOG_MAX_WIDTH_PX     = 420;
    private static final double     RULE_KEYWORD_MIN_WIDTH  = 68.0;
    // ---------------------------------------------------------------
    //  Maze grid
    // ---------------------------------------------------------------
    private static final int        GRID_COLS               = 10;
    private static final int        GRID_ROWS               = 10;
    private static final int        CELL_SIZE_PX            = 46;
    private static final int        GRID_GAP_PX             = 2;
    // ---------------------------------------------------------------
    //  Wall border
    // ---------------------------------------------------------------
    private static final int        WALL_THICKNESS_PX       = 4;
    private static final int        WALL_NONE_PX            = 0;
    private static final String     WALL_COLOR              = "#4a3728";
    // ---------------------------------------------------------------
    //  Animation
    // ---------------------------------------------------------------
    private static final double     NOTIFIER_FADE_MS        = 280.0;
    private static final double     NOTIFIER_VISIBLE        = 1.0;
    private static final double     NOTIFIER_HIDDEN         = 0.0;
    private static final double     OVERLAY_FADE_MS         = 220.0;
    private static final double     SOLO_BFS_INTERVAL_MS    = 120.0;
    // ---------------------------------------------------------------
    //  CSS stylesheet resource name
    // ---------------------------------------------------------------
    private static final String     STYLESHEET              = "styles.css";
    // ---------------------------------------------------------------
    //  CSS class names
    // ---------------------------------------------------------------
    private static final String     CSS_BTN_PRIMARY         = "button-primary";
    private static final String     CSS_BTN_SECONDARY       = "button-secondary";
    private static final String     CSS_CELL_HIDDEN         = "cell-hidden";
    private static final String     CSS_CELL_VISITED_LIGHT  = "cell-visited-light";
    private static final String     CSS_CELL_VISITED_DARK   = "cell-visited-dark";
    private static final String     CSS_CELL_PLAYER         = "cell-player";
    private static final String     CSS_CELL_EXIT           = "cell-exit";
    private static final String     CSS_NOTIFIER            = "notifier-label";
    private static final String     CSS_RULE_ROW            = "rule-row";
    private static final String     CSS_SEED_FIELD          = "seed-field";
    private static final String     CSS_DIALOG              = "dialog-box";
    private static final String     CSS_OVERLAY             = "overlay-background";
    // ---------------------------------------------------------------
    //  Rules content
    // ---------------------------------------------------------------
    private static final String[][] RULES                   =
        {
            {"Explore", "The maze is hidden. You see only cells you have visited."},
            {"Move", "Use W A S D or arrow keys. Each input counts as one tick."},
            {"Walls", "Moving into a wall reveals it — but still costs you a tick."},
            {"Exit", "You know where the exit is. You don't know the path."},
            {"BFS", "BFS searches alongside you — one step per tick. It hits walls too."},
            {"Progress", "Manhattan distance to the exit is shown for you and BFS."},
            {"Win", "Fewest ticks to reach the exit wins. Ties are possible."},
            };
    // ---------------------------------------------------------------
    //  Instance fields — stage and score are set once at construction
    // ---------------------------------------------------------------
    private final        Stage      stage;
    private final        MazeScore  score;

    // ---------------------------------------------------------------
    //  Game state — reset fresh each session via initGameState()
    // ---------------------------------------------------------------
    private Maze       maze;
    private MazeSolver solver;
    private Point      playerPosition;
    private Button[][] cellButtons;
    private boolean[]  inputLocked;
    private Label      playerDistanceLabel;
    private Label      bfsDistanceLabel;
    private Label      playerTickLabel;
    private Label      bfsTickLabel;
    private StackPane  gameRoot;
    private Timeline   soloBfsTimeline;
    private Label      notifierLabel;

    /**
     * Constructs a MazeUI bound to the given Stage and score tracker.
     *
     * @param stageParam the primary JavaFX Stage
     * @param scoreParam the shared tick score tracker
     */
    public MazeUI(final Stage stageParam,
                  final MazeScore scoreParam)
    {
        stage = stageParam;
        score = scoreParam;
    }

    // ---------------------------------------------------------------
    //  Public navigation
    // ---------------------------------------------------------------

    /**
     * Displays the title scene, which includes the game title, subtitle,
     * seed input, action buttons, and the full rules list.
     */
    public void showTitle()
    {
        stopSoloBfsTimeline();
        final Scene titleScene;
        titleScene = createTitleScene();
        stage.setScene(titleScene);
    }

    /**
     * Creates a new random maze and displays the game scene.
     */
    public void showGame()
    {
        startNewGame(MazeFactory.createRandom());
    }

    /**
     * Creates a maze from the given seed and displays the game scene.
     *
     * @param seed the maze seed for reproducible generation
     */
    public void showGameFromSeed(final long seed)
    {
        startNewGame(MazeFactory.createFromSeed(seed));
    }

    // ---------------------------------------------------------------
    //  Title scene
    // ---------------------------------------------------------------

    /*
     * Creates the title scene with heading, seed input, buttons, and rules.
     */
    private Scene createTitleScene()
    {
        final Label titleLabel;
        final Label subtitleLabel;
        final VBox  headerBox;
        final HBox  seedRow;
        final HBox  buttonRow;
        final Label rulesHeading;
        final VBox  rulesList;
        final VBox  root;
        final Scene scene;

        titleLabel = buildLabel("BEAT THE BFS", FONT_SIZE_TITLE_PT,
                                FontWeight.BOLD, COLOR_FOREST_DARK);
        titleLabel.setTextAlignment(TextAlignment.CENTER);

        subtitleLabel = buildLabel("Race the algorithm through the unknown forest",
                                   FONT_SIZE_SUBTITLE_PT,
                                   FontWeight.NORMAL, COLOR_TEXT_MUTED);
        subtitleLabel.setTextAlignment(TextAlignment.CENTER);

        headerBox = new VBox(SPACING_SMALL_PX, titleLabel, subtitleLabel);
        headerBox.setAlignment(Pos.CENTER);

        seedRow      = buildSeedRow();
        buttonRow    = buildTitleButtonRow();
        rulesHeading = buildLabel("How to Play", FONT_SIZE_HEADING_PT,
                                  FontWeight.BOLD, COLOR_FOREST_DARK);
        rulesList    = buildRulesList();

        root = new VBox(SPACING_LARGE_PX,
                        headerBox,
                        seedRow,
                        buttonRow,
                        buildSeparator(),
                        rulesHeading,
                        rulesList);

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(PADDING_OUTER_PX));

        scene = new Scene(root, SCENE_WIDTH_PX, SCENE_HEIGHT_PX);
        attachStylesheet(scene);
        return scene;
    }

    /*
     * Builds the seed input row.
     */
    private HBox buildSeedRow()
    {
        final TextField seedField;
        final Button    loadButton;
        final HBox      row;

        seedField = new TextField();
        seedField.setPromptText("Enter maze seed (optional)");
        seedField.setPrefWidth(SEED_FIELD_WIDTH_PX);
        seedField.getStyleClass().add(CSS_SEED_FIELD);

        loadButton = buildPrimaryButton("Load Seed");
        loadButton.setOnAction(e -> handleLoadSeed(seedField.getText().trim()));

        row = new HBox(SPACING_SMALL_PX, seedField, loadButton);
        row.setAlignment(Pos.CENTER);
        return row;
    }

    /*
     * Builds the New Game and Quit button row.
     */
    private HBox buildTitleButtonRow()
    {
        final Button newGameButton;
        final Button quitButton;
        final HBox   row;

        newGameButton = buildPrimaryButton("New Game");
        newGameButton.setOnAction(e -> showGame());

        quitButton = buildSecondaryButton("Quit");
        quitButton.setOnAction(e -> stage.close());

        row = new HBox(SPACING_MEDIUM_PX, newGameButton, quitButton);
        row.setAlignment(Pos.CENTER);
        return row;
    }

    /*
     * Builds the full list of rule rows.
     */
    private VBox buildRulesList()
    {
        final VBox list;
        list = new VBox(SPACING_SMALL_PX);
        list.setAlignment(Pos.CENTER_LEFT);

        for (final String[] rule : RULES)
        {
            list.getChildren().add(buildRuleRow(rule[ RULE_TITLE_COL ], rule[ RULE_DESC_COL ]));
        }

        return list;
    }

    /*
     * Builds one rule row with a keyword and description.
     */
    private HBox buildRuleRow(final String keyword,
                              final String description)
    {
        final Label keyLabel;
        final Label descLabel;
        final HBox  row;

        keyLabel = buildLabel(keyword + ":", FONT_SIZE_RULE_PT,
                              FontWeight.BOLD, COLOR_FOREST_DARK);
        keyLabel.setMinWidth(RULE_KEYWORD_MIN_WIDTH);

        descLabel = buildLabel(description, FONT_SIZE_RULE_PT,
                               FontWeight.NORMAL, COLOR_TEXT_PRIMARY);
        descLabel.setWrapText(true);

        row = new HBox(SPACING_SMALL_PX, keyLabel, descLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add(CSS_RULE_ROW);
        row.setPadding(new Insets(PADDING_RULE_V_PX, PADDING_RULE_H_PX,
                                  PADDING_RULE_V_PX, PADDING_RULE_H_PX));
        row.setMaxWidth(Double.MAX_VALUE);
        return row;
    }

    // ---------------------------------------------------------------
    //  Game session initialisation
    // ---------------------------------------------------------------

    /*
     * Stops any running Timeline, resets score and all game state,
     * then builds and shows a fresh game scene.
     */
    private void startNewGame(final Maze newMaze)
    {
        stopSoloBfsTimeline();
        score.reset();

        maze           = newMaze;
        solver         = new BFSSolver(maze); // substitution: MazeSolver reference holds BFSSolver
        playerPosition = maze.getStart();
        cellButtons    = new Button[ GRID_ROWS ][ GRID_COLS ];
        inputLocked    = new boolean[]{false};

        maze.markVisitedByPlayer(playerPosition.col(), playerPosition.row());

        final Scene gameScene;
        gameScene = createGameScene();
        stage.setScene(gameScene);
    }

    // ---------------------------------------------------------------
    //  Game scene construction
    // ---------------------------------------------------------------

    /*
     * Assembles the full game scene.
     */
    private Scene createGameScene()
    {
        final Label    gameHeading;
        final HBox     tickRow;
        final GridPane mazeGrid;
        final HBox     progressRow;
        final VBox     content;
        final Scene    scene;

        gameHeading   = buildLabel("Beat The BFS", FONT_SIZE_HEADING_PT,
                                   FontWeight.BOLD, COLOR_FOREST_DARK);
        tickRow       = buildTickRow();
        mazeGrid      = buildMazeGrid();
        notifierLabel = buildNotifierLabel();
        progressRow   = buildProgressRow();

        content = new VBox(SPACING_MEDIUM_PX,
                           gameHeading,
                           tickRow,
                           mazeGrid,
                           notifierLabel,
                           progressRow);

        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(PADDING_OUTER_PX));

        gameRoot = new StackPane(content);

        scene = new Scene(gameRoot, SCENE_WIDTH_PX, SCENE_HEIGHT_PX);
        attachStylesheet(scene);

        renderGrid();
        updateProgress();
        attachKeyHandler(scene);

        return scene;
    }

    /*
     * Builds the tick counter row.
     */
    private HBox buildTickRow()
    {
        playerTickLabel = buildLabel("Your ticks:", FONT_SIZE_TICK_PT,
                                     FontWeight.NORMAL, COLOR_PLAYER);

        bfsTickLabel = buildLabel("BFS ticks:", FONT_SIZE_TICK_PT,
                                  FontWeight.NORMAL, COLOR_BFS);

        final HBox row;
        row = new HBox(SPACING_LARGE_PX, playerTickLabel, bfsTickLabel);
        row.setAlignment(Pos.CENTER);
        return row;
    }

    /*
     * Builds the 10x10 grid of cell buttons.
     * Buttons use CSS class assignment only — no inline styles.
     */
    private GridPane buildMazeGrid()
    {
        final GridPane grid;
        grid = new GridPane();
        grid.setHgap(GRID_GAP_PX);
        grid.setVgap(GRID_GAP_PX);
        grid.setAlignment(Pos.CENTER);

        for (int row = 0; row < GRID_ROWS; row++)
        {
            for (int col = 0; col < GRID_COLS; col++)
            {
                final Button cell;
                cell = new Button();
                cell.setPrefSize(CELL_SIZE_PX, CELL_SIZE_PX);
                cell.setFocusTraversable(false);
                cell.getStyleClass().add(CSS_CELL_HIDDEN);
                cellButtons[ row ][ col ] = cell;
                grid.add(cell, col, row);
            }
        }

        return grid;
    }

    /*
     * Builds the notifier label, initially invisible.
     */
    private Label buildNotifierLabel()
    {
        final Label label;
        label = new Label("[ BFS is moving... ]");
        label.setFont(Font.font(FONT_FAMILY, FontWeight.NORMAL, FONT_SIZE_NOTIFIER_PT));
        label.getStyleClass().add(CSS_NOTIFIER);
        label.setOpacity(NOTIFIER_HIDDEN);
        return label;
    }

    /*
     * Builds the Manhattan distance progress row.
     */
    private HBox buildProgressRow()
    {
        playerDistanceLabel = buildLabel("Your distance to exit: --",
                                         FONT_SIZE_PROGRESS_PT,
                                         FontWeight.NORMAL, COLOR_PLAYER);

        bfsDistanceLabel = buildLabel("BFS distance to exit: --",
                                      FONT_SIZE_PROGRESS_PT,
                                      FontWeight.NORMAL, COLOR_BFS);

        final HBox row;
        row = new HBox(SPACING_LARGE_PX, playerDistanceLabel, bfsDistanceLabel);
        row.setAlignment(Pos.CENTER);
        return row;
    }

    // ---------------------------------------------------------------
    //  Grid rendering
    // ---------------------------------------------------------------

    /*
     * Renders the full grid based on current game state.
     */
    private void renderGrid()
    {
        final Point exitPoint;
        exitPoint = maze.getExit();

        for (int row = 0; row < GRID_ROWS; row++)
        {
            for (int col = 0; col < GRID_COLS; col++)
            {
                renderCell(col, row, exitPoint);
            }
        }
    }

    /*
     * Renders a single cell button at the given position.
     */
    private void renderCell(final int col,
                            final int row,
                            final Point exitPoint)
    {
        final Button    button;
        final Maze.Cell cell;
        final boolean   isPlayer;
        final boolean   isExit;

        button   = cellButtons[ row ][ col ];
        cell     = maze.getCell(col, row);
        isPlayer = playerPosition.col() == col
                   && playerPosition.row() == row;
        isExit   = exitPoint.col() == col
                   && exitPoint.row() == row;

        // Clear all cell CSS classes before reapplying
        button.getStyleClass().removeIf(s -> s.startsWith("cell-"));
        button.setText("");
        button.setStyle("");

        if (isPlayer)
        {
            applyPlayerStyle(button, cell);
        }
        else if (isExit)
        {
            applyExitStyle(button);
        }
        else if (cell.isVisitedByPlayer())
        {
            applyVisitedStyle(button, col, row, cell);
        }
        else
        {
            button.getStyleClass().add(CSS_CELL_HIDDEN);
        }
    }

    /*
     * Applies the player marker style.
     */
    private void applyPlayerStyle(final Button button,
                                  final Maze.Cell cell)
    {
        button.getStyleClass().add(CSS_CELL_PLAYER);
        button.setText("●");
        button.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, FONT_SIZE_CELL_PT));
        button.setTextFill(Color.web(COLOR_PLAYER));
        applyWallBorders(button, cell);
    }

    /*
     * Applies the exit marker style. Always visible.
     */
    private void applyExitStyle(final Button button)
    {
        button.getStyleClass().add(CSS_CELL_EXIT);
        button.setText("★");
        button.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, FONT_SIZE_CELL_PT));
        button.setTextFill(Color.web(COLOR_EXIT_TEXT));
    }

    /**
     * Applies the visited cell style with alternating grass colours.
     *
     * @param button input
     * @param col    column user occupies
     * @param row    row user occupies
     * @param cell   cell user occupies
     */
    private void applyVisitedStyle(final Button button,
                                   final int col,
                                   final int row,
                                   final Maze.Cell cell)
    {
        final String cssClass;
        if ((col + row) % EVEN_GRASS_TILE == CHECK_EVEN)
        {
            cssClass = CSS_CELL_VISITED_LIGHT;
        }
        else
        {
            cssClass = CSS_CELL_VISITED_DARK;
        }

        button.getStyleClass().add(cssClass);
        applyWallBorders(button, cell);
    }

    /**
     * Applies revealed wall borders programmatically.
     * CSS handles background; walls use inline border style only,
     * combining whichever sides have been revealed.
     * This avoids multiple CSS classes overwriting each other's
     * -fx-border-width property.
     *
     * @param button user input
     * @param cell   cell user occupies
     */
    private void applyWallBorders(final Button button,
                                  final Maze.Cell cell)
    {
        final int topPx;
        final int bottomPx;
        final int rightPx;
        final int leftPx;

        boolean draw;

        draw = false;

        if (cell.isRevealedNorth())
        {
            topPx = WALL_THICKNESS_PX;
            draw  = true;
        }
        else
        {
            topPx = WALL_NONE_PX;
        }

        if (cell.isRevealedSouth())
        {
            bottomPx = WALL_THICKNESS_PX;
            draw     = true;
        }
        else
        {
            bottomPx = WALL_NONE_PX;
        }

        if (cell.isRevealedEast())
        {
            rightPx = WALL_THICKNESS_PX;
            draw    = true;
        }
        else
        {
            rightPx = WALL_NONE_PX;
        }

        if (cell.isRevealedWest())
        {
            leftPx = WALL_THICKNESS_PX;
            draw   = true;
        }
        else
        {
            leftPx = WALL_NONE_PX;
        }

        if (draw)
        {
            final String borderStyle;
            borderStyle = "-fx-border-color: " + WALL_COLOR + ";"
                          + "-fx-border-width: "
                          + topPx + " " + rightPx + " "
                          + bottomPx + " " + leftPx + ";";
            button.setStyle(borderStyle);
        }
    }

    // ---------------------------------------------------------------
    //  Progress update
    // ---------------------------------------------------------------

    /*
     * Updates tick labels and Manhattan distance labels.
     */
    private void updateProgress()
    {
        final Point exitPoint;
        final int   playerDistance;
        final int   bfsDistance;

        exitPoint      = maze.getExit();
        playerDistance = playerPosition.manhattanDistanceTo(exitPoint);
        bfsDistance    = solver.getDisplayPosition().manhattanDistanceTo(exitPoint);

        playerDistanceLabel.setText("Your distance to exit: " + playerDistance);
        bfsDistanceLabel.setText("BFS distance to exit: " + bfsDistance);
        playerTickLabel.setText("Your ticks: " + score.getPlayerTicks());
        bfsTickLabel.setText("BFS ticks: " + score.getAlgorithmTicks());
    }

    // ---------------------------------------------------------------
    //  Key input and GUI concurrency
    // ---------------------------------------------------------------

    /*
     * Attaches the key handler to the scene.
     */
    private void attachKeyHandler(final Scene scene)
    {
        scene.setOnKeyPressed(event ->
                              {
                                  if (inputLocked[ FIRST_INDEX ] || score.hasPlayerFinished())
                                  {
                                      return;
                                  }

                                  final Direction direction;
                                  direction = keyCodeToDirection(event.getCode());

                                  if (direction == null)
                                  {
                                      return;
                                  }

                                  inputLocked[ FIRST_INDEX ] = true;

                                  try
                                  {
                                      processPlayerMove(direction);
                                  }
                                  catch (final InvalidMoveException e)
                                  {
                                      System.err.println("Invalid move attempted: " + e.getMessage());
                                      inputLocked[ FIRST_INDEX ] = false;
                                      return;
                                  }

                                  score.incrementPlayerTicks();

                                  renderGrid();
                                  updateProgress();

                                  final boolean playerJustFinished;
                                  playerJustFinished = playerPosition.equals(maze.getExit());

                                  if (playerJustFinished)
                                  {
                                      score.markPlayerFinished();
                                  }

                                  if (score.hasAlgorithmFinished())
                                  {
                                      // BFS already done — no more BFS steps needed
                                      inputLocked[ FIRST_INDEX ] = false;

                                      if (playerJustFinished)
                                      {
                                          showEndOverlay();
                                      }
                                      return;
                                  }

                                  showNotifier();
                                  runBfsStep(playerJustFinished);
                              });
    }

    /*
     * Processes one player move — advances position or reveals wall.
     * Throws InvalidMoveException if the direction is null, which would
     * indicate a bug in the key handler allowing a null direction through.
     */
    private void processPlayerMove(final Direction direction)
    {
        if (direction == null)
        {
            throw new InvalidMoveException(
                "Attempted to process a null direction.");
        }

        final int     col;
        final int     row;
        final boolean movePossible;

        col          = playerPosition.col();
        row          = playerPosition.row();
        movePossible = maze.isValidMove(col, row, direction);

        if (movePossible)
        {
            playerPosition = playerPosition.step(direction);
            maze.markVisitedByPlayer(playerPosition.col(),
                                     playerPosition.row());
        }
        else
        {
            maze.revealWall(col, row, direction);
        }
    }

    /*
     * Runs one BFS step on a background Task.
     * On completion, updates UI and checks win conditions.
     * If the player just finished and BFS has not, starts the solo Timeline.
     */
    private void runBfsStep(final boolean playerJustFinished)
    {
        final Task<Void> bfsTask;
        bfsTask = new Task<>()
        {
            @Override
            public Void call()
            {
                solver.step();
                return null;
            }
        };

        bfsTask.setOnSucceeded(e -> Platform.runLater(() ->
                                                      {
                                                          score.incrementAlgorithmTicks();

                                                          final boolean bfsJustFinished;
                                                          bfsJustFinished = solver.hasReachedExit();

                                                          if (bfsJustFinished)
                                                          {
                                                              score.markAlgorithmFinished();
                                                          }

                                                          renderGrid();
                                                          updateProgress();
                                                          hideNotifier();
                                                          inputLocked[ FIRST_INDEX ] = false;

                                                          if (bfsJustFinished && score.hasPlayerFinished())
                                                          {
                                                              showEndOverlay();
                                                          }
                                                          else if (playerJustFinished && !bfsJustFinished)
                                                          {
                                                              startSoloBfsTimeline();
                                                          }
                                                      }));

        bfsTask.setOnFailed(e -> Platform.runLater(() ->
                                                   {
                                                       hideNotifier();
                                                       inputLocked[ FIRST_INDEX ] = false;
                                                       System.err.println("BFS task failed: "
                                                                          + bfsTask.getException().getMessage());
                                                   }));

        final Thread bfsThread;
        bfsThread = new Thread(bfsTask);
        bfsThread.setDaemon(true);
        bfsThread.start();
    }

    // ---------------------------------------------------------------
    //  Solo BFS Timeline (runs when player finishes before BFS)
    // ---------------------------------------------------------------

    /*
     * Starts a Timeline that steps BFS automatically at a fixed interval
     * until it reaches the exit, then shows the end overlay.
     */
    private void startSoloBfsTimeline()
    {
        soloBfsTimeline = new Timeline(
            new KeyFrame(Duration.millis(SOLO_BFS_INTERVAL_MS), e ->
            {
                solver.step();
                score.incrementAlgorithmTicks();

                renderGrid();
                updateProgress();

                if (solver.hasReachedExit() || solver.isUnsolvable())
                {
                    score.markAlgorithmFinished();
                    stopSoloBfsTimeline();
                    showEndOverlay();
                }
            }));

        soloBfsTimeline.setCycleCount(Timeline.INDEFINITE);
        soloBfsTimeline.play();
    }

    /*
     * Stops the solo BFS Timeline if it is running.
     */
    private void stopSoloBfsTimeline()
    {
        if (soloBfsTimeline != null)
        {
            soloBfsTimeline.stop();
            soloBfsTimeline = null;
        }
    }

    // ---------------------------------------------------------------
    //  End overlay
    // ---------------------------------------------------------------

    /*
     * Displays the end-of-game overlay with result and navigation buttons.
     */
    private void showEndOverlay()
    {
        final Label     resultLabel;
        final Label     summaryLabel;
        final Label     playerTicksLabel;
        final Label     bfsTicksLabel;
        final Label     savedLabel;
        final Button    playAgainButton;
        final Button    saveSeedButton;
        final Button    menuButton;
        final HBox      buttonRow;
        final VBox      dialog;
        final StackPane overlay;

        resultLabel = buildLabel(score.getResultSummary(),
                                 FONT_SIZE_RESULT_PT,
                                 FontWeight.BOLD, COLOR_FOREST_DARK);
        resultLabel.setWrapText(true);
        resultLabel.setTextAlignment(TextAlignment.CENTER);
        resultLabel.setMaxWidth(DIALOG_MAX_WIDTH_PX);

        summaryLabel = buildLabel("Here's how it went:",
                                  FONT_SIZE_SUBHEADING_PT,
                                  FontWeight.NORMAL, COLOR_TEXT_MUTED);

        playerTicksLabel = buildLabel("Your ticks: " + score.getPlayerTicks(),
                                      FONT_SIZE_PROGRESS_PT,
                                      FontWeight.NORMAL, COLOR_PLAYER);

        bfsTicksLabel = buildLabel("BFS ticks: " + score.getAlgorithmTicks(),
                                   FONT_SIZE_PROGRESS_PT,
                                   FontWeight.NORMAL, COLOR_BFS);

        savedLabel = buildLabel("", FONT_SIZE_PROGRESS_PT,
                                FontWeight.NORMAL, COLOR_FOREST_DARK);

        playAgainButton = buildPrimaryButton("Play Again");
        playAgainButton.setOnAction(e -> showGame());

        saveSeedButton = buildSecondaryButton("Save Seed");
        saveSeedButton.setOnAction(e -> handleSaveSeed(maze.getSeed(), savedLabel));

        menuButton = buildSecondaryButton("Main Menu");
        menuButton.setOnAction(e -> showTitle());

        buttonRow = new HBox(SPACING_MEDIUM_PX,
                             playAgainButton, saveSeedButton, menuButton);
        buttonRow.setAlignment(Pos.CENTER);

        dialog = new VBox(SPACING_MEDIUM_PX,
                          resultLabel,
                          buildSeparator(),
                          summaryLabel,
                          playerTicksLabel,
                          bfsTicksLabel,
                          savedLabel,
                          buttonRow);

        dialog.setAlignment(Pos.CENTER);
        dialog.setPadding(new Insets(PADDING_DIALOG_PX));
        dialog.setMaxWidth(DIALOG_MAX_WIDTH_PX);
        dialog.getStyleClass().add(CSS_DIALOG);

        overlay = new StackPane(dialog);
        overlay.getStyleClass().add(CSS_OVERLAY);
        overlay.setOpacity(NOTIFIER_HIDDEN);

        gameRoot.getChildren().add(overlay);

        final FadeTransition fade;
        fade = new FadeTransition(Duration.millis(OVERLAY_FADE_MS), overlay);
        fade.setToValue(NOTIFIER_VISIBLE);
        fade.play();
    }

    // ---------------------------------------------------------------
    //  Notifier animation
    // ---------------------------------------------------------------

    /*
     * Fades the BFS notifier in.
     */
    private void showNotifier()
    {
        final FadeTransition fade;
        fade = new FadeTransition(Duration.millis(NOTIFIER_FADE_MS), notifierLabel);
        fade.setToValue(NOTIFIER_VISIBLE);
        fade.play();
    }

    /*
     * Fades the BFS notifier out.
     */
    private void hideNotifier()
    {
        final FadeTransition fade;
        fade = new FadeTransition(Duration.millis(NOTIFIER_FADE_MS), notifierLabel);
        fade.setToValue(NOTIFIER_HIDDEN);
        fade.play();
    }

    // ---------------------------------------------------------------
    //  Seed handling
    // ---------------------------------------------------------------

    /*
     * Parses the seed field and starts the appropriate game.
     * If the field is empty, attempts to load a saved seed from
     * maze_seed.txt via MazeFileManager.
     * If the field contains a number, uses it directly as the seed.
     * Falls back to a random maze if no valid seed can be determined.
     */
    private void handleLoadSeed(final String seedText)
    {
        if (seedText.isEmpty())
        {
            tryLoadSeedFromFile();
            return;
        }

        try
        {
            final long seed;
            seed = Long.parseLong(seedText);
            showGameFromSeed(seed);
        }
        catch (final NumberFormatException e)
        {
            showGame();
        }
    }

    /*
     * Saves the current maze seed to maze_seed.txt via MazeFileManager.
     * Updates the savedLabel to confirm success or report failure.
     */
    private void handleSaveSeed(final long seed,
                                final Label savedLabel)
    {
        try
        {
            MazeFileManager.saveSeed(seed);
            savedLabel.setText("Seed saved to maze_seed.txt");
        }
        catch (final MazeFileException e)
        {
            savedLabel.setText("Failed to save seed.");
            System.err.println("Seed save failed: " + e.getMessage());
        }
    }

    /*
     * Attempts to load a seed from maze_seed.txt via MazeFileManager.
     * Falls back to a random maze if the file does not exist or is invalid.
     */
    private void tryLoadSeedFromFile()
    {
        try
        {
            final long seed;
            seed = MazeFileManager.loadSeed();
            showGameFromSeed(seed);
        }
        catch (final MazeFileException e)
        {
            showGame();
        }
    }

    // ---------------------------------------------------------------
    //  Direction mapping
    // ---------------------------------------------------------------

    /*
     * Maps a KeyCode to a Direction. Returns null for non-directional keys.
     */
    private Direction keyCodeToDirection(final KeyCode code)
    {
        return switch (code)
        {
            case W, UP -> Direction.NORTH;
            case S, DOWN -> Direction.SOUTH;
            case D, RIGHT -> Direction.EAST;
            case A, LEFT -> Direction.WEST;
            default -> null;
        };
    }

    // ---------------------------------------------------------------
    //  Stylesheet
    // ---------------------------------------------------------------

    /*
     * Attaches the CSS stylesheet to the given scene.
     */
    private void attachStylesheet(final Scene scene)
    {
        final String stylesheet;
        stylesheet = Objects.requireNonNull(getClass().getResource(STYLESHEET)).toExternalForm();
        scene.getStylesheets().add(stylesheet);
    }

    // ---------------------------------------------------------------
    //  Shared UI builders
    // ---------------------------------------------------------------

    /*
     * Builds a Label with specified font, weight, and text colour.
     */
    private Label buildLabel(final String text,
                             final int fontSizePt,
                             final FontWeight weight,
                             final String colorHex)
    {
        final Label label;
        label = new Label(text);
        label.setFont(Font.font(FONT_FAMILY, weight, fontSizePt));
        label.setTextFill(Color.web(colorHex));
        return label;
    }

    /*
     * Builds a primary green-filled button using the CSS class only.
     */
    private Button buildPrimaryButton(final String text)
    {
        final Button button;
        button = new Button(text);
        button.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, FONT_SIZE_BUTTON_PT));
        button.setPrefWidth(BUTTON_WIDTH_PX);
        button.setPrefHeight(BUTTON_HEIGHT_PX);
        button.getStyleClass().add(CSS_BTN_PRIMARY);
        return button;
    }

    /*
     * Builds a secondary outlined button using the CSS class only.
     */
    private Button buildSecondaryButton(final String text)
    {
        final Button button;
        button = new Button(text);
        button.setFont(Font.font(FONT_FAMILY, FontWeight.NORMAL, FONT_SIZE_BUTTON_PT));
        button.setPrefWidth(BUTTON_WIDTH_PX);
        button.setPrefHeight(BUTTON_HEIGHT_PX);
        button.getStyleClass().add(CSS_BTN_SECONDARY);
        return button;
    }

    /*
     * Builds a full-width separator.
     */
    private Separator buildSeparator()
    {
        final Separator sep;
        sep = new Separator();
        sep.setMaxWidth(Double.MAX_VALUE);
        return sep;
    }
}