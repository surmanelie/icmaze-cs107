package ch.epfl.cs107.icmaze.actor.ui;

import ch.epfl.cs107.play.engine.actor.ImageGraphics;
import ch.epfl.cs107.play.io.ResourcePath;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public final class CoinCounterHUD {

    private int coinCount;

    public void setCoinCount(int coinCount) {
        this.coinCount = coinCount;
    }

    public void draw(Canvas canvas) {


        double scale = canvas.getScaledHeight() / 11.0;

        double hudWidth  = 4.0 * scale;
        double hudHeight = 2.0 * scale;


        double digitSize = 1 * scale;


        Vector center = canvas.getPosition();
        double viewWidth  = canvas.getScaledWidth();
        double viewHeight = canvas.getScaledHeight();

        Vector topLeft = center.add(
                new Vector(-viewWidth / 2.0, viewHeight / 2.0)
        );


        Vector hudAnchor = topLeft.add(
                new Vector(
                        hudWidth / 2.0 + scale,
                        -hudHeight / 2.0 - scale
                )
        );


        new ImageGraphics(
                ResourcePath.getSprite("icmaze/coinsDisplay"),
                (float) hudWidth,
                (float) hudHeight,
                new RegionOfInterest(0, 0, 64, 32),
                hudAnchor,
                1f,
                2000
        ).draw(canvas);


        final double GREY_X_CENTER = 70.0;
        final double GREY_Y_CENTER = 6.5; //

        double pxToWorldX = hudWidth  / 64.0;
        double pxToWorldY = hudHeight / 32.0;

        Vector digitsCenter = hudAnchor.add(
                new Vector(
                        (GREY_X_CENTER - 32.0) * pxToWorldX,
                        (16.0 - GREY_Y_CENTER) * pxToWorldY
                )
        );


        String text = String.valueOf(coinCount);
        double startX = -(text.length() * digitSize) / 2.0;

        for (int i = 0; i < text.length(); i++) {

            int digit = text.charAt(i) - '0';
            int rx, ry;

            if (digit == 0) {
                rx = 16;
                ry = 32;
            } else {
                int n = digit - 1;
                rx = (n % 4) * 16;
                ry = (n / 4) * 16;
            }

            new ImageGraphics(
                    ResourcePath.getSprite("icmaze/digits"),
                    (float) digitSize,
                    (float) digitSize,
                    new RegionOfInterest(rx, ry, 16, 16),
                    digitsCenter.add(new Vector(startX + i * digitSize, 0.0)),
                    1f,
                    2001
            ).draw(canvas);
        }
    }
}
