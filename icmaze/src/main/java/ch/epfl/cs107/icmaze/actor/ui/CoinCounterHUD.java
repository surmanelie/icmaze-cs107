package ch.epfl.cs107.icmaze.actor.ui;

import ch.epfl.cs107.play.engine.actor.ImageGraphics;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public final class CoinCounterHUD {

    private int coinCount;

    public void setCoinCount(int coinCount) {
        this.coinCount = coinCount;
    }

    public void draw(Canvas canvas) {

        float scaleRatio = (float) canvas.getScaledHeight() / 11.0f;

        float hudWidth = 4f * scaleRatio;
        float hudHeight = 2f * scaleRatio;
        float digitSize = 0.5f * scaleRatio;

        Vector viewCenter = canvas.getPosition();
        float viewWidth = (float) canvas.getScaledWidth();
        float viewHeight = (float) canvas.getScaledHeight();

        Vector topLeft = viewCenter.add(new Vector(-viewWidth / 2, viewHeight / 2));

        Vector anchor = topLeft.add(
                new Vector(
                        (hudWidth / 2) + (0.5f * scaleRatio),
                        -(hudHeight / 2) - (0.5f * scaleRatio)));

        ImageGraphics coinIcon = new ImageGraphics(
                ch.epfl.cs107.play.io.ResourcePath.getSprite("icmaze/coinsDisplay"),
                hudWidth, hudHeight,
                new RegionOfInterest(0, 0, 64, 32),
                anchor, 1f, 2000f);
        coinIcon.draw(canvas);

        String countStr = String.valueOf(coinCount);
        float numberStartX = (hudWidth / 4) - ((countStr.length() * digitSize) / 2);

        for (int i = 0; i < countStr.length(); i++) {

            int digit = Character.getNumericValue(countStr.charAt(i));
            int regionX, regionY;

            if (digit == 0) {
                regionX = 16;
                regionY = 32;
            } else {
                int n = digit - 1;
                regionX = (n % 4) * 16;
                regionY = (n / 4) * 16;
            }

            ImageGraphics digitGraphics = new ImageGraphics(
                    ch.epfl.cs107.play.io.ResourcePath.getSprite("icmaze/digits"),
                    digitSize, digitSize,
                    new RegionOfInterest(regionX, regionY, 16, 16),
                    anchor.add(new Vector(numberStartX + i * digitSize, 0)),
                    1f, 2001f);
            digitGraphics.draw(canvas);
        }
    }
}
