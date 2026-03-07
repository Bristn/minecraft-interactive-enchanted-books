package net.bristn.lectern;

import net.minecraft.client.model.BookModel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public interface BookModelHelper {

    static void setBookAngles(BookModel book, float delta, float pageTurningSpeed, float nextPageTurningSpeed, float pageAngle, float nextPageAngle) {
        float f = Mth.lerp(delta, pageTurningSpeed, nextPageTurningSpeed);
        float g = Mth.lerp(delta, pageAngle, nextPageAngle);
        float j = Mth.clamp(Mth.frac(g + 0.25F) * 1.6F - 0.3F, 0.0F, 1.0F);
        float k = Mth.clamp(Mth.frac(g + 0.75F) * 1.6F - 0.3F, 0.0F, 1.0F);
        book.setupAnim(0.0F, j, k, f);
    }

    static float[] updateBookParameters(RandomSource random, float approximatePageAngle, float nextPageAngle, float nextPageTurningSpeed,
            float pageRotationSpeed, boolean force) {
        if (random.nextIntBetweenInclusive(1, 75) == 1 || force)
            do {
                approximatePageAngle += (float) (random.nextInt(4) - random.nextInt(4));
            } while (nextPageAngle <= approximatePageAngle + 1.0F && nextPageAngle >= approximatePageAngle - 1.0F);
        float pageAngle = nextPageAngle;
        float pageTurningSpeed = nextPageTurningSpeed;
        nextPageTurningSpeed += 0.2F;
        nextPageTurningSpeed = Mth.clamp(nextPageTurningSpeed, 0.0F, 1.0F);
        float f = (approximatePageAngle - nextPageAngle) * 0.4F;
        float g = 0.2F;
        f = Mth.clamp(f, -g, g);
        pageRotationSpeed += (f - pageRotationSpeed) * 0.9F;
        nextPageAngle += pageRotationSpeed;
        return new float[] { approximatePageAngle, nextPageAngle, pageAngle, pageTurningSpeed, nextPageTurningSpeed, pageRotationSpeed };
        // 1 2 3 4 5 6
    }

}
