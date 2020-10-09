package dev.aisandbox.client.scenarios.twisty.puzzles;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import dev.aisandbox.client.scenarios.twisty.NotExistentMoveException;
import dev.aisandbox.client.scenarios.twisty.TwistyPuzzle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.text.Font;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.junit.BeforeClass;
import org.junit.Test;

@Slf4j
public class GraphicsTest {

  static List<TwistyPuzzle> puzzleList = new ArrayList();

  @BeforeClass
  public static void setupPuzzleList() {
    // load font
    Font.loadFont(GraphicsTest.class.getResourceAsStream("/fonts/Hack-Regular.ttf"), 12.0);
    // load puzzles
    puzzleList.add(new Cube2x2x2());
    puzzleList.add(new Cube3x3x3());
    puzzleList.add(new Cube4x4x4());
    puzzleList.add(new Cube5x5x5());
    puzzleList.add(new Cube6x6x6());
    puzzleList.add(new Cube7x7x7());
    puzzleList.add(new Cube8x8x8());
    puzzleList.add(new Cube9x9x9());
    puzzleList.add(new Cube10x10x10());
  }

  @Test
  public void createDefaultImageTest() throws IOException {
    assertNotEquals("No Puzzles to test", 0, puzzleList.size());
    for (TwistyPuzzle puzzle : puzzleList) {
      puzzle.resetPuzzle();
      File out = new File("target/test-images/twisty/" + puzzle.getPuzzleName() + "/puzzle.png");
      out.getParentFile().mkdirs();
      BufferedImage image = puzzle.getStateImage();
      ImageIO.write(image, "PNG", out);
      assertTrue("Output file does not exist", out.isFile());
      assertTrue("Output file doesn't have data in it", out.length() > 0);
    }
  }

  @Test
  public void createMoveImageTest() throws IOException, NotExistentMoveException {
    assertNotEquals("No Puzzles to test", 0, puzzleList.size());
    for (TwistyPuzzle puzzle : puzzleList) {
      for (String move : puzzle.getMoveList()) {
        // create image showing the result of the move from the starting positions
        puzzle.resetPuzzle();
        File moveOutputFile =
            new File("target/test-images/twisty/" + puzzle.getPuzzleName() + "/" + move + ".png");
        moveOutputFile.getParentFile().mkdirs();
        puzzle.applyMove(move);
        BufferedImage image = puzzle.getStateImage();
        // test image is created
        assertNotNull("move image generated", image);
        assertNotEquals("Move image has zero width", 0, image.getWidth());
        assertNotEquals("Move image has zero height", 0, image.getHeight());
        //    ImageIO.write(image, "PNG", moveOutputFile);
        //    assertTrue("Move file written", moveOutputFile.isFile());
        // move icon image
        File iconOutputFile =
            new File(
                "target/test-images/twisty/" + puzzle.getPuzzleName() + "/" + move + "-icon.png");
        image = puzzle.getMoveImage(move);
        assertNotNull("Null Icon image", image);
        assertEquals("icon image width", 60, image.getWidth());
        assertEquals("icon image height", 100, image.getHeight());
        //    ImageIO.write(image, "PNG", iconOutputFile);
        //    assertTrue("icon image not written to file", iconOutputFile.isFile());
      }
    }
  }

  @Test
  public void createMoveSpriteSheet() throws IOException, NotExistentMoveException {
    for (TwistyPuzzle puzzle : puzzleList) {
      puzzle.resetPuzzle();
      File out = new File("target/test-images/twisty/" + puzzle.getPuzzleName() + "/sprites.png");
      out.getParentFile().mkdirs();
      BufferedImage image = puzzle.createMoveSpriteSheet();
      ImageIO.write(image, "PNG", out);
      assertTrue("Output file does not exist", out.isFile());
      assertTrue("Output file doesn't have data in it", out.length() > 0);
    }
  }
}
